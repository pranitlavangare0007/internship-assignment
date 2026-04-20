package com.pranit.assignment.services;

import com.pranit.assignment.models.Comment;
import com.pranit.assignment.models.Post;
import com.pranit.assignment.repos.CommentRepo;
import com.pranit.assignment.repos.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private ViralityService viralityService;

    public Post createPost(Post post) {
        return postRepo.save(post);
    }



    public Comment addComment(Long postId, Comment comment) {

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        comment.setPost(post);

        Comment saved = commentRepo.save(comment);


        viralityService.updateScore(postId, "HUMAN_COMMENT");

        return saved;
    }
    public void likePost(Long postId) {

        if (!postRepo.existsById(postId)) {
            throw new RuntimeException("Post not found");
        }

        viralityService.updateScore(postId, "HUMAN_LIKE");
    }
}
