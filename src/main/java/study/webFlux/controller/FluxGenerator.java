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
import java.util.Map;

@Component
public class FluxGenerator {
    @Value("${fluxDelayValue}")
    private int DELAY;

    private static final String UNORDERED_OUTPUT = "%s,%s,%s,%s\n";
    private static final String ORDERED_OUTPUT = "%d,%s,%s,%d,%s,%s,%d\n";
    private static final String ERR_MSG = "Exception: %s";
    private static final int FUNCTION_1 = 1;
    private static final int FUNCTION_2 = 2;
    private static final String ERR_TIME = "n/a";

    private final ScriptEngine engine;
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

    public FluxGenerator() {
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    //sorted part
    public Flux<String> generate(String fn1, String fn2, int count, boolean isOrdered) {
        HashMap<Long, String[]> f1results = new HashMap<>();
        HashMap<Long, String[]> f2results = new HashMap<>();

        Flux<Integer> fn1Flux = generateFunctionResultFlux(fn1, FUNCTION_1, count, f1results);
        Flux<Integer> fn2Flux = generateFunctionResultFlux(fn2, FUNCTION_2, count, f2results);

        if (isOrdered) {
            return Flux.merge(fn1Flux, fn2Flux)
                    .map(fnNum -> {
                        int f1counter = f1results.size();
                        int f2counter = f2results.size();

                        if (fnNum == 1 && f1counter > f2counter || fnNum != 1 && f1counter < f2counter) {
                            return "";
                        }

                        long min = Math.min(f1counter, f2counter);
                        String[] f1 = f1results.get(min - 1);
                        String[] f2 = f2results.get(min - 1);

                        int f1ahead = f1counter - f2counter;
                        int f2ahead = 0;
                        if (f1ahead < 0) {
                            f2ahead = -f1ahead;
                            f1ahead = 0;
                        }
                        return String.format(ORDERED_OUTPUT, min - 1, f1[0], f1[1], f1ahead, f2[0], f2[1], f2ahead);

                    })
                    .delayElements(Duration.ofMillis(DELAY));
        } else {
            return Flux.merge(fn1Flux, fn2Flux)
                    .map(fnNum -> {
                        Map<Long, String[]> map = fnNum == 1 ? f1results : f2results;
                        long lastKey = map.size() - 1;
                        String[] fnResult = map.get(lastKey);

                        return String.format(UNORDERED_OUTPUT, lastKey, fnNum, fnResult[0], fnResult[1]);
                    })
                    .delayElements(Duration.ofMillis(DELAY));
        }
    }

    private Flux<Integer> generateFunctionResultFlux(String function, int fnNum, int count, Map<Long,String[]> f1results) {
        return Flux.interval(Duration.ZERO)
                .map(counter -> getFunctionResult(function, counter, f1results, fnNum))
                        .limitRequest(count);
    }

    // [result,time]
    private int getFunctionResult(String function, long argument, Map<Long,String[]> map, int fnNum) {
        String functionName = function.split("[ (]")[1];
        String[] result=new String[2];

        try {
            long start = System.currentTimeMillis();
            engine.eval(function);

            Invocable invocable = (Invocable)engine ;

            result[0]= invocable.invokeFunction(functionName, argument).toString();
            result[1]=  (System.currentTimeMillis() - start)+"";

        } catch (ScriptException ex) {
            String exText=ex.getLocalizedMessage();
            String errMsg=String.format(ERR_MSG, exText);
            result[0]= errMsg;
            result[1]=ERR_TIME;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        map.put(argument, result);
        return fnNum;
    }
}