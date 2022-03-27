package kup.get.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Controller
public class LoginController {
    @GetMapping("/")
    public String authorizationPage() {
        return "redirect: /login";
    }

    @ConnectMapping("shell-client")
    void connectShellClient(RSocketRequester requester, @Payload String client) {
        System.out.println(client + " connected");
        requester.route("client-status")
                .data("OPEN")
                .retrieveFlux(String.class)
                .subscribe();
    }

    @MessageMapping("getAuthorities")
    Flux<String> getAuthorities(@AuthenticationPrincipal Mono<UserDetails> user) {
        return user
                .flatMapMany(u ->
                        Flux.fromIterable(u.getAuthorities()
                                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
    }
}
