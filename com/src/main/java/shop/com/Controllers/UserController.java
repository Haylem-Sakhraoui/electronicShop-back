package shop.com.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.com.DTO.ForgetPassword;
import shop.com.DTO.resetPasswordRequest;
import shop.com.Entity.User;
import shop.com.Services.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:4200")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/currentUser")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        Optional<User> currentUser = userService.getCurrentUser(jwtToken);
        return currentUser.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/forgetPassword")
    public void forgetPassword(@RequestBody ForgetPassword forgetPassword) throws Exception  {
        userService.forgetPassword(forgetPassword);
    }
    @PostMapping("/resetPassword")
    public void resetPassword(@RequestBody resetPasswordRequest request)  {
        userService.resetPassword(request);
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<User>> findAll() {
        List<User> clients = userService.retrieveAllClient();
        return ResponseEntity.ok(clients);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(Long id) {
        userService.DeleteById(id);
        return ResponseEntity.noContent().build();
    }
}
