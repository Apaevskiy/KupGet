package kup.get.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
public class Version implements Serializable {
    private long id;
    private String release;
    private String information;
}
