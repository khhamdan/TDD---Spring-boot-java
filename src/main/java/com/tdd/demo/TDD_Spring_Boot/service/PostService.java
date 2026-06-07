package com.tdd.demo.TDD_Spring_Boot.service;

import com.tdd.demo.TDD_Spring_Boot.exception.PostNotFoundException;
import com.tdd.demo.TDD_Spring_Boot.model.Post;
import com.tdd.demo.TDD_Spring_Boot.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(int id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post with id "  + id  + " not found!"));
        return post;
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(int id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post with id "  + id  + " not found!"));
        postRepository.delete(post);
    }

    public Post updatePost(int id, Post post) {
        Post savedPost = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post with id " + id  + " not found!"));
        if(savedPost != null) {
            savedPost.setTitle(post.getTitle());
            savedPost.setDescription(post.getDescription());
        }
        return postRepository.save(savedPost);
    }
}
