package study.webFlux.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import study.webFlux.model.FormData;
import study.webFlux.model.OutputOrder;

import javax.script.*;
import java.time.Duration;


@Service
public class MainServiceImpl implements MainService {
    @Value("${fluxDelayValue}")
    private int DELAY;

    private static final String FUNCTION_1 = "Function 1";
    private static final String FUNCTION_2 = "Function 2";

    private final ScriptEngine engine;

    public MainServiceImpl() {
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public Flux<String> generateFlux(FormData data) {

        boolean isOrdered = data.getOutputOrder() == OutputOrder.ORDERED;

        int count = data.getCount();
        String fn1 = data.getFunction1();
        String fn2 = data.getFunction2();

        return isOrdered ? generateOrdered(fn1, fn2, count) : generateUnordered(fn1, fn2, count);
    }

    private Flux<String> generateOrdered(String fn1, String fn2, int count) {
//        Flux<String> fn1Flux = Flux.create()
//                .limitRequest(count);
//
//        Flux<String> fn2Flux = Flux.create()
//                .limitRequest(count);
//
//        return Flux.mergeDelayError(1, fn1Flux, fn2Flux)
//                .delayElements(Duration.ofMillis(DELAY));
        return Flux.just("[ORDERED]" + fn1, "[ORDERED]" + fn2 + "\n").repeat(count).delayElements(Duration.ofMillis(DELAY));
    }

    private Flux<String> generateUnordered(String fn1, String fn2, int count) {
        Flux<String> fn1Flux = generateFunctionResultFlux(fn1, count, FUNCTION_1);
        Flux<String> fn2Flux = generateFunctionResultFlux(fn2, count, FUNCTION_2);

        return Flux.merge(fn1Flux, fn2Flux)
                .delayElements(Duration.ofMillis(DELAY));
    }

    private Flux<String> generateFunctionResultFlux(String function, int count, String fnNumber) {
        return Flux.interval(Duration.ZERO)
                .map(counter -> counter + ",\n"
                                + fnNumber + ",\n"
                                + getFunctionResult(function, counter))
                .limitRequest(count);
    }

    private String getFunctionResult(String function, long argument) {
        String functionName = function.split("[ (]")[1];
        try {
            long start = System.currentTimeMillis();
            engine.eval(function);
            Invocable invocable = (Invocable) engine;

            return invocable.invokeFunction(functionName, argument).toString() + ",\n"
                    + (System.currentTimeMillis() - start)+ "\n";
        } catch (ScriptException ex) {
            ex.printStackTrace();
            return "Exception while processing function "
                    + functionName + "(). \nException text: "
                    + ex.getLocalizedMessage().replace(" in <eval> at line number 1 at column number 26","\n");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
