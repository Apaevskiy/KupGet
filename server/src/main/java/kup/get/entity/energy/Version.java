package kup.get.entity.energy;

import lombok.*;

import javax.persistence.*;
import java.io.File;

@Entity
@Table(name = "version")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
//    @Column(unique = true)
    private String release;
    private String information;

    public Version(String release, String information) {
        this.release = release;
        this.information = information;
    }
}
