package shop.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages ={
		"shop.com.Controllers",
		"shop.com.Services",
		"shop.com.Repository",
		"shop.com.auth",
		"shop.com.config"


})
@EnableJpaRepositories(basePackages = {"shop.com.Repository"})
@EntityScan(basePackages = {"shop.com.Entity"})
@EnableAspectJAutoProxy
public class ShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopApplication.class, args);
	}

}
