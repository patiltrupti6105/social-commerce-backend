package com.socialcommerce.social;

import com.socialcommerce.social.document.Post;

import java.util.List;

public interface PostService {
    List<Post> getReportedPosts();
    void deletePost(String postId);
}
