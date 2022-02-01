package kup.get.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

@Configuration
public class RSocketClientConfig {
    @Bean
    RSocketRequester requester(RSocketRequester.Builder builder){
        return builder.tcp("localhost", 9091);
    }
}
