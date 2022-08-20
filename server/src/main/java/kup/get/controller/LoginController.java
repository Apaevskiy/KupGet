package kup.get.controller;

import kup.get.service.security.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
public class LoginController {
    private final UserService userService;


    @GetMapping("/auth/getAuthorities")
    Flux<String> getAuthorities(@AuthenticationPrincipal Mono<UserDetails> user) {
        return user
                .flatMapMany(u ->
                        Flux.just(userService.getUserByUsername(u.getUsername()).getFIO())
                                .concatWith(
                                        Flux.fromIterable(u.getAuthorities()
                                                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))));
    }
}
