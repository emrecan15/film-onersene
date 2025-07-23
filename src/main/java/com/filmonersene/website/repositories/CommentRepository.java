package com.filmonersene.website.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filmonersene.website.entities.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

}
