package kup.get.entity.alfa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;

@Entity
@Table(name = "sp_ol_blobs")
@Getter
@Setter
@NoArgsConstructor
public class Photo {

    @Id
    @Column(name = "kod")
    private long id;

    @Column(name = "kod_il")
    private long personId;

    @Column(name = "pict")
    private byte[] photo;

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", personId=" + personId +
                ", photo=" + (photo != null) +
                '}';
    }
}
