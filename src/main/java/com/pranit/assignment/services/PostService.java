package com.pranit.assignment.services;

import com.pranit.assignment.entity.AuthorType;
import com.pranit.assignment.models.Comment;
import com.pranit.assignment.models.Post;
import com.pranit.assignment.repos.CommentRepo;
import com.pranit.assignment.repos.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private ViralityService viralityService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Post createPost(Post post) {
        return postRepo.save(post);
    }


    public Comment addComment(Long postId, Comment comment) {

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (comment.getDepthLevel() > 20) {
            throw new RuntimeException("Depth limit exceeded (max 20)");
        }
        String botCountKey=null;
        try {
            if (comment.getAuthorType() == AuthorType.BOT) {

                 botCountKey = "post:" + postId + ":bot_count";

                Long count = redisTemplate.opsForValue().increment(botCountKey);

                if (count != null && count > 100) {
                    throw new RuntimeException("429 Too Many Requests - Bot limit reached");
                }


                if (comment.getParentAuthorType() == AuthorType.USER) {

                    String cooldownKey =
                            "cooldown:bot_" + comment.getAuthorId() +
                                    ":human_" + comment.getParentAuthorId();

                    Boolean exists = redisTemplate.hasKey(cooldownKey);

                    if (Boolean.TRUE.equals(exists)) {

                        redisTemplate.opsForValue().decrement(botCountKey);
                        throw new RuntimeException("Cooldown active - try after 10 minutes");
                    }


                    redisTemplate.opsForValue().set(cooldownKey, "1", 10, TimeUnit.MINUTES);
                }
            }
            comment.setPost(post);

            Comment saved = commentRepo.save(comment);
            if (comment.getAuthorType() == AuthorType.BOT && comment.getParentAuthorType() == AuthorType.USER) {

                handleNotification(comment.getParentAuthorId(),
                        "Bot " + comment.getAuthorId() + " replied to your post");
            }

            if (comment.getAuthorType() == AuthorType.USER) {
                viralityService.updateScore(postId, "HUMAN_COMMENT");
            } else {
                viralityService.updateScore(postId, "BOT_REPLY");
            }

            return saved;

        } catch (Exception e) {

            if (botCountKey != null && comment.getAuthorType() == AuthorType.BOT) {
                redisTemplate.opsForValue().decrement(botCountKey);
            }

            throw e;
        }
    }

    public void handleNotification(Long userId, String message) {

        String cooldownKey = "notif:cooldown:user:" + userId;
        String listKey = "user:" + userId + ":pending_notifs";

        Boolean exists = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.opsForList().rightPush(listKey, message);
        } else {
            System.out.println("Push Notification Sent to User: " + message);

            redisTemplate.opsForValue().set(cooldownKey, "1", 15, TimeUnit.MINUTES);
        }
    }

    public void likePost(Long postId) {

        if (!postRepo.existsById(postId)) {
            throw new RuntimeException("Post not found");
        }

        viralityService.updateScore(postId, "HUMAN_LIKE");
    }
}
