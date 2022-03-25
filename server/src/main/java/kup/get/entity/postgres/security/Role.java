package kup.get.entity.postgres.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "t_role")
@NoArgsConstructor
@Getter
@Setter
public class Role implements GrantedAuthority {
    @Id
    private Long id;
    private String name;
    private String fullName;
    /*@Transient
    @OneToMany(fetch = FetchType.EAGER)
    private Set<User> users;*/

    @Override
    public String getAuthority() {
        return getName();
    }

    @Override
    public String toString() {
        return name.replaceAll("ROLE_","");
    }
}
