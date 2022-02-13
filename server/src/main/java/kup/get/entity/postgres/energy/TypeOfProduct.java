package kup.get.entity.postgres.energy;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "type_of_products")
public class TypeOfProduct{
    @Id
    private Long id;
    private String name;
    private String unit;
}
