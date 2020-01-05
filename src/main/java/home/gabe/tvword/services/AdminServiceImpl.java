package home.gabe.tvword.services;

import home.gabe.tvword.controllers.TVWordException;
import home.gabe.tvword.model.Admin;
import home.gabe.tvword.model.User;
import home.gabe.tvword.model.web.PasswordCommand;
import home.gabe.tvword.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(User user, PasswordCommand command) {
        //verify input
        String oldp = command.getOldPassword();
        String newp = command.getNewPassword();
        String confirmp = command.getConfirmPassword();
        if (oldp.isEmpty() || newp.isEmpty() || confirmp.isEmpty())
            throw new TVWordException("Some of the passwords are missing or empty.", TVWordException.EC_PASSWORD_MISSING);
        if (!newp.equals(confirmp))
            throw new TVWordException("Confirm password does not match new password.", TVWordException.EC_PASSWORD_MISMATCH);


        //load current record
        Optional<User> optional = userRepository.findById(user.getId());
        if (optional.isEmpty() || !(optional.get() instanceof Admin))
            throw new IllegalArgumentException("Invalid user record as principal: id=" + user.getId());
        User admin = optional.get();

        //verify old password
        if (!passwordEncoder.matches(oldp, admin.getHashedPassword()))
            throw new TVWordException("Old password is invalid.", TVWordException.EC_INVALID_PASSWORD);

        //update fields
        admin.setPassword(newp);
        admin.setHashedPassword(passwordEncoder.encode(newp));

        //save object
        userRepository.save(admin);
    }
}
