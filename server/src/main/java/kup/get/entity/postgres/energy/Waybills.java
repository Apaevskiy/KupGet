package kup.get.entity.postgres.energy;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "waybills")
public class Waybills {
    @Id
    private Long id;
    private String person;
    private String department;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate date;
}
