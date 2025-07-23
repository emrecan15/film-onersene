package com.filmonersene.website.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filmonersene.website.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
