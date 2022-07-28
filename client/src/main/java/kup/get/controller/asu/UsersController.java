package kup.get.controller.asu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.entity.security.Role;
import kup.get.entity.security.User;
import kup.get.service.socket.PersonSocketService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FxmlLoader(path = "/fxml/asu/users.fxml")
public class UsersController extends MyAnchorPane {

    @FXML
    private TextField loginField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField numberField;

    @FXML
    private TextField passwordField;

    @FXML
    private ListView<Role> rolesView;

    @FXML
    private Button saveButton;

    @FXML
    private MyTable<User> usersTable;

    private final PersonSocketService services;
    private User user;

    private final ObservableList<Role> roles = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();

    public UsersController(PersonSocketService services) {
        this.services = services;

        usersTable
                .contextMenu(cm -> cm
                        .item("Добавить пользователя", event -> {
                            user = null;
                            loginField.setText("");
                            nameField.setText("");
                            numberField.setText("");
                            passwordField.setText("");
                            rolesView.getItems().forEach(u -> u.getChanged().set(false));
                        })
                        .item("Удалить пользователя", event -> {
                            SelectionModel<User> model = usersTable.getSelectionModel();
                            if (model != null && model.getSelectedItem() != null) {
                                services.deleteUser(model.getSelectedItem())
                                        .doOnError(e -> Platform.runLater(() -> createAlert("Ошибка", "Не удалось удалить пользователя")))
                                        .doOnSuccess(b -> usersTable.getItems().remove(model.getSelectedItem()))
                                        .subscribe();
                            }
                        }))
                .items(users)
                .addColumn(col -> col.header("id").cellValueFactory(User::getId).property(TableColumnBase::visibleProperty, false))
                .addColumn(col -> col.header("login").cellValueFactory(User::getUsername))
                .addColumn(col -> col.header("ФИО").cellValueFactory(User::getFIO))
                .addColumn(col -> col.header("Таб. №").cellValueFactory(User::getTabNum));

        rolesView.setCellFactory(CheckBoxListCell.forListView(Role::getChanged));
        rolesView.setItems(roles);

        saveButton.setOnAction(event -> {
            if (loginField.getText().isEmpty()) {
                createAlert("Уведомление", "Заполните поля");
                return;
            }
            try {
                if (user == null) {
                    user = new User();
                }
                user.setRoles(roles.stream().filter(r -> r.getChanged().get()).collect(Collectors.toSet()));
                user.setUsername(loginField.getText());
                user.setFIO(nameField.getText());
                user.setTabNum(numberField.getText().isEmpty() ? null : Long.valueOf(numberField.getText()));
                if (passwordField.getText() != null && !passwordField.getText().isEmpty())
                    user.setPassword(passwordField.getText());

                services.saveUser(user)
                        .doOnError(throwable -> Platform.runLater(() -> createAlert("Ошибка", throwable.getMessage())))
                        .doOnSuccess(u -> {
                            if (user.getId() == null) {
                                usersTable.getItems().add(u);
                                Platform.runLater(() -> createAlert("Уведомление", "Пользователь упешно создан!"));
                            } else
                                Platform.runLater(() -> createAlert("Уведомление", "Пользователь упешно изменён!"));
                            user = u;
                            loginField.setText("");
                            nameField.setText("");
                            numberField.setText("");
                            passwordField.setText("");
                            roles.forEach(us -> us.getChanged().set(false));
                        })
                        .subscribe();
            } catch (NumberFormatException e) {
                user = null;
                createAlert("Ошибка ввода данных", "Табельный номер должен состоять исключительно из цифр");
            }
            usersTable.refresh();
        });

        usersTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    user = row.getItem();
                    loginField.setText(user.getUsername());
                    nameField.setText(user.getFIO());
                    numberField.setText(String.valueOf(user.getTabNum()));
                    roles.forEach(role -> role.getChanged().set(user.getRoles().contains(role)));
                    rolesView.refresh();
                }
            });
            return row;
        });
    }

    @Override
    public void clearData() {
        roles.clear();
        users.clear();

        user = null;
        loginField.setText("");
        nameField.setText("");
        numberField.setText("");
        passwordField.setText("");
//        roles.forEach(u -> u.getChanged().set(false));
    }

    @Override
    public void fillData() {
        services.getRoles()
                .doFinally(signalType -> Platform.runLater(() -> rolesView.refresh()))
                .subscribe(e -> {
                    Platform.runLater(() -> rolesView.getItems().add(e));
//                    roles.add(e);
                });
        services.getUsers()
                .doFinally(signalType -> Platform.runLater(() -> usersTable.refresh()))
                .subscribe(users::add);
    }
}
