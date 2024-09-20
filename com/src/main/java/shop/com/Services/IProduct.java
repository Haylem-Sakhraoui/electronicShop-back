package shop.com.Services;

import org.springframework.web.multipart.MultipartFile;
import shop.com.DTO.ProductDTO;
import shop.com.DTO.ProductRequestDTO;
import shop.com.Entity.Product;

import java.io.IOException;
import java.util.List;

public interface IProduct {
    List<Product> getAllProducts();

    Product getProductById(Long id);

    void deleteProduct(Long id);

    public Product addProduct(ProductDTO productDTO)throws IOException;
}
