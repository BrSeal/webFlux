package study.webFlux.handlers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class MainHandler {

    public Mono<ServerResponse> home(ServerRequest request) {
        return ServerResponse
                .ok()
                .render("index");
    }

    public Mono<ServerResponse> calculate(ServerRequest serverRequest) {
        String function1 = serverRequest.queryParam("function1").orElse("null");
        String function2 = serverRequest.queryParam("function2").orElse("null");
        String count = serverRequest.queryParam("count").orElse("null");
        String ordered = serverRequest.queryParam("ordered").orElse("null");

        Map<String, String> params = Map.of(
                "function1",    function1,
                "function2",    function2,
                "count",        count,
                "ordered",      ordered
        );

        return ServerResponse
                .ok()
                .render("results", params);
    }

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue(request.queryParam("name").orElse("someone")));
    }
}
