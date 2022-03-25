package kup.get.entity.security;

import lombok.Data;

import java.util.Set;

@Data
public class User{
    private Long id;
    private String username;
    private String password;
    private String FIO;
    private Long tabNum;
    private Set<Role> roles;
}
