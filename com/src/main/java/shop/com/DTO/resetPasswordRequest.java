package shop.com.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class resetPasswordRequest {
    private String email;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

}
