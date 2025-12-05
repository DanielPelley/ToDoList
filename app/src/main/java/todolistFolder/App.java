// Daniel Pelley and David Pelley
package todolistFolder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class App extends JFrame {

    private final TaskManager taskManager;

    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton toggleCompleteButton;
    private JButton showAllButton;
    private JButton showCompletedButton;

    private boolean showingCompletedOnly = false;

    public App() {
        taskManager = new TaskManager("tasks.txt");

        setTitle("My To-Do List");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 450);
        setLayout(new BorderLayout());

        createComponents();
        buildLayout();
        wireEvents();

        refreshTaskList();

        setLocationRelativeTo(null); // center on screen
        setVisible(true);
    }

    private void createComponents() {
        // Task list
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Input fields
        titleField = new JTextField();
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // Buttons
        addButton = new JButton("Add Task");
        updateButton = new JButton("Update Task");
        deleteButton = new JButton("Delete Task");
        toggleCompleteButton = new JButton("Toggle Complete");
        showAllButton = new JButton("Show All");
        showCompletedButton = new JButton("Show Completed");
    }

    private void buildLayout() {
        // Left side: list of tasks
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Tasks"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(taskList), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(250, 0));

        // Right side: editor + buttons
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Top: fields
        JPanel fieldsPanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(new JLabel("Title:"), BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        fieldsPanel.add(titlePanel, BorderLayout.NORTH);
        fieldsPanel.add(descPanel, BorderLayout.CENTER);

        // Middle: main action buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(toggleCompleteButton);

        // Bottom: filter buttons
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filterPanel.add(showAllButton);
        filterPanel.add(showCompletedButton);

        rightPanel.add(fieldsPanel, BorderLayout.NORTH);
        rightPanel.add(buttonsPanel, BorderLayout.CENTER);
        rightPanel.add(filterPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void wireEvents() {
        addButton.addActionListener(e -> handleAddTask());
        updateButton.addActionListener(e -> handleUpdateTask());
        deleteButton.addActionListener(e -> handleDeleteTask());
        toggleCompleteButton.addActionListener(e -> handleToggleCompleted());
        showAllButton.addActionListener(e -> {
            showingCompletedOnly = false;
            refreshTaskList();
        });
        showCompletedButton.addActionListener(e -> {
            showingCompletedOnly = true;
            refreshTaskList();
        });

        // When user clicks a task, load its data into fields
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Task selected = taskList.getSelectedValue();
                if (selected != null) {
                    titleField.setText(selected.getTitle());
                    descriptionArea.setText(selected.getDescription());
                }
            }
        });
    }

    private void refreshTaskList() {
        taskListModel.clear();

        List<Task> tasks = showingCompletedOnly ? taskManager.getCompletedTasks() : taskManager.getAllTasks();

        for (Task task : tasks) {
            taskListModel.addElement(task);
        }
    }

    private void handleAddTask() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a title.", "Missing Title", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task t = taskManager.addTask(title, description);
        titleField.setText("");
        descriptionArea.setText("");
        showingCompletedOnly = false;
        refreshTaskList();
        taskList.setSelectedValue(t, true);
    }

    private void handleUpdateTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Pick a task to update first.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newTitle = titleField.getText().trim();
        String newDescription = descriptionArea.getText().trim();

        if (newTitle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a title.", "Missing Title", JOptionPane.ERROR_MESSAGE);
            return;
        }

        taskManager.updateTask(selected, newTitle, newDescription);
        refreshTaskList();
        taskList.setSelectedValue(selected, true);
    }

    private void handleDeleteTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Pick a task to delete first.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this task?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            taskManager.deleteTask(selected);
            titleField.setText("");
            descriptionArea.setText("");
            refreshTaskList();
        }
    }

    private void handleToggleCompleted() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Pick a task to toggle first.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        taskManager.toggleCompleted(selected);
        refreshTaskList();
        taskList.setSelectedValue(selected, true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
