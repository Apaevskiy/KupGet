package kup.get.service.socket;

import kup.get.entity.alfa.ClinicEvent;
import kup.get.entity.traffic.TrafficItemType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class ClinicService {
    private final SocketService socketService;

    public Flux<ClinicEvent> getItemsType(LocalDate date) {
        return socketService.getClient().get()
                .uri("/clinic/event/{date}", date)
                .retrieve()
                .bodyToFlux(ClinicEvent.class);
    }
}
