package id.or.gri.sdk.client;

import org.springframework.web.reactive.function.client.WebClient;

public abstract class AbstractService {

    protected final String tokenType = "Bearer ";
    protected final WebClient webClient;

    public AbstractService(WebClient webClient) {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

}
