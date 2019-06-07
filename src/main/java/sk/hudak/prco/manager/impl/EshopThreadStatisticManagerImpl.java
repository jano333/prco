package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.manager.EshopThreadStatisticManager;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.task.EshopTaskManager;
import sk.hudak.prco.task.TaskContext;
import sk.hudak.prco.task.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EshopThreadStatisticManagerImpl implements EshopThreadStatisticManager {

    @Autowired
    private EshopTaskManager eshopTaskManager;

    @Autowired
    private InternalTxService internalTxService;

    @Override
    public void startShowingStatistics() {
        boolean shoudDownStatistic = false;
        Thread thread = new Thread(() -> {
            boolean aha = true;
            while (aha) {
                doInOneLoop();
                try {
                    Thread.sleep((long) (10 * 1000));
                } catch (InterruptedException e) {
                    log.error("thread interrupted " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
                if (shoudDownStatistic) {
                    log.debug("shunting down statistics");
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.setName("thread-statistic-mng");
        thread.setDaemon(true);
        thread.start();
    }

    private void doInOneLoop() {
        Map<EshopUuid, TaskContext> tasks = eshopTaskManager.getTasks();

        List<EshopUuid> running = new ArrayList<>(EshopUuid.values().length);
        List<EshopUuid> finishedOk = new ArrayList<>(EshopUuid.values().length);
        List<EshopUuid> finishedNotOk = new ArrayList<>(EshopUuid.values().length);

        for (Map.Entry<EshopUuid, TaskContext> eshopUuidTaskStatusEntry : tasks.entrySet()) {
            TaskStatus value = eshopUuidTaskStatusEntry.getValue().getStatus();
            if (value.equals(TaskStatus.RUNNING)) {
                running.add(eshopUuidTaskStatusEntry.getKey());
            }
            if (value.equals(TaskStatus.FINISHED_OK)) {
                finishedOk.add(eshopUuidTaskStatusEntry.getKey());
            }
            if (value.equals(TaskStatus.FINISHED_WITH_ERROR)) {
                finishedNotOk.add(eshopUuidTaskStatusEntry.getKey());
            }
        }

        log.debug("all tasks: {}  running: {}, finished(ok/error): {}/{}{}", tasks.size(), running.size(), finishedOk.size(), finishedNotOk.size(), finishedNotOk);
//        log.debug("status: {}", tasks.toString());
        log.debug("error statistic {}", internalTxService.getStatisticForErrors());

        /*if (running.isEmpty()) {
            return true;
        } else {
            return false;
        }*/
    }
}
