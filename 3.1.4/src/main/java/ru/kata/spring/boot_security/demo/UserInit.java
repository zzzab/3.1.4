package ru.kata.spring.boot_security.demo;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.*;

@AllArgsConstructor
@Component
public class UserInit {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        Role userRole = roleRepository.getRoleByName("ROLE_USER");
        Role adminRole = roleRepository.getRoleByName("ROLE_ADMIN");

        if (userRepository.findByUsername("admin") == null) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);

            User admin = new User();
            admin.setUsername("admin");
            admin.setSurname("admin");
            admin.setAge(26);
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(adminRoles);
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user") == null) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);


            User user = new User();
            user.setUsername("user");
            user.setSurname("user");
            user.setAge(26);
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRoles(userRoles);
            userRepository.save(user);
        }
    }
}
