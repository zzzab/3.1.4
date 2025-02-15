package ru.kata.spring.boot_security.demo.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kata.spring.boot_security.demo.models.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private Long id;

    @NotNull
    private String name;

    private String surname;

    private int age;

    @Email(message = "Email should be valid")
    private String email;

    private String password;

    private Set<Role> roles;
}