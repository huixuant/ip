package peppa.commands;

import peppa.Storage;
import peppa.Task;
import peppa.TaskList;
import peppa.Todo;
import peppa.Ui;

/**
 * Represents a todo command that adds the todo to the tasklist.
 */
public class TodoCommand implements Command {
    public static final String COMMAND_WORD = "todo";
    public static final String ABBREVIATION = "T";
    public static final int DESC_INDEX = 5;
    private String taskDescription;

    public TodoCommand() {}

    public TodoCommand(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public void execute(TaskList taskList, Ui screen, Storage storage) {
        Task task = new Todo(taskDescription);
        taskList.addTask(task);
        Ui.displayAddTaskMessage(task);
        Ui.displayTaskSummary(taskList);
        storage.saveChanges(taskList);
    }

    @Override
    public boolean isExit() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TodoCommand) {
            return this.taskDescription.equals(((TodoCommand) obj).taskDescription);
        } else {
            return false;
        }
    }
}
