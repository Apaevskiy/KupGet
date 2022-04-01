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
import kup.get.config.FX.MyContextMenu;
import kup.get.config.MyTable;
import kup.get.entity.alfa.Department;
import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Position;
import kup.get.service.DAO.PersonDaoService;

import java.util.Optional;
import java.util.function.BiConsumer;


@FxmlLoader(path = "/fxml/people.fxml")
public class PersonController extends MyAnchorPane {

    @FXML
    private MyTable<Person> peopleTable;

    private final PersonDaoService service;

    public PersonController(PersonDaoService service) {

        this.service = service;

        peopleTable
                .setMyContextMenu(createContextMenu())
                .addColumn("Таб.№",
                        Person::getPersonnelNumber,
                        savePerson(Person::setPersonnelNumber),
                        TextFieldTableCell.forTableColumn())
                .addColumn("Фамилия",
                        Person::getLastName,
                        savePerson(Person::setLastName),
                        TextFieldTableCell.forTableColumn())
                .addColumn("Имя",
                        Person::getFirstName,
                        savePerson(Person::setFirstName),
                        TextFieldTableCell.forTableColumn())
                .addColumn("Отчество",
                        Person::getMiddleName,
                        savePerson(Person::setMiddleName),
                        TextFieldTableCell.forTableColumn())
                .addColumn("Разряд",
                        Person::getRank,
                        savePerson(Person::setRank),
                        TextFieldTableCell.forTableColumn(new IntegerStringConverter()))
                .addColumn("Должность",
                        p -> p.getPosition().getName(),
                        savePosition(),
                        TextFieldTableCell.forTableColumn())
                .addColumn("Подразделение",
                        p -> p.getDepartment().getName(),
                        saveDepartment(),
                        TextFieldTableCell.forTableColumn());
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
                    if(per!=null)
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
                .andThen((person, t) ->{
                    Person per = service.savePerson(person);
                    if(per!=null)
                        person.setPosition(per.getPosition());
                });
    }

    private <T> BiConsumer<Person, T> savePerson(BiConsumer<Person, T> consumer) {
        return consumer.andThen((person, t) ->{
            Person p = service.savePerson(person);
            if (person != null && p != null){
                person.setId(p.getId());
                person.setFirstName(p.getFirstName());
                person.setLastName(p.getLastName());
                person.setMiddleName(p.getMiddleName());
                person.setRank(p.getRank());
                person.setPersonnelNumber(p.getPersonnelNumber());
            }
        });
    }

    private ContextMenu createContextMenu() {
        return MyContextMenu.builder()
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
                });
    }

    @Override
    public void fillData() {
        peopleTable.getItems().addAll(service.getPeople());
    }

    @Override
    public void clearData() {
        peopleTable.getItems().clear();
    }
}
