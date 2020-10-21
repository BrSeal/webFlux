package study.webFlux.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import springfox.documentation.spring.web.plugins.Docket;
import study.webFlux.model.FormData;

@Controller
@Api(tags = {"Main controller"})
@SwaggerDefinition(tags = {
        @Tag(name = "Main controller", description = "reactive REST-controller")
})
public class MainController {

    private final MainService service;

    @Autowired
    public MainController(MainService service) {
        this.service = service;
    }

    @GetMapping("/")
    @ApiOperation(value = "shows main form. Where you can input some params")
    public String showMainPage() {
        return "index";
    }

    @PostMapping(
            value = "/calculate",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "returns Flux<String> with results of calculations", response = Flux.class)
    public Flux<String> showResults(FormData data) {
        return service.generateFlux( data);
    }
}
