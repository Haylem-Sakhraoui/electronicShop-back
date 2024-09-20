package shop.com.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.com.Entity.User;
import shop.com.Repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user= userRepository.findByEmail(email).orElse(null);

        if(user==null) {
            throw new UsernameNotFoundException(email + "not found");
        }

        UserDetails u= new org.springframework.security.core.userdetails.User(email,
                user.getPassword(),
                true,
                true,
                true,
                true,
                getAuthority(user));
        System.out.println("u.toString()==> "+ u.toString());

        if (!u.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        return u;
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
        return authorities;
    }
}