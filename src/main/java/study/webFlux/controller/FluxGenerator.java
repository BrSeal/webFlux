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
    private static final String FUNCTION_1 = "FUNCTION_1";
    private static final String FUNCTION_2 = "FUNCTION_2";
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
    public Flux<String> generateOrdered(String fn1, String fn2, int count) {
        HashMap<Integer,String[]> f1results=new HashMap<>();
        HashMap<Integer,String[]> f2results=new HashMap<>();

        Flux<Integer> fn1Flux = mapToIntegerFlux(generateFunctionResultFlux(fn1, count, FUNCTION_1), f1results);

        Flux<Integer> fn2Flux = mapToIntegerFlux(generateFunctionResultFlux(fn2, count, FUNCTION_2), f2results);

        return Flux.merge(fn1Flux, fn2Flux)
                 .map(iterationNum->{
                     if(f1results.containsKey(iterationNum)&&f2results.containsKey(iterationNum)){
                         String[] f1=f1results.get(iterationNum);
                         String[] f2=f2results.get(iterationNum);

                         int f1ahead=f1results.keySet().size()-f2results.keySet().size();
                         int f2ahead=0;
                         if(f1ahead<0){
                             f2ahead=-f1ahead;
                             f1ahead=0;
                         }
                         return String.format(ORDERED_OUTPUT,iterationNum,f1[0],f1[1],f1ahead,f2[0],f2[1],f2ahead);
                     }
                     return "";
                 })
                 .delayElements(Duration.ofMillis(DELAY));
    }

    private Flux<Integer> mapToIntegerFlux(Flux<String[]> stringArrayFlux, Map<Integer,String[]> resultsMap){
        return stringArrayFlux.map(fnResult->{
            String[] res = new String[2];
            int iteration=Integer.parseInt(fnResult[0]);
            res[0]=fnResult[2];
            res[1]=fnResult[3];
            resultsMap.put(iteration,res);

            return iteration;
        });

    }

    //unsorted part
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

    // [iteration,function,result,time]
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
