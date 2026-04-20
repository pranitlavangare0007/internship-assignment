package com.pranit.assignment.controllers;

import com.pranit.assignment.models.Comment;
import com.pranit.assignment.models.Post;
import com.pranit.assignment.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @PostMapping("/{postId}/comments")
    public Comment addComment(@PathVariable Long postId,
                              @RequestBody Comment comment) {
        return postService.addComment(postId, comment);
    }
    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return "Post liked successfully";
    }
}
