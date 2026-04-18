package com.socialcommerce.social;

import com.socialcommerce.social.document.Post;
import com.socialcommerce.social.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    @Autowired
    private PostRepository postRepository;

    // ✅ Get feed (latest posts of followed users)
    public Page<Post> getFeed(List<String> authorIds, int page, int size) {
        return postRepository.findByAuthorIdIn(
                authorIds,
                PageRequest.of(page, size)
        );
    }
}