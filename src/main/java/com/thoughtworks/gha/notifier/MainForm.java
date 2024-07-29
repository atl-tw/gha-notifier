/*
 * Created by JFormDesigner on Sat Jul 27 21:16:02 EDT 2024
 */

package com.thoughtworks.gha.notifier;

import java.awt.*;
import lombok.Getter;

import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author thw8305
 */
@Getter
public class MainForm extends JPanel {
    public MainForm() {
        initComponents();
        var addImage = Toolkit.getDefaultToolkit().getImage(MainForm.class.getResource("/add.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        var removeImage = Toolkit.getDefaultToolkit().getImage(MainForm.class.getResource("/remove.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        var ghImage = Toolkit.getDefaultToolkit().getImage(MainForm.class.getResource("/github.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        this.getAdd().setIcon(new ImageIcon(addImage));
        this.getRemove().setIcon(new ImageIcon(removeImage));
        this.getGh().setIcon(new ImageIcon(ghImage));
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

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JToolBar toolBar1;
    private JButton add;
    private JButton remove;
    private JButton gh;
    private JScrollPane scrollPane1;
    private JList<com.thoughtworks.gha.notifier.model.Repository> repositories;
    private JPanel detailsPanel;
    private JLabel repositoryPath;
    private JScrollPane scrollPane2;
    private JList<com.thoughtworks.gha.notifier.model.Workflow> workflows;
    private JPanel workflowConfig;
    private JCheckBox notify;
    private JTextField mainBranch;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
