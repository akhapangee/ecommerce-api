package com.ecommerce.api.model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.api.model.persistence.Cart;
import com.ecommerce.api.model.persistence.User;

import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(User user);
}
