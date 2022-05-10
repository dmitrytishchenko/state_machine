package ru.tdn.state_machine.statemachine;

public enum States {
    BACKLOG,
    IN_PROGRESS,
    TESTING,
    DONE;

    public static String getId(States states) {
        for (States s : States.values()) {
            if (s.equals(states)) return s.name();
        }
        return null;
    }
}
