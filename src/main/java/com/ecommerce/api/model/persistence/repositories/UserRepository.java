package com.ecommerce.api.model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.api.model.persistence.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
