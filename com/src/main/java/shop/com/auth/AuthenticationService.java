package shop.com.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shop.com.DTO.*;
import shop.com.Entity.Role;
import shop.com.Entity.User;
import shop.com.Repository.UserRepository;
import shop.com.config.JwtService;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public AuthenticationResponse register(RegisterRequest request) {

        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .role(Role.CLIENT)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(new HashMap<>(),user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(AuthenticateRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(request.getEmail());


            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow();
            var jwtToken = jwtService.generateToken(new HashMap<>(),user);
            return new AuthResponse(200,AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build(), "login successfully");

        }catch (BadCredentialsException e) {
            return new AuthResponse(404, "Invalid email or password");
        }


    }


}
