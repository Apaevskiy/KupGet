package kup.get.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
public class TrafficItemType {
    private Long id;
    private int defaultDurationInMonth;
    private boolean status;
    private boolean changed = false;
    private String name;

    public boolean isNotEmpty() {
        return !this.equals(new TrafficItemType());
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", status=" + status +
                ", changed=" + changed +
                '}';
    }
}
