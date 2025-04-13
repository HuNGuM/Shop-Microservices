import com.hungum.common.dto.ProductSearchResponseDto;
import com.hungum.product.service.SearchService;
import com.hungum.product.repository.ProductRepository;
import com.hungum.product.repository.CategoryRepository;
import com.hungum.common.dto.ProductDto;
import com.hungum.product.model.Product;
import com.hungum.product.model.Category;
import com.hungum.common.dto.SearchQueryDto;
import com.hungum.common.exceptions.ShopException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchCategoryNotFound() {
        // Arrange
        when(categoryRepository.findByName("NonExistentCategory")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShopException.class, () -> searchService.searchWithFilters(new SearchQueryDto(), "NonExistentCategory"));
    }
}
