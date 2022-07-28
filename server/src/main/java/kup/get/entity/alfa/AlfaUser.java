package kup.get.entity.alfa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sp_user")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AlfaUser {
    @Id
    @Column(name = "KU")
    private long id;

    @Column(name = "FAM")
    private String lastName;

    @Column(name = "IM")
    private String firstName;

    @Column(name = "OTCH")
    private String middleName;
}
