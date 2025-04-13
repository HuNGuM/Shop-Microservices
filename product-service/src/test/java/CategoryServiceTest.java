import com.hungum.product.service.CategoryService;
import com.hungum.product.repository.CategoryRepository;
import com.hungum.common.dto.CategoryDto;
import com.hungum.product.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        // Arrange
        Category category1 = new Category("Electronics");
        Category category2 = new Category("Furniture");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        // Act
        List<CategoryDto> categories = categoryService.findAll();

        // Assert
        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("Electronics", categories.get(0).getCategoryNames());
        assertEquals("Furniture", categories.get(1).getCategoryNames());
    }
}
