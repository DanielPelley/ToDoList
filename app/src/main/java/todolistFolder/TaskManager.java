// Daniel Pelley and David Pelley
package todolistFolder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final List<Task> tasks = new ArrayList<>();
    private final Path filePath;
    private int nextId = 1;

    public TaskManager(String fileName) {
        this.filePath = Paths.get(fileName);
        loadFromFile();
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getCompletedTasks() {
        List<Task> completed = new ArrayList<>();
        for (Task t : tasks) {
            if (t.isCompleted()) {
                completed.add(t);
            }
        }
        return completed;
    }

    public Task addTask(String title, String description) {
        Task t = new Task(nextId++, title, description);
        tasks.add(t);
        saveToFile();
        return t;
    }

    public void updateTask(Task task, String newTitle, String newDescription) {
        task.setTitle(newTitle);
        task.setDescription(newDescription);
        saveToFile();
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
        saveToFile();
    }

    public void toggleCompleted(Task task) {
        task.toggleCompleted();
        saveToFile();
    }

    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            return; 
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            int maxId = 0;
            while ((line = reader.readLine()) != null) {
                //format: id|title|description|completed
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4) {
                    continue; 
                }
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String description = parts[2];
                boolean completed = Boolean.parseBoolean(parts[3]);
                Task t = new Task(id, title, description);
                t.setCompleted(completed);
                tasks.add(t);
                if (id > maxId) {
                    maxId = id;
                }
            }
            nextId = maxId + 1;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Task t : tasks) {
                // id|title|description|completed
                writer.write(t.getId() + "|" + safe(t.getTitle()) + "|" + safe(t.getDescription()) + "|" + t.isCompleted());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\n", " ");
    }
}
