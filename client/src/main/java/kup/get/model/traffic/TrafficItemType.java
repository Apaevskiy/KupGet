package kup.get.model.traffic;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
        return name;
    }

    public void setItemType(TrafficItemType type) {
        this.id = type.id;
        this.defaultDurationInMonth = type.getDefaultDurationInMonth();
        this.status = type.isStatus();
        this.name = type.getName();
    }
}
