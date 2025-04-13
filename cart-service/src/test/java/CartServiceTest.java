import com.hungum.cart.CartApplication;
import com.hungum.cart.model.ShoppingCartItem;
import com.hungum.cart.service.CartService;
import com.hungum.cart.model.ShoppingCart;
import com.hungum.cart.repository.CartRepository;
import com.hungum.common.dto.ProductDto;
import com.hungum.cart.kafka.KafkaProducerService;
import com.hungum.common.event.ProductRequestEvent;
import com.hungum.common.event.UserRequestEvent;
import com.hungum.common.event.UserResponseEvent;
import com.hungum.common.exceptions.ShopException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CartApplication.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCartForCurrentUser() {
        // Arrange
        String username = "user";
        ShoppingCart expectedCart = new ShoppingCart(null, null, BigDecimal.ZERO, 0, username);
        when(cartRepository.findByUsername(username)).thenReturn(expectedCart);

        // Act
        ShoppingCart cart = cartService.getCartForCurrentUser();

        // Assert
        assertNotNull(cart);
        assertEquals(username, cart.getUsername());
    }


    @Test
    public void testAddToCart_ProductNotFound() {
        // Arrange
        String sku = "12345";
        when(kafkaProducerService.sendProductRequestEvent(any(ProductRequestEvent.class))).thenReturn(null);

        // Act & Assert
        assertThrows(ShopException.class, () -> cartService.addToCart(sku));
    }


    @Test
    public void testRemoveFromCart_ItemNotFound() {
        // Arrange
        String productName = "Nonexistent Product";
        ShoppingCart cart = new ShoppingCart(null, new HashSet<>(), BigDecimal.ZERO, 0, "user");
        when(cartRepository.findByUsername(anyString())).thenReturn(cart);

        // Act & Assert
        assertThrows(ShopException.class, () -> cartService.removeFromCart(productName));
    }


}
