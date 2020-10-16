package study.webFlux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import study.webFlux.model.FormData;

import java.time.Duration;

@Controller
public class MainController {

    private final MainService service;

    @Autowired
    public MainController(MainService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String showMainPage() {
        return "index";
    }

    @PostMapping(
            value = "/calculate",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    @ResponseBody
    public Flux<String> showResults(FormData data) {
        return service.generateFlux( data);
    }
}
