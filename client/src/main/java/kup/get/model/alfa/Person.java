package kup.get.model.alfa;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Person {
    private Long id;
    private String personnelNumber;
    private Department department;
    private Position position;
    private String lastName;
    private String firstName;
    private String middleName;
    private Integer rank;

    @Override
    public String toString() {
        return personnelNumber + ' ' + lastName + ' ' + firstName + ' ' + middleName;
    }
}
