package com.filmonersene.website.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filmonersene.website.entities.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{

}
