package com.tdd.demo.TDD_Spring_Boot.repository;

import com.tdd.demo.TDD_Spring_Boot.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {
}
