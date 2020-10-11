package study.webFlux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import study.webFlux.handlers.MainHandler;

@Configuration
public class GreetingRouter {

    private static final RequestPredicate acceptMediaTypePlainText = RequestPredicates.accept(MediaType.TEXT_PLAIN);


    @Bean
    public RouterFunction<ServerResponse> route(MainHandler mainHandler) {
        RequestPredicate helloRoute = RequestPredicates
                .GET("/hello");

        RequestPredicate homeRoute = RequestPredicates
                .GET("/")
                .and(acceptMediaTypePlainText);

        RequestPredicate calculateRoute = RequestPredicates
                .POST("/calculate");

        return RouterFunctions
                .route(homeRoute, mainHandler::home)
                .andRoute(calculateRoute, mainHandler::calculate)
                .andRoute(helloRoute, mainHandler::hello);
    }

}
