package kup.get.config;

import javafx.animation.SequentialTransition;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class RSocketClientConfig {
    @Bean
    RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
        return strategies -> strategies
                .encoders(encoders -> {
                    encoders.add(new Jackson2CborEncoder());
                    encoders.add( new SimpleAuthenticationEncoder());
                })
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()));
    }
    @Bean
    AtomicReference<SequentialTransition> getTransition(){
        return new AtomicReference<>(new SequentialTransition());
    }

    @Bean
    File getLogo(){
        try {
            return ResourceUtils.getFile("classpath:images/logo.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
