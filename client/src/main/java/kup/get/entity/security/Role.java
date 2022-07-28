package kup.get.entity.security;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Data
public class Role{
    private Long id;
    private String name;
    private String fullName;

    private final BooleanProperty changed = new SimpleBooleanProperty();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name) && Objects.equals(fullName, role.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fullName);
    }

    @Override
    public String toString() {
        return fullName;
    }
}
