package com.pranit.assignment.repos;

import com.pranit.assignment.models.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRepo extends JpaRepository<Bot,Long> {
}
