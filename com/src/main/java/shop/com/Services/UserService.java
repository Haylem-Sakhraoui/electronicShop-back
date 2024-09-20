package shop.com.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import shop.com.DTO.ForgetPassword;
import shop.com.DTO.Mail;
import shop.com.Entity.Role;
import shop.com.Entity.Token;
import shop.com.Entity.User;
import shop.com.DTO.resetPasswordRequest;
import shop.com.Repository.TokenRepository;
import shop.com.Repository.UserRepository;
import shop.com.config.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor()
public class UserService implements IUser{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserDetailsService userDetailsService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public List<User> retrieveAllClient() {

        return (List<User>) userRepository.findByRole(Role.CLIENT);
    }

    @Override
    public Optional<User> getCurrentUser(String token) {
        String username = jwtService.extractUsername(token);
        return userRepository.findByEmail(username);
    }
    @Override
    public Optional<User> retrieveUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void resetPassword(resetPasswordRequest request) {
        // Retrieve user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check if the current password matches
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Check if the new password matches the confirmed password
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        // Update user's password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Save the updated user
        userRepository.save(user);
    }
    @Override
    public void forgetPassword(ForgetPassword forgetPassword) throws jakarta.mail.MessagingException {
        Optional<User> optionalUser = userRepository.findByEmail(forgetPassword.getEmail());
        if (optionalUser.isPresent()) {
            User user =optionalUser.get();
            // Generate a new random password

            final UserDetails userDetails = userDetailsService.loadUserByUsername(forgetPassword.getEmail());
            String token = jwtService.generateToken(new HashMap<>(),userDetails);

            // Create a new Token object and set the generated token
            Token userToken = user.getToken();
            if (userToken == null) {
                userToken = new Token();
                user.setToken(userToken);
            }
            userToken.setToken(token);

            // Save the Token object first
            tokenRepository.save(userToken);

            String newPassword = generateRandomPassword();

            // Encrypt the new password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(newPassword);

            // Update the user's password
            user.setPassword(encodedPassword);

            userRepository.save(user);

            final String subject = "Reset Password";
            String body = "<div><h3>Hello " + user.getFirstname() +" "+user.getLastname()+ " </h3>" +
                    "<br>" +
                    "<h4>A password reset request has been requested for your user account  </h4>" +
                    "<h4>This your new password : "+ newPassword + "</h4>" +                    "<h4>If you need help, please contact the website administrator.</h4>" +
                    "<br>" +
                    "<h4>Admin</h4></div>";
            Mail mail = new Mail(forgetPassword.getEmail(), subject, body);
            emailService.sendMail(mail);
        } else {
            // Log the error
            System.out.println("User not found for email: " + forgetPassword.getEmail());
            throw new NotFoundException("User not found");
        }
    }
    public void DeleteById(Long id){
        userRepository.deleteById(id);
    }


    private String generateResetToken() {
        // Implement your logic to generate a unique password reset token
        // This can be a random string or a token generated based on user information
        // For example, you can use UUID.randomUUID().toString() to generate a random token
        return UUID.randomUUID().toString();
    }

    private String generateResetLink(String email, String resetToken) {
        // Implement your logic to generate the password reset link
        // This link should contain the user's email and the reset token
        // For example, you can construct the link like: "/reset-password?email=" + email + "&token=" + resetToken
        return "/reset-password?email=" + email + "&token=" + resetToken;
    }

    public class UnauthorizedUserException extends RuntimeException {
        public UnauthorizedUserException(String message) {
            super(message);
        }
    }

    private boolean userAlreadyExist(String email){
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private static final Random RANDOM = new SecureRandom();
    private String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

}
