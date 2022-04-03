package kup.get.entity.postgres.update;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "version")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
//    @Column(unique = true)
    private String release;
    private String information;

    public Version(String release, String information) {
        this.release = release;
        this.information = information;
    }
}
