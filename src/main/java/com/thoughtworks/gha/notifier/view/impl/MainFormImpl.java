/*
 * Created by JFormDesigner on Sat Jul 27 21:16:02 EDT 2024
 */

package com.thoughtworks.gha.notifier.view.impl;

import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;
import com.thoughtworks.gha.notifier.view.MainForm;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author thw8305
 */
public class MainFormImpl extends JPanel implements MainForm {
  @Setter
  private transient Runnable onAddRepository;
  @Setter
  private transient Runnable onRemoveRepositories;
  @Setter
  private transient Runnable onGitHubSelect;
  @Setter
  private transient Runnable onNotifyChanged;
  @Setter
  private transient Consumer<String> onMainBranchChanged;
  @Setter
  private transient Runnable onRepositoriesSelected;
  @Setter
  private transient Runnable onWorkflowSelected;

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JToolBar toolBar1;
    private JButton add;
    private JButton remove;
    private JButton gh;
    private JScrollPane scrollPane1;
    private JList<Repository> repositories;
    private JPanel detailsPanel;
    private JLabel repositoryPath;
    private JScrollPane scrollPane2;
    private JList<Workflow> workflows;
    private JPanel workflowConfig;
    private JCheckBox notify;
    private JTextField mainBranch;
    private JLabel label1;
  // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

  public MainFormImpl() {
    initComponents();
    LightDark.register("/add-", ".png", 16, 16, image-> this.add.setIcon(new ImageIcon(image)));
    LightDark.register("/remove-", ".png", 16, 16, image-> this.remove.setIcon(new ImageIcon(image)));
    LightDark.register("/github-", ".png", 16, 16, image-> this.gh.setIcon(new ImageIcon(image)));

    this.add.addActionListener(e -> {
      if(onAddRepository != null)
        onAddRepository.run();
    });
    this.remove.addActionListener(e -> {
      if(onRemoveRepositories != null)
        onRemoveRepositories.run();
    });
    this.gh.addActionListener(e ->{
      if(onGitHubSelect != null) onGitHubSelect.run();
    });
    this.notify.addActionListener(e -> {
      if (onNotifyChanged != null) onNotifyChanged.run();
    });
    this.mainBranch.addActionListener(e -> {
      if (onMainBranchChanged != null) onMainBranchChanged.accept(mainBranch.getText());
    });
    this.repositories.addListSelectionListener(e -> {
      if (e.getValueIsAdjusting()) {
        return;
      }
      if (onRepositoriesSelected != null) onRepositoriesSelected.run();
    });
   this.repositories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.workflows.addListSelectionListener(e -> {
      if (e.getValueIsAdjusting()) {
        return;
      }
      if (onWorkflowSelected != null) onWorkflowSelected.run();
    });
  }

  public boolean isNotifyChecked(){
    return this.notify.isSelected();
  }

  public void setRepositories(List<Repository> repositories){
    SwingUtilities.invokeLater(() -> this.repositories.setListData(repositories.toArray(new Repository[0])));
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        toolBar1 = new JToolBar();
        add = new JButton();
        remove = new JButton();
        gh = new JButton();
        scrollPane1 = new JScrollPane();
        repositories = new JList<>();
        detailsPanel = new JPanel();
        repositoryPath = new JLabel();
        scrollPane2 = new JScrollPane();
        workflows = new JList<>();
        workflowConfig = new JPanel();
        notify = new JCheckBox();
        mainBranch = new JTextField();
        label1 = new JLabel();

        //======== this ========

        //======== panel1 ========
        {

            //======== toolBar1 ========
            {
                toolBar1.setFloatable(false);

                //---- add ----
                add.setToolTipText("Add Repository");
                toolBar1.add(add);

                //---- remove ----
                remove.setToolTipText("Remove Selected Repositories");
                toolBar1.add(remove);
                toolBar1.add(gh);
            }

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(repositories);
            }

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addComponent(toolBar1, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPane1)
                        .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(toolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== detailsPanel ========
        {

            //---- repositoryPath ----
            repositoryPath.setText("text");
            repositoryPath.setFont(new Font("Inter", Font.BOLD, 24));

            //======== scrollPane2 ========
            {
                scrollPane2.setViewportView(workflows);
            }

            //======== workflowConfig ========
            {

                //---- notify ----
                notify.setText("Notify Failures");

                //---- label1 ----
                label1.setText("Main Branch");

                GroupLayout workflowConfigLayout = new GroupLayout(workflowConfig);
                workflowConfig.setLayout(workflowConfigLayout);
                workflowConfigLayout.setHorizontalGroup(
                    workflowConfigLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, workflowConfigLayout.createSequentialGroup()
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(notify, GroupLayout.PREFERRED_SIZE, 258, GroupLayout.PREFERRED_SIZE))
                        .addGroup(workflowConfigLayout.createSequentialGroup()
                            .addGroup(workflowConfigLayout.createParallelGroup()
                                .addGroup(workflowConfigLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 237, GroupLayout.PREFERRED_SIZE))
                                .addComponent(mainBranch, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE))
                            .addGap(0, 0, Short.MAX_VALUE))
                );
                workflowConfigLayout.setVerticalGroup(
                    workflowConfigLayout.createParallelGroup()
                        .addGroup(workflowConfigLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(notify)
                            .addGap(14, 14, 14)
                            .addComponent(label1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(mainBranch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(715, Short.MAX_VALUE))
                );
            }

            GroupLayout detailsPanelLayout = new GroupLayout(detailsPanel);
            detailsPanel.setLayout(detailsPanelLayout);
            detailsPanelLayout.setHorizontalGroup(
                detailsPanelLayout.createParallelGroup()
                    .addGroup(detailsPanelLayout.createSequentialGroup()
                        .addGroup(detailsPanelLayout.createParallelGroup()
                            .addGroup(detailsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(repositoryPath, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(detailsPanelLayout.createSequentialGroup()
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(workflowConfig, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
            );
            detailsPanelLayout.setVerticalGroup(
                detailsPanelLayout.createParallelGroup()
                    .addGroup(detailsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(repositoryPath, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(detailsPanelLayout.createParallelGroup()
                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
                            .addComponent(workflowConfig, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(707, Short.MAX_VALUE)))
                .addGroup(layout.createSequentialGroup()
                    .addGap(390, 390, 390)
                    .addComponent(detailsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap()))
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(detailsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
  }


  public void showDetailsPane() {
    detailsPanel.setVisible(true);
  }

  public void hideDetailsPane() {
    detailsPanel.setVisible(false);
  }

  public void setTitle(String s) {
    repositoryPath.setText(s);
  }

  public void hideWorkflowConfig() {
    workflowConfig.setVisible(false);
  }

  public List<Repository> getSelectedRepositories() {
    return repositories.getSelectedValuesList();
  }

  public void setWorkflows(List<Workflow> workflows) {
    SwingUtilities.invokeLater(() -> this.workflows.setListData(workflows.toArray(new Workflow[0])));
  }

  public void showWorkflowConfig() {
    workflowConfig.setVisible(true);
  }

  public java.util.List<Workflow> getSelectedWorkflows() {
    return workflows.getSelectedValuesList();
  }

  public void setNotifyChecked(boolean notify) {
    this.notify.setSelected(notify);
  }

  public void setMainBranch(String mainBranch) {
    this.mainBranch.setText(mainBranch);
  }
}
