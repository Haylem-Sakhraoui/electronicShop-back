package shop.com.Services;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import shop.com.DTO.CategoryDTO;
import shop.com.DTO.ProductDTO;
import shop.com.DTO.ProductRequestDTO;
import shop.com.Entity.Product;
import shop.com.Repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Value("${google.drive.folder.id}")
    private String folderId;

    @Autowired
    private Drive googleDrive;

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public ProductDTO uploadProduct(ProductDTO productDTO) throws IOException {

        // Convert DTO to entity
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        // Save product to the database
        product = productRepository.save(product);

        // Convert entity back to DTO and update ID
        productDTO.setId(product.getId());

        return productDTO;
    }
    public String uploadImageToGoogleDrive(MultipartFile imageFile) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("temp", ".jpg");
        try (InputStream inputStream = imageFile.getInputStream()) {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, tempFile);
        }

        File fileMetadata = new File();
        fileMetadata.setName(tempFile.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));

        FileContent mediaContent = new FileContent("image/jpeg", tempFile);
        File file = googleDrive.files().create(fileMetadata, mediaContent)
                .setFields("id, webContentLink, webViewLink")
                .execute();
        String imageUrl = "https://drive.google.com/thumbnail?id="+file.getId();
        tempFile.delete();

        return imageUrl;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void deleteProducts(List<Long> ids){
        productRepository.deleteAllById(ids);
    }
}
