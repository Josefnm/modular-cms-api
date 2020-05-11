package se.josef.cmsapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import se.josef.cmsapi.firebase.FirebaseTokenAuthenticationFilter;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
@Profile("!test")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.permitAll}")
    private String[] allowedUris;

    /**
     * configures types/origin of requests allowed
     *
     * @return
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE"));
        config.setAllowCredentials(true);
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("content-type");
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * basic security configuration
     */
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .authorizeRequests()
                // allowed paths loaded from properties
                .antMatchers(allowedUris).permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .addFilterBefore(new FirebaseTokenAuthenticationFilter(), BasicAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().and()
                .csrf().disable();
    }
}