package study.webFlux.handlers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MainHandler {
    private static final String INDEX = "index";
    private static final String RESULTS = "results";

    public Mono<ServerResponse> home(ServerRequest request) {
        return ServerResponse
                .ok()
                .render(INDEX);
    }

    public Mono<ServerResponse> calculate(ServerRequest serverRequest) {
        Mono<MultiValueMap<String,String>> formData=serverRequest.formData();


        return ServerResponse
                .ok()
                .render(RESULTS, formData);
    }

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue(request.queryParam("name").orElse("someone")));
    }

}
