package study.webFlux.controller;

import reactor.core.publisher.Flux;
import study.webFlux.model.FormData;

public interface MainService {
    Flux<String> generateFlux(FormData data);
}
