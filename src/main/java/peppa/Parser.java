package peppa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import peppa.commands.Command;
import peppa.commands.DeadlineCommand;
import peppa.commands.DeleteCommand;
import peppa.commands.EventCommand;
import peppa.commands.ExitCommand;
import peppa.commands.IncorrectCommand;
import peppa.commands.InvalidCommand;
import peppa.commands.ListCommand;
import peppa.commands.MarkCommand;
import peppa.commands.TodoCommand;
import peppa.commands.UnmarkCommand;

/**
 * Represents a parser that converts user input into Commands that can be executed.
 */
public class Parser {
    public Parser() {

    }

    /**
     * Parses the given command into one of Command objects based on the first word.
     * Manages high-level switching logic only. Actual parsing is handled by individual parsing methods.
     *
     * @param fullCommand Command given by the user.
     * @return Command object corresponding to the user request.
     */
    public static Command parseCommand(String fullCommand) {
        String[] args = fullCommand.split(" ");
        String commandType = args[0];
        switch (commandType) {
        case ListCommand.COMMAND_WORD:
            return new ListCommand();
        case MarkCommand.COMMAND_WORD:
            return parseMarkCommand(args);
        case UnmarkCommand.COMMAND_WORD:
            return parseUnmarkCommand(args);
        case DeleteCommand.COMMAND_WORD:
            return parseDeleteCommand(args);
        case FindCommand.COMMAND_WORD:
            return new FindCommand(args[1]);
        case TodoCommand.COMMAND_WORD:
            return parseTodoCommand(fullCommand);
        case DeadlineCommand.COMMAND_WORD:
            return parseDeadlineCommand(fullCommand);
        case EventCommand.COMMAND_WORD:
            return parseEventCommand(fullCommand);
        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();
        default:
            return new InvalidCommand();
        }
    }

    /**
     * Parses an add deadline request into a DeadlineCommand object.
     *
     * @param command Command given by the user.
     * @return DeadlineCommand object if command was parsed successfully, and IncorrectCommand object otherwise.
     */
    public static Command parseDeadlineCommand(String command) {
        try {
            int paramIndex = DeadlineCommand.getParameterIndex(command);

            String desc = DeadlineCommand.getParameterValue(command, DeadlineCommand.DESC_INDEX, paramIndex - 1);
            String deadline = DeadlineCommand.getParameterValue(command, paramIndex + 4, command.length());

            LocalDateTime dueDate = LocalDateTime.parse(deadline, DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm"));
            return new DeadlineCommand(desc, dueDate);
        } catch (PeppaException e) {
            Ui.displayMessage(e.getMessage());
        } catch (DateTimeParseException e) {
            Ui.displayMessage("Boink! Peppa could not process the request. "
                    + "Please enter the date in dd/MM/yyyy and time in HHmm (ie. 24h) format respectively.");
        }
        return new IncorrectCommand();
    }

    /**
     * Parses a delete task request into a DeleteCommand object.
     *
     * @param args Array of arguments obtained from splitting the given command around a whitespace character.
     * @return DeleteCommand object if command was parsed successfully, and IncorrectCommand object otherwise.
     */
    public static Command parseDeleteCommand(String[] args) {
        try {
            String idxStr = args[1];
            return new DeleteCommand(Integer.parseInt(idxStr) - 1);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            Ui.displayMessage("Boink! Peppa could not process the request. "
                    + "Please enter a valid integer and try again.");
            return new IncorrectCommand();
        }
    }

    /**
     * Parses an add event request into an EventCommand object.
     *
     * @param command Command given by the user.
     * @return EventCommand object if command was parsed successfully, and IncorrectCommand object otherwise.
     */
    public static Command parseEventCommand(String command) {
        try {
            int fromIndex = EventCommand.getParameterIndex(command, "from");
            int toIndex = EventCommand.getParameterIndex(command, "to");

            String desc = EventCommand.getParameterValue(command, EventCommand.DESC_INDEX, fromIndex - 1);
            String startDate = EventCommand.getParameterValue(command, fromIndex + 6, toIndex - 1);
            String endDate = EventCommand.getParameterValue(command, toIndex + 4, command.length());

            LocalDate from = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalDate to = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new EventCommand(desc, from, to);
        } catch (PeppaException e) {
            Ui.displayMessage(e.getMessage());
        } catch (DateTimeParseException e) {
            Ui.displayMessage("Boink! Peppa could not process the request. "
                    + "Please enter the date in dd/MM/yyyy format and try again.");
        }
        return new IncorrectCommand();
    }

    /**
     * Parses a line in the file and adds the task to the Tasklist provided.
     *
     * @param line Entry in the file that corresponds to a task.
     * @param tasks List of tasks.
     * @throws PeppaException If entry is not recognised or formatted correctly.
     */
    public static void parseFileEntry(String line, TaskList tasks) throws PeppaException {
        String[] fields = line.split(" \\| ");
        String taskType = fields[0];
        String done = fields[1];
        String taskDesc = fields[2];
        switch (taskType) {
        case (TodoCommand.ABBREVIATION):
            tasks.addTask(new Todo(taskDesc));
            break;
        case (EventCommand.ABBREVIATION):
            String[] eventFields = fields[3].split(" to ");
            LocalDate startDate = LocalDate.parse(eventFields[0],
                            DateTimeFormatter.ofPattern("MMM dd yyyy"));
            LocalDate endDate = LocalDate.parse(eventFields[1],
                            DateTimeFormatter.ofPattern("MMM dd yyyy"));
            tasks.addTask(new Event(taskDesc, startDate, endDate));
            break;
        case (DeadlineCommand.ABBREVIATION):
            LocalDateTime deadline = LocalDateTime.parse(fields[3],
                    DateTimeFormatter.ofPattern("MMM dd yyyy',' hh'.'mma"));
            tasks.addTask(new Deadline(taskDesc, deadline));
            break;
        default:
            throw new PeppaException("Boink! Peppa could not process the request. "
                    + "Please check that file input is correctly formatted and try again.");
        }
        if (done.equals("1")) {
            int idx = tasks.getLength() - 1;
            tasks.retrieveTask(idx).setDone(true);
        }
    }

    /**
     * Parses a mark task as done request into a MarkCommand object.
     *
     * @param args Array of arguments obtained from splitting the given command around a whitespace character.
     * @return MarkCommand object if command was parsed successfully, and IncorrectCommand object otherwise.
     */
    public static Command parseMarkCommand(String[] args) {
        try {
            String idxStr = args[1];
            return new MarkCommand(Integer.parseInt(idxStr) - 1);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            Ui.displayMessage("Boink! Peppa could not process the request. "
                    + "Please enter a valid integer and try again.");
            return new IncorrectCommand();
        }
    }

    /**
     * Parses an add todo request into a TodoCommand object.
     *
     * @param command Command given by the user.
     * @return TodoCommand object if command was parsed successfully, and IncorrectCommand object otherwise.
     */
    public static Command parseTodoCommand(String command) {
        try {
            return new TodoCommand(command.substring(TodoCommand.DESC_INDEX));
        } catch (IndexOutOfBoundsException e) {
            Ui.displayMessage("Boink! Peppa could not process the request. "
                    + "Please enter a description for the task and try again.");
            return new IncorrectCommand();
        }
    }

    /**
     * Parses an unmark task as done request into a UnmarkCommand object.
     *
     * @param args Array of arguments obtained from splitting the given command around a whitespace character.
     * @return UnmarkCommand object if command was parsed successfully, and IncorrectCommand object otherwise.
     */
    public static Command parseUnmarkCommand(String[] args) {
        try {
            String idxStr = args[1];
            return new UnmarkCommand(Integer.parseInt(idxStr) - 1);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            Ui.displayMessage("Boink! Peppa could not process the request. "
                    + "Please enter a valid integer and try again.");
            return new IncorrectCommand();
        }
    }
}

