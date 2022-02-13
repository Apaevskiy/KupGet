package kup.get.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;

    private TrafficItemType type;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateStart;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateFinish;

    private Person person;

}
