package kup.get.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
public class Person {
    private long id;
    private String personnelNumber;
    private Department department;
    private Position position;
    private String lastName;
    private String firstName;
    private String middleName;
}
