package com.thoughtworks.gha.notifier.gh;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.gha.notifier.model.Configuration;
import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GH {

  private static final Logger LOGGER = Logger.getLogger("GH");
  public static final String RECENT_RUNS = "Recent runs"+System.lineSeparator();
  private final ObjectMapper mapper;
  @Setter
  private String executable;

  public GH(ObjectMapper mapper, String githubExecutable) {
    this.mapper = mapper;
    this.executable = githubExecutable;
  }


  public List<Workflow> listWorkflows(File path) {
    try {
      LOGGER.info("Listing workflows in " + path);
      var proc = new ProcessBuilder(executable, "workflow", "list", "--json", "id,name,path,state")
          .directory(path).start();
      var result = new String(proc.getInputStream().readAllBytes());
      var error = new String(proc.getErrorStream().readAllBytes());
      if (!error.isBlank()) {
        LOGGER.severe("Error listing workflows: " + error);
        throw new GitHubException(error);
      }
      LOGGER.info("Found workflows in " + path + " " + result);
      return mapper.readValue(result, new TypeReference<>() {
      });
    } catch(Exception e){
      LOGGER.log(Level.SEVERE, "Error listing workflows", e);
      return Collections.emptyList();
    }
  }

  @SneakyThrows
  public Configuration.State queryState(Repository repository, Workflow workflow){
    var proc = new ProcessBuilder(executable, "workflow", "view", workflow.getId())
        .directory(new File(repository.getPath())).start();
    var result = new String(proc.getInputStream().readAllBytes());
    var executions = parseExecutions(result);
    var error = new String(proc.getErrorStream().readAllBytes());
    if (!error.isBlank()) {
      LOGGER.severe("Error querying state: " + error);
      throw new GitHubException(error);
    }
    return executions.stream().filter(e-> e.getBranch().equals(workflow.getMainBranch())).findFirst()
        .map(e-> e.getResult().equals("failure") ? Configuration.State.FAILURE : Configuration.State.SUCCESS)
        .orElse(null);
  }

  private List<Execution> parseExecutions(String result){
    result = result.substring(result.indexOf(RECENT_RUNS) + RECENT_RUNS.length());
    result = result.substring(0, result.indexOf(System.lineSeparator()+System.lineSeparator()));
    var lines = result.split(System.lineSeparator());
    return Arrays.asList(lines).stream().map(this::parseLine).filter(Objects::nonNull).toList();
  }

  private Execution parseLine(String line){
    try {
      var parts = line.split("\\s+");
      var execution = new Execution();
      execution.setState(parts[0]);
      execution.setResult(parts[1]);
      execution.setTimeStamp(parts[parts.length - 1]);
      execution.setDuration(parts[parts.length - 2]);
      execution.setTrigger(parts[parts.length - 3]);
      execution.setBranch(parts[parts.length - 4]);
      var message = new StringJoiner(" ");
      Arrays.asList(Arrays.copyOfRange(parts, 2, parts.length - 4)).forEach(message::add);
      execution.setMessage(message.toString());
      return execution;
    } catch (Exception e){
      LOGGER.severe("Error parsing line: "+line);
      return null;
    }
  }

  public boolean checkGitHub() {
    try {
      var proc = new ProcessBuilder(executable, "--help").start();
      var error = new String(proc.getErrorStream().readAllBytes());
      if (!error.isBlank()) {
        LOGGER.severe("Error listing workflows: " + error);
      }
      return true;
    } catch(Exception e){
      LOGGER.log(Level.SEVERE, "Error checking GitHub", e);
      return false;
    }
  }


  @Data
  static class Execution {
    private String message;
    private String state;
    private String result;
    private String timeStamp;
    private String duration;
    private String trigger;
    private String branch;

  }

}
