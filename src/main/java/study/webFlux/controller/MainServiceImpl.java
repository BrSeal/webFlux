package study.webFlux.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import study.webFlux.model.FormData;
import study.webFlux.model.OutputOrder;

import java.time.Duration;

@Service
public class MainServiceImpl implements MainService {
    @Value("${fluxDelayValue}")
    private int delay;

    @Override
    public Flux<String> generateFlux(FormData data) {
        if (data.getOutputOrder() == OutputOrder.ORDERED) {
            return generateOrdered(data);
        }
            return generateUnordered(data);
    }

    private Flux<String> generateOrdered(FormData data) {
        return Flux.just(
                "[ORDERED] function1: "+data.getFunction1()+"\n",
                "[ORDERED] function2: "+data.getFunction2()+"\n",
                "[ORDERED] count: "+data.getCount()+"\n",
                "[ORDERED] order: "+data.getOutputOrder()+"\n"
        )
                .delayElements(Duration.ofMillis(delay));
    }

    private Flux<String> generateUnordered(FormData data) {
        return Flux.just(
                "[UNORDERED] function1: "+data.getFunction1()+"\n",
                "[UNORDERED] function2: "+data.getFunction2()+"\n",
                "[UNORDERED] count: "+data.getCount()+"\n",
                "[UNORDERED] order: "+data.getOutputOrder()+"\n"
        )
                .delayElements(Duration.ofMillis(delay));
    }
}
