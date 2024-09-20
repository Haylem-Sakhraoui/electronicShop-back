package shop.com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.com.Entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
