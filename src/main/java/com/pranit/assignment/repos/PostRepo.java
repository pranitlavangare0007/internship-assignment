package com.pranit.assignment.repos;

import com.pranit.assignment.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepo  extends JpaRepository<Post,Long> {
}
