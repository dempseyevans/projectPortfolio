package com.cookery.cookery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookery.cookery.entity.User;




public interface UserRepository extends JpaRepository<User, Long>{

    User findByUsername(String username);
    User findByEmail(String email);

}
