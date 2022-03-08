package kup.get.config.FX;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MyContextMenu extends ContextMenu {
    public MyContextMenu item(String name, EventHandler<ActionEvent> action) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(action);
        this.getItems().add(menuItem);
        return this;
    }

    public MyMenu menu(String title) {
        MyMenu myMenu = new MyMenu(title, this);
        this.getItems().add(myMenu);
        return myMenu;
    }

    public static MyContextMenu builder() {
        return new MyContextMenu();
    }

    public static class MyMenu extends Menu {
        private final MyContextMenu myContextMenu;

        public MyMenu(String title, MyContextMenu myContextMenu) {
            super(title);
            this.myContextMenu = myContextMenu;
        }

        public MyMenu menuItem(String name, EventHandler<ActionEvent> action) {
            MenuItem menuItem = new MenuItem(name);
            menuItem.setOnAction(action);
            this.getItems().add(menuItem);
            return this;
        }

        public MyContextMenu build() {
            return myContextMenu;
        }
    }
}
