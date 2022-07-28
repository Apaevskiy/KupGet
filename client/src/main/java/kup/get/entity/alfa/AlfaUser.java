package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
public class AlfaUser {
    private long id;
    private String lastName;
    private String firstName;
    private String middleName;
}
