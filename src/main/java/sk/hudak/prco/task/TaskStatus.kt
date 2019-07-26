package sk.hudak.prco.task;

public enum TaskStatus {
    RUNNING,
    SHOUD_STOP, // pomocny interny stav
    STOPPED,
    FINISHED_OK,
    FINISHED_WITH_ERROR
}
