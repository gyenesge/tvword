package home.gabe.tvword.repositories;

import home.gabe.tvword.model.PersistentLogin;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public class TokenRepository implements PersistentTokenRepository {
    private PersistentLoginRepository loginRepository;

    public TokenRepository(PersistentLoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken persistentRememberMeToken) {
        PersistentLogin login = new PersistentLogin();
        login.setSeries(persistentRememberMeToken.getSeries());
        login.setLastUsed(persistentRememberMeToken.getDate());
        login.setUsername(persistentRememberMeToken.getUsername());
        login.setToken(persistentRememberMeToken.getTokenValue());
        loginRepository.save(login);
    }

    @Override
    public void updateToken(String series, String token, Date lastUsed) {
        Optional<PersistentLogin> optional = loginRepository.findById(series);
        PersistentLogin login = optional.get();
        login.setToken(token);
        login.setLastUsed(lastUsed);
        loginRepository.save(login);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String series) {
        Optional<PersistentLogin> login = loginRepository.findById(series);

        if (login.isEmpty())
            return null;
        PersistentLogin l = login.get();
        return new PersistentRememberMeToken(l.getUsername(), l.getSeries(), l.getToken(), l.getLastUsed());
    }

    @Override
    public void removeUserTokens(String username) {
        loginRepository.deleteByUsername(username);
    }
}
