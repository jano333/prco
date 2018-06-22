package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.manager.EshopThreadStatisticManager;
import sk.hudak.prco.task.TaskManager;
import sk.hudak.prco.task.TaskStatus;

import java.util.Map;

@Slf4j
@Component
public class EshopThreadStatisticManagerImpl implements EshopThreadStatisticManager {

    @Autowired
    private TaskManager taskManager;

    @Override
    public void startShowingStatistics() {
        Thread thread = new Thread(() -> {
            boolean aha = true;
            while (aha) {
                doInOneLoop();
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    //TODO
                    e.printStackTrace();
                }
            }

        });
        thread.setName("thread-statistic-mng");
        thread.setDaemon(true);
        thread.start();
    }

    private void doInOneLoop() {
        Map<EshopUuid, TaskStatus> tasks = taskManager.getTasks();
        int countOfRunning = 0;
        for (Map.Entry<EshopUuid, TaskStatus> eshopUuidTaskStatusEntry : tasks.entrySet()) {
            if (eshopUuidTaskStatusEntry.getValue().equals(TaskStatus.RUNNING)) {
                countOfRunning++;
            }
        }
        log.debug(">> all tasks: {}  running: {}", tasks.size(), countOfRunning);
        log.debug("status: {}", tasks.toString());

    }
}
