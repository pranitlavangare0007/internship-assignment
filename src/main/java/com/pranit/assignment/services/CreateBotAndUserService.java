package com.pranit.assignment.services;

import com.pranit.assignment.models.Bot;
import com.pranit.assignment.models.User;
import com.pranit.assignment.repos.BotRepo;
import com.pranit.assignment.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateBotAndUserService {

    @Autowired
    private BotRepo botRepo;

    @Autowired
    private UserRepo userRepo;

    public User createUser(User user) {

        return userRepo.save(user);
    }

    public Bot createBot(Bot bot) {
        return botRepo.save(bot);
    }
}
