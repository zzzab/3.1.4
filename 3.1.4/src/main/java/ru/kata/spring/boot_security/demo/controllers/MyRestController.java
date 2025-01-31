package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception_handling.DataInfoHandler;
import ru.kata.spring.boot_security.demo.exception_handling.UserWithSuchLoginExist;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
    public class MyRestController {
        private final UserService userService;


        public MyRestController(UserService userService) {
            this.userService = userService;
        }


        @GetMapping("/api/user/my")
        public ResponseEntity<User> getUserInfo(@AuthenticationPrincipal User user) {
            return ResponseEntity.ok(user);
        }

         @GetMapping("/api/users")
         public ResponseEntity<List<User>> apiGetAllUsers()
         {List<User> users = userService.findAll();
             return new ResponseEntity<>(users, HttpStatus.OK);
         }

        @GetMapping("/api/users/{id}")
        public ResponseEntity<User> apiGetOneUser(@PathVariable("id") long id) {
            User user = userService.findById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

    @PostMapping("/api/users")
    public ResponseEntity<User> addNewUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            userService.saveUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<DataInfoHandler> apiUpdateUser(@PathVariable("id") int id,
                                                         @RequestBody @Valid User user,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = getErrorsFromBindingResult(bindingResult);
            return new ResponseEntity<>(new DataInfoHandler(error), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.updateUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            throw new UserWithSuchLoginExist("User with such login exists");
        }
    }

    @DeleteMapping("/api/users/{id}")
        public ResponseEntity<DataInfoHandler> apiDeleteUser(@PathVariable("id") long id) {
            userService.deleteById(id);
            return new ResponseEntity<>(new DataInfoHandler("User was deleted"), HttpStatus.OK);
        }

        private String getErrorsFromBindingResult(BindingResult bindingResult) {
            return bindingResult.getFieldErrors()
                    .stream()
                    .map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        }
    }
