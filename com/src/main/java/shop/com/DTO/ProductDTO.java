package shop.com.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import shop.com.Entity.Category;
import shop.com.Entity.Product;

@AllArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private int price;
    private String image;
    private Category category;


    @JsonCreator
    public ProductDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("price") int price,
            @JsonProperty("category") Category category,
            @JsonProperty("image") String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.image = image;
    }

    public ProductDTO(Product savedProduct) {
    }
}
