package peppa;

public abstract class Task {
    protected String name;
    protected boolean done;

    public Task() {

    }
    public Task(String name) {
        this.name = name;
        this.done = false;
    }

    public void setDone(boolean value) {
        this.done = value;
    }

    /**
     * Returns an icon that represents if the task is done.
     *
     * @return "X" if task is completed and " " otherwise.
     */
    public String getStatusIcon() {
        return (done ? "X" : " ");
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + name;
    }

    public abstract String toFormatString();
}
