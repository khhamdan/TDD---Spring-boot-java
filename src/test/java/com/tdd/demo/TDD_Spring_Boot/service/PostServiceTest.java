package com.tdd.demo.TDD_Spring_Boot.service;

import com.tdd.demo.TDD_Spring_Boot.model.Post;
import com.tdd.demo.TDD_Spring_Boot.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        posts = List.of(new Post(1, "First Post", "Spring Boot TDD"), new Post(2, "Second Post", "Mastering TDD"));
    }

    @Test
    public void findAllPosts() {
        when(postRepository.findAll()).thenReturn(posts);

        List<Post> posts = postService.getAllPosts();

        assertNotNull(posts);
        assertEquals(posts.size(), 2);
    }

    @Test
    public void findPostById() {
        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));
        Post post = postService.getPostById(1);
        assertNotNull(post);
    }

    @Test
    void findPostById_NotFound() {
        int id = 999;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> postService.getPostById(id));

        assertEquals("Post with id 999 not found!", ex.getMessage());
    }

    @Test
    public void savePost() {
        when(postRepository.save(any(Post.class))).thenReturn(posts.get(0));
        Post post = postService.savePost(new Post(1, "First Post", "Spring Boot TDD"));
        assertNotNull(post);
        assertEquals( "First Post", post.getTitle());
    }

    @Test
    void deletePost() {
        int postId = 1;
        Post post = posts.get(0);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.deletePost(postId);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void deletePost_NotFound() {
        int id = 999;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> postService.deletePost(id));

        assertEquals("Post with id 999 not found!", ex.getMessage());

        verify(postRepository, never()).delete(any());
    }

    @Test
    void updatePost() {
        int id = 1;

        Post existingPost = new Post(1, "Old Title", "Old Description");
        Post updateRequest = new Post(1, "New Title", "New Description");

        when(postRepository.findById(id)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(existingPost);

        Post result = postService.updatePost(id, updateRequest);
        assertEquals("New Title", existingPost.getTitle());
        assertEquals("New Description", existingPost.getDescription());

        verify(postRepository).save(existingPost);

        assertEquals(existingPost, result);
    }

    @Test
    void updatePost_NotFound() {
        int id = 999;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex =
                assertThrows(RuntimeException.class,
                        () -> postService.updatePost(id, new Post()));

        assertEquals("Post with id 999 not found!", ex.getMessage());

        verify(postRepository, never()).save(any());
    }

}
