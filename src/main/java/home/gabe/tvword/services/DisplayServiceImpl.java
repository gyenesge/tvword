package home.gabe.tvword.services;

import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.Status;
import home.gabe.tvword.model.User;
import home.gabe.tvword.model.UserRole;
import home.gabe.tvword.model.web.ModifyDisplayCommand;
import home.gabe.tvword.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class DisplayServiceImpl implements DisplayService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public DisplayServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        log.debug("Display service is initialized with repository {}.", userRepository.getClass().getName());
    }

    @Override
    public Display findById(Long id) {

        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty())
            throw new NoSuchElementException("Display with the following id does not exist:" + id);

        User user = optional.get();
        if (!(user instanceof Display))
            throw new NoSuchElementException("The provided ID is not a display: " + id + " (" + user.getClass().getName() + ")");
        return (Display) user;
    }

    @Override
    public Set<Display> findAll(boolean showDeleted) {
        Iterable<User> all = userRepository.findByRole(UserRole.DISPLAY);

        Set<Display> result = new HashSet<>();
        all.forEach(user -> {
            if ((showDeleted || user.getStatus().equals(Status.ACTIVE)) && user instanceof Display)
                result.add((Display) user);
        });

        return result;
    }

    @Override
    public Display register(String name, String note, String password) {
        if (name == null || password == null)
            throw new NullPointerException("Display cannot be created with null name or password.");

        var display = new Display();
        display.setName(name);
        display.setNote(note);
        display.setHashedPassword(passwordEncoder.encode(password));

        return userRepository.save(display);
    }

    @Override
    public void delete(Display display) {
        userRepository.delete(display);
    }

    @Override
    public Display update(ModifyDisplayCommand command) {
        Optional<User> optional = userRepository.findById(command.getId());
        if (optional.isEmpty() || !(optional.get() instanceof Display))
            throw new IllegalArgumentException("Unknown display ID: " + command.getId());

        Display display = (Display) optional.get();
        if (command.getPassword1() != null && command.getPassword1().trim().length() > 0) {
            if (!command.getPassword1().equals(command.getPassword2()))
                throw new IllegalArgumentException("Password mismatch.");

            display.setHashedPassword(passwordEncoder.encode(command.getPassword1()));
        }
        display.setNote(command.getNote());
        display.setStatus(Status.parse(command.getStatus()));

        return userRepository.save(display);
    }
}
