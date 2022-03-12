package kup.get.entity.alfa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "person_photo")
@Getter
@Setter
@NoArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long personId;
    private byte[] photo;
}
