package com.hungum.cart.service;

import com.hungum.common.exceptions.ShopException;
import com.hungum.cart.model.ShoppingCart;
import com.hungum.cart.model.ShoppingCartItem;
import com.hungum.common.dto.ProductDto;
import com.hungum.cart.kafka.KafkaProducerService;
import com.hungum.common.event.ProductRequestEvent;
import com.hungum.common.event.UserRequestEvent;
import com.hungum.common.event.UserResponseEvent;
import com.hungum.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final KafkaProducerService kafkaProducerService;

    public String getCurrentUsernameFromAuthService() {
        String username = "user";
        UserRequestEvent event = new UserRequestEvent(username);
        UserResponseEvent responseEvent = kafkaProducerService.sendUserRequestEvent(event);
        if (responseEvent != null) {
            username = responseEvent.getUsername();
        }
        return username;
    }
    public ShoppingCart getCartForCurrentUser() {
        String username = getCurrentUsernameFromAuthService();


        return cartRepository.findByUsername(username) != null
                ? cartRepository.findByUsername(username)
                : new ShoppingCart(null, new HashSet<>(), BigDecimal.ZERO, 0, username);
    }

    @Transactional
    public void addToCart(String sku) {

        ProductRequestEvent productRequestEvent = new ProductRequestEvent(sku);
        ProductDto productDto = kafkaProducerService.sendProductRequestEvent(productRequestEvent);

        if (productDto == null) {
            throw new ShopException("Product not found");
        }
        ShoppingCart cart = getCartForCurrentUser();
        ShoppingCartItem item = new ShoppingCartItem();
        item.setName(productDto.getProductName());
        item.setPrice(productDto.getPrice());
        cart.getShoppingCartItems().add(item);
        cart.setNumberOfItems(cart.getShoppingCartItems().size());
        cart.setCartTotalPrice(cart.getShoppingCartItems().stream()
                .map(ShoppingCartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        cartRepository.save(cart);
    }
    public void removeFromCart(String productName) {


        ShoppingCart cart = getCartForCurrentUser();


        boolean removed = cart.getShoppingCartItems().removeIf(item -> item.getName().equals(productName));
        if (!removed) {
            throw new ShopException("Item not found in cart: " + productName);
        }


        cart.setNumberOfItems(cart.getShoppingCartItems().size());
        cart.setCartTotalPrice(cart.getShoppingCartItems().stream()
                .map(ShoppingCartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        cartRepository.save(cart);
    }

    public void clearCart() {
        ShoppingCart cart = getCartForCurrentUser();
        cart.getShoppingCartItems().clear();
        cart.setCartTotalPrice(BigDecimal.ZERO);
        cart.setNumberOfItems(0);
        cartRepository.save(cart);
    }

    public ShoppingCart getCart() {
        return getCartForCurrentUser();
    }
}
