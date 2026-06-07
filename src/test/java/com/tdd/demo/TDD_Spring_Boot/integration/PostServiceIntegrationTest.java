package com.tdd.demo.TDD_Spring_Boot.integration;

import com.tdd.demo.TDD_Spring_Boot.model.Post;
import com.tdd.demo.TDD_Spring_Boot.repository.PostRepository;
import com.tdd.demo.TDD_Spring_Boot.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // use H2
public class PostServiceIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    private PostService postService;

    @BeforeEach
    public void setUp() {
        postService = new PostService(postRepository);
        postRepository.deleteAll();
    }

    @Test
    public void saveAndFindPost() {
        Post post = new Post();
        post.setTitle("First Post");
        post.setDescription("Spring Boot TDD");

        Post saved = postService.savePost(post);
        assertNotNull(saved.getId());

        Post found = postService.getPostById(saved.getId());
        assertEquals("First Post", found.getTitle());
        assertEquals("Spring Boot TDD", found.getDescription());
    }

    @Test
    public void deletePost() {
        Post post = new Post();
        post.setTitle("To Delete");
        post.setDescription("Delete Test");

        Post saved = postService.savePost(post);
        postService.deletePost(saved.getId());

        assertTrue(postRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    public void updatePost() {
        Post post = new Post();
        post.setTitle("Old");
        post.setDescription("Old Desc");

        Post saved = postService.savePost(post);
        Post update = new Post();
        update.setTitle("New");
        update.setDescription("New Desc");

        Post updated = postService.updatePost(saved.getId(), update);
        assertEquals("New", updated.getTitle());
        assertEquals("New Desc", updated.getDescription());
    }

}
