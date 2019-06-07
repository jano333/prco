package sk.hudak.prco.task;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Date;

@Getter
@ToString
public class TaskContext {

    private TaskStatus status;
    private Date lastChanged;

    public TaskContext(TaskStatus status) {
        this(status, new Date());
    }

    public TaskContext(@NonNull TaskStatus status, @NonNull Date lastChanged) {
        this.status = status;
        this.lastChanged = lastChanged;
    }

}
