package com.ecommerce.api.controllers;

import com.ecommerce.api.exception.NotFoundException;
import com.ecommerce.api.model.persistence.Cart;
import com.ecommerce.api.model.persistence.Item;
import com.ecommerce.api.model.persistence.User;
import com.ecommerce.api.model.persistence.repositories.CartRepository;
import com.ecommerce.api.model.persistence.repositories.ItemRepository;
import com.ecommerce.api.model.persistence.repositories.UserRepository;
import com.ecommerce.api.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
        log.info("Adding item to cart item ID: {}, quantity: {} for user: {}", request.getItemId(), request.getQuantity(), request.getUsername());

        User user = Optional.ofNullable(userRepository.findByUsername(request.getUsername())).orElseThrow(() -> {
            log.error("Username '{}' not found.", request.getUsername());
            return new NotFoundException(String.format("Username '%s' not found.", request.getUsername()));
        });

        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            log.error("Item with ID '{}' not found. ", request.getItemId());
            throw new NotFoundException(String.format("Item with ID '%s' not found. ", request.getItemId()));
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);
        log.info("Added item to the cart successfully.");
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
        log.info("Removing item from cart item ID: {}, quantity: {} for user: {}", request.getItemId(), request.getQuantity(), request.getUsername());

        User user = Optional.ofNullable(userRepository.findByUsername(request.getUsername())).orElseThrow(() -> {
            log.error("Username '{}' not found.", request.getUsername());
            return new NotFoundException(String.format("Username '%s' not found.", request.getUsername()));
        });
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            log.error("Item with ID '{}' not found. ", request.getItemId());
            throw new NotFoundException(String.format("Item with ID '%s' not found. ", request.getItemId()));
        }
        Cart cart = user.getCart();
        if (cart.getItems().isEmpty()) {
            log.error("No items in the cart to remove for user: {}", user.getUsername());
            throw new NotFoundException(String.format("No items in the cart to remove for user: %s", user.getUsername()));
        }

        if (!cart.getItems().contains(item.get())) {
            log.error("Item ID: {} not found in the cart for user: {}", item.get().getId(), user.getUsername());
            throw new NotFoundException(String.format("Item ID: %d not found in the cart for user: %s", item.get().getId(), user.getUsername()));
        }

        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);
        log.info("Removed item: {} from the cart successfully.", item.get());
        return ResponseEntity.ok(cart);
    }

}
