package kup.get.entity;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Version {
    private long id;
    private String release;
    private String information;
}
