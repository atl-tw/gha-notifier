package com.thoughtworks.gha.notifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.gha.notifier.gh.GH;
import com.thoughtworks.gha.notifier.model.Configuration;
import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;
import lombok.SneakyThrows;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class MonitorService {
  private static final Logger LOGGER = Logger.getLogger("ConfigurationService");
  private final File configFile = new File(System.getProperty("user.home"), ".gha-notifier.json");
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private final ObjectMapper mapper = new ObjectMapper();
  private GH gh;
  private Configuration configuration = new Configuration();
  private final Timer timer = new Timer(true);

  @SneakyThrows
  public MonitorService() {
    if(!configFile.exists()){
      saveConfig();
    } else {
      configuration = mapper.readValue(configFile, Configuration.class);
    }
    gh = new GH(mapper, configuration.getGithubExecutable());
    new HashSet<>(configuration.getLastStates().keySet())
        .forEach(k-> configuration.getLastStates().put(k,Configuration.State.SUCCESS));
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        configuration.getRepositories().stream().parallel().forEach(r->updateRepository(r));
        saveConfig();
      }
    },0);
    timer.schedule(new Task(), 3000);
  }

  void setGH(GH gh){
    this.gh = gh;
  }

  @SneakyThrows
  private synchronized void saveConfig(){
    LOGGER.info("Saving configuration");
    mapper.writeValue(configFile, configuration);
  }

  public void addRepository(File path) {
    var workflows = gh.listWorkflows(path);
    configuration.getRepositories().add(Repository.builder()
            .path(path.getAbsolutePath())
            .workflows(workflows)
        .build());
    saveConfig();
    pcs.firePropertyChange("repositories", null, getRepositories());
  }

  public void updateRepository(Repository repository) {
    repository.setWorkflows(gh.listWorkflows(new File(repository.getPath())));
  }

  public void removeRepositories(List<Repository> repositories) {
    configuration.getRepositories().removeAll(repositories);
    saveConfig();
    repositories.stream().map(Repository::getWorkflows).flatMap(List::stream).map(Workflow::getId).forEach(id-> {
      configuration.getLastStates().remove(id);
      configuration.getWorkflowsToNotify().remove(id);
    });

    pcs.firePropertyChange("repositories", null, getRepositories());
  }

  public List<Repository> getRepositories() {
    return configuration.getRepositories();
  }

  public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(property, listener);
  }

  public void addWorkflowToNotify(Workflow workflow) {
    configuration.getWorkflowsToNotify().add(workflow.getId());
    saveConfig();
    pcs.firePropertyChange("workflowsToNotify", null, configuration.getWorkflowsToNotify());
  }

  private void queryStates(){
    pcs.firePropertyChange("running", null, true);
    var workflowsToNotify = new ConcurrentHashMap<>();
    configuration.getRepositories()
        .forEach(repository -> repository.getWorkflows()
            .stream()
            .filter(this::workflowNotified)
            .parallel()
            .forEach(workflow-> {
              var state= gh.queryState(repository, workflow);
              LOGGER.info(workflow.getId()+" "+repository.getPath()+"/"+workflow.getPath()+" State: "+state);
              state.stream()
                  .filter(s ->s != configuration.getLastStates().get(workflow.getId()))
                  .findAny()
                  .ifPresent(s->{
                    workflowsToNotify.put(workflow, Boolean.TRUE);
                   configuration.getLastStates().put(workflow.getId(), s);
                  });
            }));
    saveConfig();
    this.pcs.firePropertyChange("notify", null, workflowsToNotify.keySet());
  }

  public Configuration.State lastState(Workflow workflow){
    return configuration.getLastStates().get(workflow.getId());
  }

  public boolean workflowNotified(Workflow w) {
    return configuration.getWorkflowsToNotify().contains(w.getId());
  }

  public void removeWorkflowToNotify(Workflow workflow) {
    configuration.getWorkflowsToNotify().remove(workflow.getId());
    configuration.getLastStates().remove(workflow.getId());
    saveConfig();
    pcs.firePropertyChange("workflowsToNotify", null, configuration.getWorkflowsToNotify());
  }

  @SuppressWarnings("unused")
  public void updateWorkflow(Workflow workflow) {
    saveConfig();
  }

  public Repository findRepository(Workflow w) {
    return configuration.getRepositories().stream().filter(r->r.getWorkflows().contains(w)).findFirst().orElse(null);
  }

  public void setGitHubExecutable(File selectedDirectory) {
    configuration.setGithubExecutable(selectedDirectory.getAbsolutePath());
    gh.setExecutable(selectedDirectory.getAbsolutePath());
    saveConfig();
  }

  public boolean checkGitHub() {
    return gh.checkGitHub();
  }

  public void browse(Repository repository) {
    gh.browse(repository.getPath());
  }

  public boolean anyFailures() {
    return this.configuration.getLastStates().values().stream().anyMatch(s->s == Configuration.State.FAILURE);
  }

  public List<Repository> getFailingRepositories() {
    var failingWorkflows = this.configuration.getLastStates()
        .entrySet().stream().filter(s->s.getValue() == Configuration.State.FAILURE)
        .map(Map.Entry::getKey).collect(Collectors.toSet());
    return configuration.getRepositories()
        .stream().filter(r->r.getWorkflows().stream().map(Workflow::getId).anyMatch(failingWorkflows::contains)).toList();
  }

  class Task extends TimerTask {
    @Override
    public void run() {
      try {
        queryStates();
      } catch (Exception e){
        LOGGER.log(Level.SEVERE,"Error querying states", e);
      }
      timer.schedule(new Task(), 60000);
    }
  }
}
