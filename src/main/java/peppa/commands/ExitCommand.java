package peppa.commands;

import peppa.Storage;
import peppa.TaskList;
import peppa.Ui;

/**
 * Represents an exit command that ends the current chatbot session.
 */
public class ExitCommand implements Command {
    public static final String COMMAND_WORD = "bye";
    public ExitCommand() {

    }

    @Override
    public void execute(TaskList taskList, Ui screen, Storage storage) {
        screen.terminateSession();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
