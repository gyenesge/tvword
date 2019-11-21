package home.gabe.tvword.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityController extends WebSecurityConfigurerAdapter {

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

 /*   @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .and()
                .withUser("display")
                .password(passwordEncoder.encode("password"))
                .roles("DISPLAY")
                .and()
                .withUser("admin")
                .password(passwordEncoder.encode("password"))
                .roles("ADMIN")
        ;
    }
*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/displays").hasRole("DISPLAY")
                .antMatchers("/displays/**").hasRole("DISPLAY")
                .antMatchers("/campaigns/**").hasRole("DISPLAY")
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/login/display**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login/display")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/displays/start")
                .failureUrl("/login/display?error=true")
                .and()
                .logout()
                .logoutUrl("/perform_logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login/display")
                .and()
                .rememberMe().key("6ax-;SYLE*m5Wv::xUvDnD%")
        ;

        http.headers().frameOptions().disable();
        http.csrf().disable();
    }
}
