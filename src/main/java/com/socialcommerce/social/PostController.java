package com.socialcommerce.social;

import com.socialcommerce.social.document.Post;
import com.socialcommerce.social.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // ✅ Create Post
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    // ✅ Get Feed (posts of followed users)
    @PostMapping("/feed")
    public Page<Post> getFeed(
            @RequestBody List<String> authorIds,
            @RequestParam int page,
            @RequestParam int size) {

        return postService.getFeed(authorIds, page, size);
    }
    
    @GetMapping("/explore")
    public List<Post> explore() {
        return postService.getExplorePosts();
    }
    
    @PutMapping("/like/{postId}/{userId}")
    public Post likePost(@PathVariable String postId,
                         @PathVariable String userId) {
        return postService.toggleLike(postId, userId);
    }

    // ✅ Get posts of a user
    @GetMapping("/user/{authorId}")
    public Page<Post> getUserPosts(
            @PathVariable String authorId,
            @RequestParam int page,
            @RequestParam int size) {

        return postService.getUserPosts(authorId, page, size);
    }

    // ✅ Report a post
    @PutMapping("/report/{postId}")
    public Post reportPost(@PathVariable String postId) {
        return postService.reportPost(postId);
    }

    // ✅ Get reported posts (admin)
    @GetMapping("/reported")
    public List<Post> getReportedPosts() {
        return postService.getReportedPosts();
    }

    // ✅ Delete post
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
    }
}