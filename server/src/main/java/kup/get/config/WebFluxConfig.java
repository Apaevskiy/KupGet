package kup.get.config;

import kup.get.service.security.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class WebFluxConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return
                http.authorizeExchange(exchanges  -> exchanges
                                .pathMatchers("/update/**").hasRole("UPDATE")
                                .pathMatchers("/asu/**").hasRole("ASU")
                                .pathMatchers("/traffic/**").hasAnyRole("TRAFFIC", "ASU")
                                .pathMatchers("/energy/**").hasAnyRole("ENERGY", "ASU")
                                .pathMatchers("/info").permitAll()
                                .anyExchange().authenticated()
                                .and().csrf().disable())
                        .httpBasic(withDefaults())
                        .formLogin(withDefaults())
                        .build();
    }
}
