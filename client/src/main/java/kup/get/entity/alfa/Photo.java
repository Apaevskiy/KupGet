package kup.get.entity.alfa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Photo  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private long id;
    private long personId;
    private byte[] photo;
}
