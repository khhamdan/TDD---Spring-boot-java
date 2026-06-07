package com.tdd.demo.TDD_Spring_Boot.integration;

import com.tdd.demo.TDD_Spring_Boot.model.Post;
import com.tdd.demo.TDD_Spring_Boot.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll(); // Clean database before each test
    }

    @Test
    public void savePost() throws Exception {
        String requestJson = """
            {
                "title": "First Post",
                "description": "Spring Boot Integration Test"
            }
            """;

        mockMvc.perform(post("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("First Post"))
                .andExpect(jsonPath("$.description").value("Spring Boot Integration Test"));
    }

    @Test
    public void getAllPosts() throws Exception {
        Post post1 = postRepository.save(new Post(null, "Post 1", "Desc 1"));
        Post post2 = postRepository.save(new Post(null, "Post 2", "Desc 2"));

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Post 1"))
                .andExpect(jsonPath("$[1].title").value("Post 2"));
    }

    @Test
    public void getPostById() throws Exception {
        Post post = postRepository.save(new Post(null, "Single Post", "Single Desc"));

        mockMvc.perform(get("/api/v1/post/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Single Post"))
                .andExpect(jsonPath("$.description").value("Single Desc"));
    }

    @Test
    public void getPostById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/post/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Post with id 999 not found!")));
    }

    @Test
    public void updatePost() throws Exception {
        Post post = postRepository.save(new Post(null, "Old Title", "Old Desc"));

        String updateJson = """
            {
                "title": "New Title",
                "description": "New Desc"
            }
            """;

        mockMvc.perform(put("/api/v1/post/{id}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.description").value("New Desc"));

        // Verify DB is updated
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals("New Title", updatedPost.getTitle());
        assertEquals("New Desc", updatedPost.getDescription());
    }

    @Test
    public void updatePost_NotFound() throws Exception {
        String updateJson = """
            {
                "title": "Doesn't Matter",
                "description": "Not Found"
            }
            """;

        mockMvc.perform(put("/api/v1/post/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Post with id 999 not found!")));
    }

    @Test
    public void deletePost() throws Exception {
        Post post = postRepository.save(new Post(null, "To Delete", "Desc"));

        mockMvc.perform(delete("/api/v1/post/{id}", post.getId()))
                .andExpect(status().isNoContent());

        // Verify DB deletion
        assertEquals(0, postRepository.count());
    }

    @Test
    public void deletePost_NotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/post/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Post with id 999 not found!")));
    }
}