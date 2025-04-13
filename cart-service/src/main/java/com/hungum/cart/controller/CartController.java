package com.hungum.cart.controller;

import com.hungum.cart.model.ShoppingCart;
import com.hungum.cart.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart/")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("add/{sku}")
    public ResponseEntity<Void> addToCart(@PathVariable String sku) {
        cartService.addToCart(sku);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("remove/{productName}")
    public ResponseEntity<Void> removeFromCart(@PathVariable String productName) {
        cartService.removeFromCart(productName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ShoppingCart> getCart() {
        ShoppingCart cart = cartService.getCart();
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }
}
