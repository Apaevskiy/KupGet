package kup.get.config;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class TimerForHidingNodes {
    private final Timer timer;

    @Value("${timer.task.hidden}")
    private long delay;

    TimerTaskHidden timerTask;

    public TimerForHidingNodes(InputControl... nodes) {
        this.timer = new Timer(true);
        this.timerTask = new TimerTaskHidden(Arrays.asList(nodes));
    }

    public void schedule() {
        timer.purge();
        this.timerTask = timerTask.cancelSchedule();
        timer.schedule(timerTask, delay);
    }


    private static class TimerTaskHidden extends TimerTask {
        private final List<InputControl> nodes;

        private TimerTaskHidden(List<InputControl> nodes) {
            this.nodes = nodes;
        }

        @Override
        public void run() {
            if (nodes != null)
                waitHidden();
        }

        public void waitHidden() {
            for (InputControl node : nodes) {
                if (node.getField() != null) {
                    for (Node field : node.getField()) {
                        field.setStyle("");
                    }
                }
            }
        }

        public TimerTaskHidden cancelSchedule() {
            this.cancel();
            return new TimerTaskHidden(nodes);
        }
    }
}
