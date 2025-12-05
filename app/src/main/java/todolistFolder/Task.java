package todolistFolder;
public class Task {
    private int id;
    private String title;
    private String description;
    private boolean completed;

    public Task(int newId, String newTitle, String newDescription) {
        id = newId;
        title = newTitle;
        description = newDescription;
        completed = false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    // Setters / actions
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void toggleCompleted() {
        this.completed = !this.completed;
    }

    @Override
    public String toString() {
        // This is what shows in the JList
        return (completed ? "(Done) " : "(In Progress) ") + title;
    }
}
