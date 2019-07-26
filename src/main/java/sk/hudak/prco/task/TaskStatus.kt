package sk.hudak.prco.task

enum class TaskStatus {
    RUNNING,
    SHOUD_STOP, // pomocny interny stav
    STOPPED,
    FINISHED_OK,
    FINISHED_WITH_ERROR
}
