package home.gabe.tvword.configuration;

import home.gabe.tvword.repositories.TokenRepository;
import home.gabe.tvword.services.TvUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private PasswordEncoder passwordEncoder;
    private TvUserDetailsService userDetailsService;
    private TokenRepository tokenRepository;

    @Autowired
    public void setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Autowired
    public void setUserDetailsService(TvUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/idokep/**").hasRole("ADMIN")
                .antMatchers("/displays").hasRole("DISPLAY")
                .antMatchers("/displays/**").hasRole("DISPLAY")
                .antMatchers("/campaigns/**").hasRole("DISPLAY")
                //.antMatchers("/h2-console/**").permitAll()
                .antMatchers("/login**").permitAll()
                .anyRequest().authenticated()

                .and()

                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/start")
                .failureUrl("/login?error=true")

                .and()

                .logout()
                .logoutUrl("/perform_logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login")

                .and()

                .rememberMe()
                .key("6ax-;SYLE*m5Wv::xUvDnD%")
                .tokenRepository(tokenRepository)
                .userDetailsService(userDetailsService)
        ;

        //http.headers().frameOptions().disable();
        //http.csrf().disable();
    }
}
