package study.webFlux.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.Duration;
import java.util.HashMap;

@Component
public class FluxGenerator {
    @Value("${fluxDelayValue}")
    private int DELAY;

    //iteration,function name,function result,time spent
    private static final String UNORDERED_OUTPUT = "%s,%s,%s,%s\n";
    private static final String ERR_MSG = "Exception: %s";
    private final ScriptEngine engine;
    private static final String FUNCTION_1 = "FUNCTION_1";
    private static final String FUNCTION_2 = "FUNCTION_2";
    private static final String ERR_TIME = "n/a";
    /**
     * Пример упорядоченной выдачи результатов:
     *      <№ итерации>,
     *      <результат функции 1>,
     *      <время расчета функции 1>,
     *      <кол-во полученных наперед результатов функции 1 (еще не выданных, в связи с медленным расчетом функции 2)>,
     *
     *      <результат функции 2>,
     *      <время расчета функции 2>,
     *      <кол-во полученных наперед результатов функции 2 (еще не выданных, в связи с медленным расчетом функции 1)>
     *
     * Пример неупорядоченной выдачи результатов:
     *     <№ итерации>,
     *     <номер функции>,
     *     <результат функции>,
     *     <время расчета функции>
     */
    ////////////////////////////////////////////////////////////////////
    private volatile int f1count=0;
    private volatile int f2count=0;

    // Key: iteration number,
    // String array: [
    // <результат функции 1>,
    // <время расчета функции 1>,
    // <кол-во полученных наперед результатов функции 1 (еще не выданных, в связи с медленным расчетом функции 2)>
    // ]
    HashMap<Integer,String[]> func1Results=new HashMap<>();
    HashMap<Integer,String[]> func2Results=new HashMap<>();

    public FluxGenerator() {
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    public Flux<String> generateOrdered(String fn1, String fn2, int count) {
        return null;
    }


    public Flux<String> generateUnordered(String fn1, String fn2, int count) {
        Flux<String> fn1Flux = generateFunctionResultFlux(fn1, count, FUNCTION_1)
                .map(fnResult->String.format(UNORDERED_OUTPUT,fnResult[0],fnResult[1],fnResult[2],fnResult[3]));

        Flux<String> fn2Flux = generateFunctionResultFlux(fn2, count, FUNCTION_2)
                .map(fnResult->String.format(UNORDERED_OUTPUT,fnResult[0],fnResult[1],fnResult[2],fnResult[3]));

        return Flux.merge(fn1Flux, fn2Flux)
                .delayElements(Duration.ofMillis(DELAY));
    }

    private Flux<String[]> generateFunctionResultFlux(String functionCode, int count, String functionId) {
        return Flux.interval(Duration.ZERO)
                .map(counter -> getFunctionResult(functionId, functionCode, counter))
                        .limitRequest(count);
    }

    private String[] getFunctionResult(String functionId, String function, long argument) {
        String functionName = function.split("[ (]")[1];
        String[] result=new String[]{argument+"",functionId,"0","0"};

        try {
            long start = System.currentTimeMillis();

            engine.eval(function);
            Invocable invocable = (Invocable) engine;

            result[2]= invocable.invokeFunction(functionName, argument).toString();
            result[3]=  (System.currentTimeMillis() - start)+"";

        } catch (ScriptException ex) {
            String exText=ex.getLocalizedMessage().replace(" in <eval> at line number 1 at column number 26","\n");
            String errMsg=String.format(ERR_MSG, exText);
            result[2]= errMsg;
            result[3]=ERR_TIME;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}
