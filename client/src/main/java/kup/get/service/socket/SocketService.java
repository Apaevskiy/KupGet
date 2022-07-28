package kup.get.service.socket;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SocketService {
    private WebClient client;

    public SocketService() {
    }

    public WebClient createWebClient(String username, String password, String url) {
        this.client = WebClient.builder()
                .baseUrl(url)
                .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
                .build();
        return client;
    }

    public WebClient getClient() {
        return client;
    }

    public void disconnect() {
        this.client = null;
    }

}
