package kup.get.config;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Data
public class CustomMenuItem {
    private GridPane menuItem;
    private CustomMenuItem parent;
    private final Set<CustomMenuItem> children = new HashSet<>();
    private final Set<String> roles = new HashSet<>();
    private String text;

    public CustomMenuItem menuItem(String text, Node icon) {
        menuItem = new GridPane();
        menuItem.getColumnConstraints().addAll(
                new ColumnConstraints(50),
                new ColumnConstraints(0, GridPane.USE_COMPUTED_SIZE, GridPane.USE_COMPUTED_SIZE));
        Label label = new Label(text);
        this.text = text;
        label.setMinWidth(0);
        label.setPrefHeight(35);
        GridPane.setHalignment(icon, HPos.CENTER);
        GridPane.setValignment(icon, VPos.CENTER);
        menuItem.addRow(0, icon, label);
        menuItem.setCursor(Cursor.HAND);
        VBox.setMargin(menuItem, new Insets(0, 10, 0, 10));
        return this;
    }

    public static CustomMenuItem builder() {
        return new CustomMenuItem();
    }

    public static void addToPane(VBox box, Set<CustomMenuItem> items) {
        box.getChildren().clear();
        box.getChildren().addAll(items.stream().sorted(Comparator.comparing(CustomMenuItem::getText)).map(CustomMenuItem::getMenuItem).collect(Collectors.toList()));
    }

    public CustomMenuItem setRoles(String... roles) {
        Collections.addAll(this.roles, roles);
        return this;
    }

    public CustomMenuItem setEventOpenMenu(VBox box, AtomicReference<CustomMenuItem>  actualItem, GridPane returnButton, Label menuLabel) {
        menuItem.setOnMouseClicked(event -> {
            actualItem.set(this);
            returnButton.setVisible(true);
            menuLabel.setText(this.getText());
            box.getChildren().setAll(children.stream().sorted(Comparator.comparing(CustomMenuItem::getText)).map(CustomMenuItem::getMenuItem).collect(Collectors.toList()));
        });
        return this;
    }

    public CustomMenuItem addChildren(CustomMenuItem ... items) {
        for (CustomMenuItem item : items){
            item.setParent(this);
            this.children.add(item);
        }
        return this;
    }
    public void addChildren(Set<CustomMenuItem> items) {
        for (CustomMenuItem item : items){
            item.setParent(this);
            this.children.add(item);
        }
    }
    public CustomMenuItem setEventSwitchPane(EventHandler<? super MouseEvent> eventHandler) {
        menuItem.setOnMouseClicked(eventHandler);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomMenuItem that = (CustomMenuItem) o;
        return Objects.equals(menuItem, that.menuItem) && Objects.equals(children, that.children) && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuItem, children, roles);
    }
}
