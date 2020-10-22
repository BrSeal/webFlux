package study.webFlux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import study.webFlux.model.FormData;
import study.webFlux.model.OutputOrder;

import java.util.List;


@Service
public class MainServiceImpl implements MainService {
    private final FluxGenerator generator;

    @Autowired
    public MainServiceImpl(FluxGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Flux<String> generateFlux(FormData data) {

        boolean isOrdered = data.getOutputOrder() == OutputOrder.ORDERED;

        int count = data.getCount();
        String fn1 = data.getFunction1();
        String fn2 = data.getFunction2();

        return generator.generate(fn1, fn2, count,isOrdered);
    }
}
