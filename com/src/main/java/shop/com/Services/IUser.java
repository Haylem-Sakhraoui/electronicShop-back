package shop.com.Services;

import shop.com.DTO.ForgetPassword;
import shop.com.DTO.resetPasswordRequest;
import shop.com.Entity.User;

import java.util.List;
import java.util.Optional;

public interface IUser {
    List<User> retrieveAllClient();

    Optional<User> getCurrentUser(String token);

    Optional<User> retrieveUserByEmail(String email);

    void resetPassword(resetPasswordRequest request);

    void forgetPassword(ForgetPassword forgetPassword) throws jakarta.mail.MessagingException;
}
