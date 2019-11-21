package home.gabe.tvword.services;

import home.gabe.tvword.model.User;
import home.gabe.tvword.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TvUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public TvUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByName(name);
        if (user == null)
            throw new UsernameNotFoundException(name);
        return BaseUserPrincipal.createPrincipal(user);
    }
}
