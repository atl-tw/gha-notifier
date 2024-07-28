package com.thoughtworks.gha.notifier;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import com.thoughtworks.gha.notifier.gh.GitHubException;
import com.thoughtworks.gha.notifier.model.Configuration;
import com.thoughtworks.gha.notifier.model.Repository;
import com.thoughtworks.gha.notifier.model.Workflow;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static java.util.Optional.ofNullable;

/**
 * Hello world!
 *
 */
public class App {
public static void main(String[] args) throws InterruptedException {
    System.setProperty("apple.awt.UIElement", "true");
    LafManager.install(new DarculaTheme());
    final CountDownLatch latch = new CountDownLatch(1);
    SwingUtilities.invokeLater(() -> {
        new JFXPanel();
        latch.countDown();
    });
    latch.await();
    SwingUtilities.invokeLater(App::new);
}
private JFrame frame = new JFrame("GitHub Notifier");
private MainForm mainForm = new MainForm();
private final MonitorService configurationService = new MonitorService();
private final Timer timer = new Timer(true);
private java.util.List<Workflow> selectedWorkflows;
private final ActionListener notifyCheckedListener = e1 -> {
    if (selectedWorkflows == null) {
        return;
    }
    selectedWorkflows.forEach(workflow -> {
        if (mainForm.getNotify().isSelected()) {
            configurationService.addWorkflowToNotify(workflow);
        } else {
            configurationService.removeWorkflowToNotify(workflow);
        }
    });
};
private final ActionListener mainBranchListener = e1 -> {
    if (selectedWorkflows == null) {
        return;
    }
    selectedWorkflows.forEach(workflow -> {
        workflow.setMainBranch(mainForm.getMainBranch().getText());
        configurationService.updateWorkflow(workflow);
    });
};

@SuppressWarnings("unchecked")
private App(){
    mainForm.getAdd().addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Git Repositories";
            }
        });
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            if(!ofNullable(selectedDirectory.list((dir, name) -> ".git".equals(name))).map(files -> files.length > 0).orElse(false)){
                JOptionPane.showMessageDialog(frame,
                    "Selected directory is not a git repository: " + selectedDirectory.getAbsolutePath());
                return;
            }
            try {
                configurationService.addRepository(selectedDirectory);
            } catch(GitHubException exception){
                JOptionPane.showMessageDialog(frame, exception.getMessage());
            }
        }
    });
    mainForm.getRemove().addActionListener(e->{
        var selected = mainForm.getRepositories().getSelectedValuesList();
        configurationService.removeRepositories(selected);
    });
    mainForm.getRepositories().addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
            return;
        }
        var selected = mainForm.getRepositories().getSelectedValuesList();
        if (selected == null || selected.size() > 1) {
            mainForm.getDetailsPanel().setVisible(false);
            return;
        }
        var repository = (Repository) selected.get(0);
        mainForm.getDetailsPanel().setVisible(true);
        mainForm.getRepositoryPath().setText("Workflows: "+
            repository.getPath().substring(repository.getPath().lastIndexOf('/') + 1));
        mainForm.getWorkflows().setListData(repository.getWorkflows().toArray(new Workflow[0]));
    });
    mainForm.getWorkflowConfig().setVisible(false);
    mainForm.getWorkflows().addListSelectionListener(e -> {
        mainForm.getNotify().removeActionListener(this.notifyCheckedListener);
        mainForm.getMainBranch().removeActionListener(this.mainBranchListener);
        if (e.getValueIsAdjusting()) {
            return;
        }
        var selected = mainForm.getWorkflows().getSelectedValuesList();
        if (selected == null) {
            mainForm.getWorkflowConfig().setVisible(false);
            return;
        }
        mainForm.getWorkflowConfig().setVisible(true);
        var notify = selected.stream().map(configurationService::workflowNotified).reduce((a, b) ->a || b ).orElse(false);
        mainForm.getNotify().setSelected(notify);
        if(selected.size() == 1){
            mainForm.getMainBranch().setText(selected.get(0).getMainBranch());
        }
        mainForm.getNotify().addActionListener(this.notifyCheckedListener);
        this.selectedWorkflows = selected;
    });

    var add = Toolkit.getDefaultToolkit().getImage(App.class.getResource("/add.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
    var remove = Toolkit.getDefaultToolkit().getImage(App.class.getResource("/remove.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
    mainForm.getAdd().setIcon(new ImageIcon(add));
    mainForm.getRemove().setIcon(new ImageIcon(remove));
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setSize(1024, 600);
    frame.setLocationRelativeTo(null);
    frame.setContentPane(mainForm);
    setupTray();
    configurationService.addPropertyChangeListener(
        "repositories", e -> SwingUtilities.invokeLater(
            ()->
                mainForm.getRepositories().setListData(configurationService.getRepositories().toArray())));
    mainForm.getRepositories().setListData(configurationService.getRepositories().toArray());
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            frame.setVisible(false);
        }
    });
    mainForm.getDetailsPanel().setVisible(false);
    var closeable = Toast.toast(ToastType.INFO, App.class.getResource("/build.png").toString(), "GitHub Notifier", "Started");
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(()->{
                try {
                    closeable.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }});

        }
    }, 3000);
    configurationService.addPropertyChangeListener("notify", e->{
        var workflows = (Set<Workflow>) e.getNewValue();
        workflows.forEach(w-> {
            var state = configurationService.lastState(w);
            var repository = configurationService.findRepository(w);
            if(state == Configuration.State.SUCCESS){
                var success = Toast.toast(ToastType.INFO, App.class.getResource("/build.png").toString(), "GitHub Notifier",
                    "Workflow "+w.getName()+" on "+ repository.getPath().substring(repository.getPath().lastIndexOf('/') +1)+" is now successful.");
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(()->{
                            try {
                                success.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }});

                    }
                }, 3000);
            } else {
              //noinspection resource
              Toast.toast(ToastType.ERROR, App.class.getResource("/build.png").toString(), "GitHub Notifier",
                    "Workflow "+w.getName()+" on "+ repository.getPath().substring(repository.getPath().lastIndexOf('/') +1)+" is failing.");
            }
        });
    });

}

private void showWindow(){
    frame.setVisible(true);
    frame.setExtendedState(JFrame.NORMAL);
    frame.toFront();
    frame.repaint();
}
private void setupTray() {
    if (SystemTray.isSupported()) {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(App.class.getResource("/build.png"));
        PopupMenu popup = new PopupMenu();
        TrayIcon trayIcon = new TrayIcon(image, "System Tray Example", popup);
        trayIcon.setImageAutoSize(true);
        MenuItem restoreItem = new MenuItem("Show Configuration");
        restoreItem.addActionListener(e -> SwingUtilities.invokeLater(this::showWindow));

        MenuItem exitItem = new MenuItem("Quit");
        exitItem.addActionListener(e -> System.exit(0));

        popup.add(restoreItem);
        popup.add(exitItem);

        trayIcon.addActionListener(e -> showWindow());

        try {
            tray.add(trayIcon);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    } else {
        System.err.println("System tray not supported!");
    }
}
}