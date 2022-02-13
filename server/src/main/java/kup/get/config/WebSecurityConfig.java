package kup.get.config;

import kup.get.entity.postgres.security.User;
import kup.get.service.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableRSocketSecurity
//@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final SimpleAuthenticationSuccessHandler successHandler;

    public WebSecurityConfig(UserService userService, SimpleAuthenticationSuccessHandler successHandler) {
        this.userService = userService;
        this.successHandler = successHandler;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .ignoringAntMatchers("/traffic/**")
                .and()
//                    .disable()
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                    .antMatchers("/superAdmin/**").hasRole("SUPERADMIN")
                    .antMatchers("/", "/login/**", "/traffic/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login").successHandler(successHandler)
                    .permitAll()
                .and()
                    .logout()
                    .permitAll()
                    .logoutSuccessUrl("/login");
    }

    @Bean
    RSocketMessageHandler messageHandler(RSocketStrategies strategies) {
        RSocketMessageHandler mh = new RSocketMessageHandler();
        mh
            .getArgumentResolverConfigurer()
            .addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        mh.setRSocketStrategies(strategies);
        return mh;
    }
   /* @Bean
    MapReactiveUserDetailsService authentication() {
       Collection<UserDetails> collection = new ArrayList<>(userService.allUsers());
       return new MapReactiveUserDetailsService(collection);
    }*/
    @Bean
    PayloadSocketAcceptorInterceptor authorization(RSocketSecurity security) {
        return security
                .authorizePayload(authorize ->
                        authorize
                                .setup().hasRole("SETUP")
                                .route("traffic.*").hasRole("SUPERADMIN")
//                                .anyExchange().authenticated()
                                .anyRequest().authenticated()
                )
                .simpleAuthentication(Customizer.withDefaults())
                .build();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(userService.bCryptPasswordEncoder());
    }
}
