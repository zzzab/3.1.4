package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.exception_handling.NoSuchUserException;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void saveUser(User user) {
        System.out.println("Received roles: " + user.getRole());
        User userByName = userRepository.findByUsername(user.getUsername());
        if (userByName != null) {
            throw new IllegalStateException("Username: " + userByName);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roleSet = new HashSet<>();

        if (user.getRole() != null && !user.getRole().isEmpty()) {
            for (Role rl : user.getRole()) {
                Role roleFromDb = roleRepository.getRoleByName(rl.getName());
                if (roleFromDb != null) {
                    roleSet.add(roleFromDb);
                }
            }
        }
        user.setRole(roleSet);
        userRepository.save(user);
    }


    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Not found " + email);
        }
        return user;
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateUser(User user) {
        long id = user.getId();
        User existingUser = userRepository.findById(id).orElseThrow(() -> new NoSuchUserException("User not found"));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roleSet = new HashSet<>();
        if (user.getRole() != null) {
            for (Role rl : user.getRole()) {
                roleSet.add(roleRepository.getRoleByName(rl.getName()));
            }
        }
        existingUser.setRoles(roleSet);
        userRepository.save(existingUser);
    }
}