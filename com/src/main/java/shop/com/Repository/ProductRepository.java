package shop.com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.com.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
