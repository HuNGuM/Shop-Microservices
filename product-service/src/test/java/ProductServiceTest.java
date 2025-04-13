import com.hungum.product.service.ProductService;
import com.hungum.product.repository.ProductRepository;
import com.hungum.product.repository.CategoryRepository;
import com.hungum.product.model.Product;
import com.hungum.product.model.ProductRating;
import com.hungum.common.dto.ProductDto;
import com.hungum.common.dto.ProductRatingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("12345");
        product.setPrice(BigDecimal.valueOf(100));
        product.setQuantity(10);  // Убедитесь, что quantity задано

        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        // Act
        List<ProductDto> products = productService.findAll();

        // Assert
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Test Product", products.get(0).getProductName());
    }

    @Test
    public void testReadOneProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("12345");
        product.setPrice(BigDecimal.valueOf(100));
        product.setQuantity(10);  // Убедитесь, что quantity задано

        when(productRepository.findBySku("12345")).thenReturn(Optional.of(product));

        // Act
        ProductDto productDto = productService.readOneProduct("12345");

        // Assert
        assertNotNull(productDto);
        assertEquals("Test Product", productDto.getProductName());
    }

    @Test
    public void testSaveProduct() {
        // Arrange
        ProductDto productDto = new ProductDto("Test Product", "image_url", "12345", BigDecimal.valueOf(100), "Description", "Manufacturer", null, null, false, null);

        // Act
        productService.save(productDto);

        // Assert
        verify(productRepository, times(1)).save(any(Product.class)); // Verify save is called once
    }
}
