package kup.get.controller.other;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import kup.get.config.FX.FxmlLoader;
import kup.get.config.FX.MyAnchorPane;
import kup.get.config.MyTable;
import kup.get.entity.alfa.Department;
import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Position;
import kup.get.service.DAO.PersonDaoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;


@FxmlLoader(path = "/fxml/people.fxml")
public class PersonController extends MyAnchorPane {

    @FXML
    private MyTable<Person> peopleTable;

    private final PersonDaoService service;
    private final List<Person> people = new ArrayList<>();

    public PersonController(PersonDaoService service) {

        this.service = service;

        peopleTable
                .contextMenu(cm -> cm
                        .item("Добавить", e -> {
                            peopleTable.getItems().add(new Person());
                            peopleTable.getSelectionModel().selectLast();
                            peopleTable.refresh();
                        })
                        .item("Удалить", e -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Подтверждение");
                            alert.setHeaderText("Вы действительно хотите удалить сотрудника?");
                            alert.setContentText("Нажмите ОК для удаление и Cancel для отмены");

                            Optional<ButtonType> option = alert.showAndWait();
                            if (option.isPresent() && option.get() == ButtonType.OK) {
                                SelectionModel<Person> model = peopleTable.getSelectionModel();
                                if (model != null && model.getSelectedItem() != null) {
                                    service.deletePerson(model.getSelectedItem());
                                    peopleTable.getItems().remove(model.getSelectedItem());
                                    peopleTable.refresh();
                                }
                            }
                        }))
                .<String>addColumn(col -> col
                        .header("Таб.№")
                        .cellValueFactory(Person::getPersonnelNumber)
                        .editable(savePerson(Person::setPersonnelNumber))
                        .cellFactory(TextFieldTableCell.forTableColumn()))
                .<String>addColumn(col -> col
                        .header("Фамилия")
                        .cellValueFactory(Person::getLastName)
                        .editable(savePerson(Person::setLastName))
                        .cellFactory(TextFieldTableCell.forTableColumn()))
                .<String>addColumn(col -> col
                        .header("Имя")
                        .cellValueFactory(Person::getFirstName)
                        .editable(savePerson(Person::setFirstName))
                        .cellFactory(TextFieldTableCell.forTableColumn()))
                .<String>addColumn(col -> col
                        .header("Отчество")
                        .cellValueFactory(Person::getMiddleName)
                        .editable(savePerson(Person::setMiddleName))
                        .cellFactory(TextFieldTableCell.forTableColumn()))
                .<Integer>addColumn(col -> col
                        .header("Разряд")
                        .cellValueFactory(Person::getRank)
                        .editable(savePerson(Person::setRank))
                        .cellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter())))
                .<String>addColumn(col -> col
                        .header("Должность")
                        .cellValueFactory( p -> p.getPosition().getName())
                        .editable(savePosition())
                        .cellFactory(TextFieldTableCell.forTableColumn()))
                .<String>addColumn(col -> col
                        .header("Подразделение")
                        .cellValueFactory( p -> p.getDepartment().getName())
                        .editable(saveDepartment())
                        .cellFactory(TextFieldTableCell.forTableColumn()));
    }

    private BiConsumer<Person, String> saveDepartment() {
        return ((BiConsumer<Person, String>)
                (person, s) -> {
                    Department department = service.getDepartmentByName(s);
                    if (department == null) {
                        department = new Department();
                        department.setName(s);
                    }
                    person.setDepartment(department);
                })
                .andThen((person, t) -> {
                    Person per = service.savePerson(person);
                    if (per != null)
                        person.setDepartment(per.getDepartment());
                });
    }

    private BiConsumer<Person, String> savePosition() {
        return ((BiConsumer<Person, String>)
                (person, s) -> {
                    Position position = service.getPositionByName(s);
                    if (position == null) {
                        position = new Position();
                        position.setName(s);
                    }
                    person.setPosition(position);
                })
                .andThen((person, t) -> {
                    Person per = service.savePerson(person);
                    if (per != null)
                        person.setPosition(per.getPosition());
                });
    }

    private <T> BiConsumer<Person, T> savePerson(BiConsumer<Person, T> consumer) {
        return consumer.andThen((person, t) -> {
            Person p = service.savePerson(person);
            if (person != null && p != null) {
                person.setId(p.getId());
                person.setFirstName(p.getFirstName());
                person.setLastName(p.getLastName());
                person.setMiddleName(p.getMiddleName());
                person.setRank(p.getRank());
                person.setPersonnelNumber(p.getPersonnelNumber());
            }
        });
    }

    @Override
    public void fillData() {
        service.getPeople().subscribe(people::add);
    }

    @Override
    public void clearData() {
        peopleTable.getItems().clear();
    }
}
