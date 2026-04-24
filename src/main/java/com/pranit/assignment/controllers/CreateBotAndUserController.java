package com.pranit.assignment.controllers;

import com.pranit.assignment.models.Bot;
import com.pranit.assignment.models.User;
import com.pranit.assignment.services.CreateBotAndUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CreateBotAndUserController {

    @Autowired
    private CreateBotAndUserService createBotAndUserService;

    @PostMapping("/users")
    public User createUser(@RequestBody User user){
        return createBotAndUserService.createUser(user);

    }

    @PostMapping("/bots")
    public Bot createUser(@RequestBody Bot bot){
        return createBotAndUserService.createBot(bot);

    }
}
