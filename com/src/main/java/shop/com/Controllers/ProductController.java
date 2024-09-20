package shop.com.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import shop.com.DTO.ProductDTO;
import shop.com.Entity.Product;
import shop.com.Repository.ProductRepository;
import shop.com.Services.GoogleDriveService;
import shop.com.Services.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:4200")
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private GoogleDriveService googleDriveService;
    @Autowired
    ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

//    @PostMapping(path = "/upload", consumes = "multipart/form-data")
//    public ResponseEntity<ProductDTO> uploadProduct(
//            @RequestPart("productDTO") ProductDTO productDTO,
//            @RequestPart("file") MultipartFile file) throws IOException {
//        ProductDTO uploadedProduct = productService.uploadProduct(productDTO, file);
//        return new ResponseEntity<>(uploadedProduct, HttpStatus.CREATED);
//    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public Product uploadFile(
            @RequestPart("productDTO") String productDTOString,
            @RequestPart("file") MultipartFile file) throws IOException {

        // Log received parts
        System.out.println("Received productDTO: " + productDTOString);
        System.out.println("Received file: " + file.getOriginalFilename());

        // Deserialize the productDTO
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDTO productDTO = objectMapper.readValue(productDTOString, ProductDTO.class);

        // Upload the image to Google Drive and get the URL
        String imageUrl = productService.uploadImageToGoogleDrive(file);

        // Create a new Product and set its properties
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setImage(imageUrl);
        // Save the product to the database
        Product savedProduct = productRepository.save(product);
        return  savedProduct;
    }

    @PutMapping(path = "/update/{id}", consumes = "multipart/form-data")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestPart("productDTO") String productDTOString,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        // Log received parts
        System.out.println("Received productDTO: " + productDTOString);
        if (file != null) {
            System.out.println("Received file: " + file.getOriginalFilename());
        }

        // Deserialize the productDTO
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDTO productDTO = objectMapper.readValue(productDTOString, ProductDTO.class);

        // Fetch the existing product by its ID
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (!existingProductOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        Product existingProduct = existingProductOpt.get();

        // Update product properties
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setCategory(productDTO.getCategory());

        // If a new file is uploaded, update the image URL
        if (file != null) {
            // Upload the image to Google Drive and get the URL
            String imageUrl = productService.uploadImageToGoogleDrive(file);
            existingProduct.setImage(imageUrl);
        }

        // Save the updated product to the database
        Product updatedProduct = productRepository.save(existingProduct);
        return updatedProduct;
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteProducts(@RequestBody List<Long> ids) {
        productService.deleteProducts(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}