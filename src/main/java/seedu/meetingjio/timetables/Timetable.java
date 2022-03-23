package seedu.meetingjio.timetables;

import seedu.meetingjio.events.Event;
import seedu.meetingjio.exceptions.DuplicateEventException;
import seedu.meetingjio.exceptions.OverlappingEventException;

import java.util.ArrayList;

public class Timetable {
    private final String name;
    private final ArrayList<Event> list;

    public Timetable(String name) {
        this.name = name;
        this.list = new ArrayList<>();
    }

    /**
     * Checks if the event to be added has already been added, or if there is a timing clash with an existing event.
     *
     * @param event New event to be added
     * @throws DuplicateEventException If identical event has already been added
     * @throws OverlappingEventException If another existing event has a timetable clash
     */
    public void add(Event event) throws DuplicateEventException, OverlappingEventException {
        if (isDuplicate(event)) {
            throw new DuplicateEventException();
        } else if (isOverlap(event)) {
            throw new OverlappingEventException();
        }
        list.add(event);
    }

    public void remove(int index) {
        list.remove(index);
    }

    public int size() {
        return list.size();
    }

    public Event get(int eventIndex) {
        Event targetEvent = list.get(eventIndex);
        return targetEvent;
    }

    public String getName() {
        return name;
    }

    public void clear() {
        list.clear();
    }

    private boolean isDuplicate(Event newEvent) {
        for (int i = 0; i < list.size(); i++) {
            Event event = list.get(i);
            if (event.equals(newEvent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks through all existing events and comparing their timings to the event to be added to ensure that
     * there is no timing clash.
     *
     * @param newEvent Event to be added
     * @return true if there is overlap, otherwise false
     */
    private boolean isOverlap(Event newEvent) {
        for (int i = 0; i < list.size(); i++) {
            Event event = list.get(i);
            if (event.overlaps(newEvent)) {
                return true;
            }
        }
        return false;
    }

}