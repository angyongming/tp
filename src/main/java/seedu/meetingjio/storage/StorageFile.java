package seedu.meetingjio.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import seedu.meetingjio.exceptions.IncorrectIndexException;
import seedu.meetingjio.exceptions.InvalidEventTypeException;
import seedu.meetingjio.timetables.MasterTimetable;
import seedu.meetingjio.parser.ParserLocalData;

import static seedu.meetingjio.common.ErrorMessages.ERROR_INVALID_EVENT_TYPE_LOADING;

import static seedu.meetingjio.common.Messages.MESSAGE_DIVIDER;
import static seedu.meetingjio.common.Messages.SAVE_DATA_MESSAGE;
import static seedu.meetingjio.common.ErrorMessages.ERROR_DATA_SAVE_FAILED;
import static seedu.meetingjio.common.ErrorMessages.ERROR_EMPTY_ROW;
import static seedu.meetingjio.common.ErrorMessages.ERROR_INCORRECT_INDEX;
import static seedu.meetingjio.parser.ParserLocalData.checkEventType;


/**
 * Represents the file used to store timetable data.
 */

public class StorageFile {

    public static final String DATA_FILE_PATH = "MeetingJio.txt";

    private static final String NO_TIMETABLE = "There are no lessons in your timetable yet!";

    public static void saveData(MasterTimetable masterTimetable) {
        System.out.println(MESSAGE_DIVIDER);
        try {
            File dataFile = new File(DATA_FILE_PATH);
            dataFile.createNewFile();
            FileOutputStream outFile = new FileOutputStream(dataFile, false);
            FileWriter dataWriter = new FileWriter(dataFile);
            String str = masterTimetable.collateAll(masterTimetable);
            String truncatedString = "";
            if (str != "") {
                truncatedString = str.substring(0, str.length() - 1);
            }
            dataWriter.write(truncatedString);
            dataWriter.close();
            System.out.println(SAVE_DATA_MESSAGE);
        } catch (IOException ioe) {
            System.out.println(ERROR_DATA_SAVE_FAILED);
        }
    }

    public static void loadData(MasterTimetable masterTimetable) {
        try {
            File dataFile = new File(DATA_FILE_PATH);
            Scanner dataReader = new Scanner(dataFile);
            int listNum = 0;
            String name = "";
            String eventType;
            boolean hasMeeting = false;
            List<String> meetingList = new ArrayList<>();
            int personCount = 0;
            while (dataReader.hasNextLine()) {
                String data = dataReader.nextLine();
                if (isName(data)) {
                    name = data;
                    listNum = 1;
                    personCount++;
                } else {
                    if (!isNoTimetableMessage(data)) {
                        if (checkIndex(data, listNum)) {
                            eventType = ParserLocalData.getEventType(data);
                            checkEventType(eventType);
                            if (eventType.equals("L")) {
                                ParserLocalData.prepareLoadLesson(name, data, masterTimetable);
                            } else if (eventType.equals("M") && personCount == 1) {
                                meetingList.add(data);
                                hasMeeting = true;
                            }
                            listNum++;
                        } else {
                            throw new IncorrectIndexException();
                        }
                    }
                }
            }
            if (hasMeeting) {
                for (String str : meetingList) {
                    ParserLocalData.prepareLoadMeeting(str, masterTimetable);
                }
            }
        } catch (FileNotFoundException ffe) {
            System.out.println("");
        } catch (InvalidEventTypeException iete) {
            throw new RuntimeException(ERROR_INVALID_EVENT_TYPE_LOADING);
        } catch (IncorrectIndexException iie) {
            throw new RuntimeException(ERROR_INCORRECT_INDEX);
        }
    }

    private static boolean checkIndex(String data, int expectedIndex) {
        String index = getFirstChar(data);
        int i = Integer.parseInt(index);
        if (i == expectedIndex) {
            return true;
        }
        return false;
    }

    private static boolean isName(String data) {
        String firstChar = getFirstChar(data);
        if (firstChar == null || isNoTimetableMessage(data) == true) {
            return false;
        }
        try {
            int i = Integer.parseInt(firstChar);
        } catch (NumberFormatException nfe) {
            return true;
        }
        return false;
    }

    private static boolean isNoTimetableMessage(String data) {
        if (NO_TIMETABLE.equalsIgnoreCase(data)) {
            return true;
        }
        return false;
    }

    private static String getFirstChar(String data) throws StringIndexOutOfBoundsException {
        try {
            data = data.trim();
            String firstChar = data.substring(0, 1);
            return firstChar;
        } catch (StringIndexOutOfBoundsException sibe) {
            throw new RuntimeException(ERROR_EMPTY_ROW);
        }
    }

}