package com.tdd.demo.TDD_Spring_Boot.controller;

import com.tdd.demo.TDD_Spring_Boot.exception.PostNotFoundException;
import com.tdd.demo.TDD_Spring_Boot.model.Post;
import com.tdd.demo.TDD_Spring_Boot.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PostControllerTest {

    private List<Post> posts = new ArrayList<>();

    @MockitoBean
    private PostService postService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        posts = List.of(new Post(1, "First Post", "Spring Boot TDD"), new Post(2, "Second Post", "Mastering TDD"));
    }

    @Test
    public void findAllPosts() throws Exception {
        String jsonResponse = """
                [{"id":1,"title":"First Post","description":"Spring Boot TDD"}, { "id":2,"title":"Second Post","description":"Mastering TDD"}]""";
        when(postService.getAllPosts()).thenReturn(posts);
        ResultActions resultActions = mockMvc.perform(get("/api/v1/posts")).andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    public void findPostById() throws Exception {
        String jsonResponse = """
                {"id":1,"title":"First Post","description":"Spring Boot TDD"}
                """;
        when(postService.getPostById(1)).thenReturn(new Post(1, "First Post", "Spring Boot TDD"));
        ResultActions resultActions = mockMvc.perform(get("/api/v1/post/{id}", 1)).andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    public void findPostById_NotFound() throws Exception {
        int id = 999;

        when(postService.getPostById(id))
                .thenThrow(new RuntimeException("Post with id " + id + " not found!"));

        mockMvc.perform(get("/api/v1/post/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
                .andExpect(result -> assertEquals(
                        "Post with id 999 not found!",
                        result.getResolvedException().getMessage()
                ));
    }


    @Test
    public void savePost() throws Exception {
        int postId = 1;
        String jsonRequest = """
                {"id":1,"title":"First Post","description":"Spring Boot TDD"}
                """;
        String jsonResponse = """
                {"id":1,"title":"First Post","description":"Spring Boot TDD"}
                """;
        when(postService.savePost(any(Post.class))).thenReturn(new Post(postId, "First Post", "Spring Boot TDD"));
        ResultActions resultActions = mockMvc.perform(post("/api/v1/post").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonResponse));
        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    public void deletePost() throws Exception {
        int postId = 1;
        mockMvc.perform(delete("/api/v1/post/{id}", postId))
                .andExpect(status().isNoContent());
        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    public void deletePost_NotFound() throws Exception {
        int id = 999;

        doThrow(new PostNotFoundException("Post with id " + id + " not found!"))
                .when(postService).deletePost(id);

        mockMvc.perform(delete("/api/v1/post/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(
                        "Post with id 999 not found!",
                        result.getResponse().getContentAsString()
                ));
    }


    @Test
    void updatePost() throws Exception {
        int id = 1;

        Post updatedPost = new Post(1, "Updated Title", "Updated Description");

        when(postService.updatePost(eq(id), any(Post.class))).thenReturn(updatedPost);

        String requestJson = """
            {
                "id": 1,
                "title": "Updated Title",
                "description": "Updated Description"
            }
            """;

        String expectedJson = """
            {
                "id": 1,
                "title": "Updated Title",
                "description": "Updated Description"
            }
            """;

        mockMvc.perform(put("/api/v1/post/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(postService, times(1)).updatePost(eq(id), any(Post.class));
    }

    @Test
    public void updatePost_NotFound() throws Exception {
        int id = 999;

        String requestJson = """
        {
            "id": 999,
            "title": "Doesn't matter",
            "description": "Not found test"
        }
        """;

        when(postService.updatePost(eq(id), any(Post.class)))
                .thenThrow(new PostNotFoundException("Post with id " + id + " not found!"));

        mockMvc.perform(put("/api/v1/post/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(
                        "Post with id 999 not found!",
                        result.getResponse().getContentAsString()
                ));
    }


}
