package ru.kata.spring.boot_security.demo.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.exception_handling.NoSuchUserException;
import ru.kata.spring.boot_security.demo.models.DTO.UserUpdateRequest;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void saveUser(User user) {
        System.out.println("Received roles: " + user.getRoles());
        User userByName = userRepository.findByUsername(user.getUsername());
        if (userByName != null) {
            throw new IllegalStateException("Username: " + userByName);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roleSet = new HashSet<>();

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role rl : user.getRoles()) {
                Role roleFromDb = roleService.getRoleByName(rl.getName());
                if (roleFromDb != null) {
                    roleSet.add(roleFromDb);
                }
            }
        }
        user.setRoles(roleSet);
        userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Not found " + email);
        }
        return user;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateUser(UserUpdateRequest user) {
        User existingUser = userRepository.findById(user.getId()).orElseThrow(() -> new NoSuchUserException("User not found"));

        //обновляем имя
        if (user.getName() != null && !user.getName().isEmpty()) {
            existingUser.setName(user.getName());
        }

        //Обновляем фамилию
        if (user.getSurname() != null && !user.getSurname().isEmpty()) {
            existingUser.setSurname(user.getSurname());
        }

        //Обновляем возраст
        if (user.getAge() != 0) {
            existingUser.setAge(user.getAge());
        }

        // Обновляем email, если он был изменен
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            existingUser.setEmail(user.getEmail());
        }

        // Обновляем пароль, только если он был изменен
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Обновляем роли
        Set<Role> roleSet = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role rl : user.getRoles()) {
                roleSet.add(roleService.getRoleByName(rl.getName()));
            }
        }

        existingUser.setRoles(roleSet);
        userRepository.save(existingUser);
    }
}