package kup.get.config;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.Builder;

public class MyContextMenu extends ContextMenu {
    public MyContextMenu item(String name, EventHandler<ActionEvent> action){
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(action);
        this.getItems().add(menuItem);
        return this;
    }
    public static MyContextMenu builder(){
        return new MyContextMenu();
    }
}
