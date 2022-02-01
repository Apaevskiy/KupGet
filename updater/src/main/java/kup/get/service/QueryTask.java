package kup.get.service;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.ArrayDeque;

public class QueryTask extends Task<Void> {
    private final Label label;
    private final Timeline animation;

    private final ArrayDeque<String> states = new ArrayDeque<>();

    public QueryTask(Label label) {
        this.label = label;
        animation = getAnimation(label, createKeyFrame(""));
        animation.setOnFinished(event -> play());
    }


    @Override
    protected Void call() {
        for (String message = get(animation); message == null || !message.equals("stop"); message = get(animation)) {
            animation.getKeyFrames().remove(1);
            animation.getKeyFrames().add(1, createKeyFrame(message));
            animation.play();
        }
        return null;
    }

    private Timeline getAnimation(Control label, KeyFrame keyFrame) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(
                        Duration.millis(500),
                        new KeyValue(label.opacityProperty(), 0)),
                keyFrame,
                new KeyFrame(
                        Duration.millis(2000),
                        new KeyValue(label.opacityProperty(), 1)));
        return timeline;
    }

    public KeyFrame createKeyFrame(String message) {
        return new KeyFrame(Duration.millis(500), new KeyValue(label.textProperty(), message));
    }

    public synchronized String get(Animation animation) {
        while (animation.getStatus().equals(Animation.Status.RUNNING) || states.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return states.poll();
    }


    public synchronized void put(String message) {
        System.out.println("message " + message);
        states.addLast(message);
        notify();
    }

    private synchronized void play() {
        notify();
    }
}
