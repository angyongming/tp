package seedu.meetingjio.parser;

import seedu.meetingjio.commands.Command;
import seedu.meetingjio.commands.ListCommand;
import seedu.meetingjio.commands.DeleteCommand;
import seedu.meetingjio.commands.ClearCommand;
import seedu.meetingjio.commands.AddUserCommand;
import seedu.meetingjio.commands.AddLessonCommand;
import seedu.meetingjio.commands.AddMeetingCommand;
import seedu.meetingjio.commands.EditCommand;
import seedu.meetingjio.commands.FreeCommand;
import seedu.meetingjio.commands.CommandResult;
import seedu.meetingjio.commands.HelpCommand;

import seedu.meetingjio.exceptions.InvalidDayException;
import seedu.meetingjio.exceptions.InvalidModeException;
import seedu.meetingjio.exceptions.InvalidTimeException;
import seedu.meetingjio.exceptions.MissingValueException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_VALUES_ADD_EVENT;
import static seedu.meetingjio.common.ErrorMessages.ERROR_INVALID_COMMAND;
import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_PARAMETERS_ADD_EVENT;
import static seedu.meetingjio.common.ErrorMessages.ERROR_INVALID_TIME;
import static seedu.meetingjio.common.ErrorMessages.ERROR_INVALID_DAY;
import static seedu.meetingjio.common.ErrorMessages.ERROR_INVALID_MODE;
import static seedu.meetingjio.common.ErrorMessages.ERROR_INDEX_OUT_OF_BOUND;
import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_PARAMETERS_DELETE;
import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_VALUES_DELETE;
import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_PARAMETERS_ADD_MEETING;
import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_VALUES_ADD_MEETING;
import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_VALUES_ADD_USER;
import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_PARAMETERS_EDIT;
import static seedu.meetingjio.common.ErrorMessages.ERROR_MISSING_VALUES_EDIT;


import static seedu.meetingjio.common.Messages.MESSAGE_HELP;

public class Parser {

    private final String command;
    private final String arguments;
    public static Logger logger = Logger.getLogger(Parser.class.getName());

    private static final int NAME_INDEX = 0;
    private static final int TITLE_INDEX = 1;
    private static final int DAY_INDEX = 2;
    private static final int START_TIME_INDEX = 3;
    private static final int END_TIME_INDEX = 4;
    private static final int MODE_INDEX = 5;

    protected static final String[] HEADINGS_ALL = {"n", "t", "d", "st", "et", "m"};
    protected static final String[] HEADINGS_WITHOUT_NAME = {"t", "d", "st", "et", "m"};
    protected static final String[] HEADINGS_NAME_INDEX = {"n", "i"};
    protected static final String[] HEADINGS_ALL_WITH_INDEX = {"n", "i", "t", "d", "st", "et", "m"};

    public Parser(String input) {
        this.command = ParserArguments.getCommandFromInput(input);
        this.arguments = ParserArguments.getArgumentsFromInput(input);
    }

    public Command parseCommand() {
        switch (command) {
        case AddUserCommand.COMMAND_WORD:
            return prepareAddUser();
        case AddLessonCommand.COMMAND_WORD:
            return prepareAddLesson();
        case AddMeetingCommand.COMMAND_WORD:
            return prepareAddMeeting();
        case EditCommand.COMMAND_WORD:
            return prepareEdit();
        case ListCommand.ALL_COMMAND_WORD:
            return new ListCommand(arguments, 0);
        case ListCommand.LESSON_COMMAND_WORD:
            return new ListCommand(arguments, 1);
        case ListCommand.MEETING_COMMAND_WORD:
            return new ListCommand(arguments, 2);
        case DeleteCommand.COMMAND_WORD:
            return prepareDelete();
        case ClearCommand.COMMAND_WORD:
            return new ClearCommand(arguments);
        case FreeCommand.COMMAND_WORD:
            return new FreeCommand(arguments);
        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();
        default:
            String feedback = ERROR_INVALID_COMMAND + '\n' + MESSAGE_HELP;
            return new CommandResult(feedback);
        }
    }

    private Command prepareAddUser() {
        if (arguments.isEmpty()) {
            return new CommandResult(ERROR_MISSING_VALUES_ADD_USER);
        }
        return new AddUserCommand(arguments);
    }
    
    private Command prepareAddLesson() {
        try {
            String[] eventDescription = ParserArguments.splitArgumentsAll(arguments);
            ParserHelperMethods.checkNonNullValues(eventDescription);

            String day = eventDescription[DAY_INDEX];
            int startTime = Integer.parseInt(eventDescription[START_TIME_INDEX]);
            int endTime = Integer.parseInt(eventDescription[END_TIME_INDEX]);
            String mode = eventDescription[MODE_INDEX];

            ParserHelperMethods.checkDay(day);
            ParserHelperMethods.checkTime(startTime, endTime);
            ParserHelperMethods.checkMode(mode);

            String name = eventDescription[NAME_INDEX];
            String title = eventDescription[TITLE_INDEX];
            return new AddLessonCommand(name, title, day, startTime, endTime, mode);

        } catch (ArrayIndexOutOfBoundsException | NullPointerException npe) {
            return new CommandResult(ERROR_MISSING_PARAMETERS_ADD_EVENT);
        } catch (MissingValueException mve) {
            return new CommandResult(ERROR_MISSING_VALUES_ADD_EVENT);
        } catch (InvalidTimeException | NumberFormatException ite) {
            return new CommandResult(ERROR_INVALID_TIME);
        } catch (InvalidDayException ide) {
            return new CommandResult(ERROR_INVALID_DAY);
        } catch (InvalidModeException ime) {
            return new CommandResult(ERROR_INVALID_MODE);
        } catch (AssertionError ae) {
            logger.log(Level.INFO, "Assertion Error");
            return new CommandResult(ae.getMessage());
        }
    }

    private Command prepareEdit() {
        try {
            Map<String, String> newValues = ParserArguments.getAttributesMap(arguments);

            String name = newValues.get("n");
            String indexStr = newValues.get("n");
            int index = Integer.parseInt(indexStr);
            if (newValues.isEmpty()) {
                System.out.println("is empty");
                return new CommandResult(ERROR_MISSING_PARAMETERS_EDIT);
            }
            System.out.println(3);

            return new EditCommand(name, index, newValues);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException npe) {
            System.out.println(npe.getMessage());
            return new CommandResult(ERROR_MISSING_PARAMETERS_EDIT);
        } catch (NumberFormatException nfe) {
            System.out.println(nfe.getMessage());
            return new CommandResult(ERROR_INDEX_OUT_OF_BOUND);
        } catch (AssertionError ae) {
            logger.log(Level.INFO, "Assertion Error");
            return new CommandResult(ae.getMessage());
        }
    }

    /**
     * Try to parse the delete command to see if index has been done.
     */
    private Command prepareDelete() {
        try {
            String[] eventDescription = ParserArguments.splitArgumentsNameIndex(arguments);
            ParserHelperMethods.checkNonNullValues(eventDescription);

            String name = eventDescription[0];
            int index = Integer.parseInt(eventDescription[1]);
            return new DeleteCommand(name, index);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException npe) {
            return new CommandResult(ERROR_MISSING_PARAMETERS_DELETE);
        } catch (MissingValueException mve) {
            return new CommandResult(ERROR_MISSING_VALUES_DELETE);
        } catch (NumberFormatException nfe) {
            return new CommandResult(ERROR_INDEX_OUT_OF_BOUND);
        } catch (AssertionError ae) {
            logger.log(Level.INFO, "Assertion Error");
            return new CommandResult(ae.getMessage());
        }
    }

    private Command prepareAddMeeting() {
        try {
            String[] eventDescription = ParserArguments.splitArgumentsWithoutName(arguments);
            ParserHelperMethods.checkNonNullValues(eventDescription);

            //there is no name for meeting because meeting applies to everyone
            String day = eventDescription[DAY_INDEX - 1];
            int startTime = Integer.parseInt(eventDescription[START_TIME_INDEX - 1]);
            int endTime = Integer.parseInt(eventDescription[END_TIME_INDEX - 1]);
            String mode = eventDescription[MODE_INDEX - 1];

            ParserHelperMethods.checkDay(day);
            ParserHelperMethods.checkTime(startTime, endTime);
            ParserHelperMethods.checkMode(mode);

            String title = eventDescription[TITLE_INDEX - 1];
            return new AddMeetingCommand(title, day, startTime, endTime, mode);

        } catch (ArrayIndexOutOfBoundsException | NullPointerException npe) {
            return new CommandResult(ERROR_MISSING_PARAMETERS_ADD_MEETING);
        } catch (MissingValueException mve) {
            return new CommandResult(ERROR_MISSING_VALUES_ADD_MEETING);
        } catch (InvalidTimeException | NumberFormatException ite) {
            return new CommandResult(ERROR_INVALID_TIME);
        } catch (InvalidDayException ide) {
            return new CommandResult(ERROR_INVALID_DAY);
        } catch (InvalidModeException ime) {
            return new CommandResult(ERROR_INVALID_MODE);
        } catch (AssertionError ae) {
            logger.log(Level.INFO, "Assertion Error");
            return new CommandResult(ae.getMessage());
        }
    }


}
