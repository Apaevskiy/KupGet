package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;

@Data
public class ClinicEvent {
    private long id;
    private LocalDate dateClinicInspection;
    private Time time;
    private int pnp;
    private Long personId;
    private AlfaUser user;
    private Date dateCheckpointInspection;
}
