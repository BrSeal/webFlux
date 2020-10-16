package study.webFlux.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormData {
    private static final String ORDERED = "ORDERED";
    private static final String UNORDERED = "UNORDERED";

    private String function1;
    private String function2;
    private int count;
    private OutputOrder outputOrder;

    public void setOutputOrder(String outputOrder) {
        boolean inputEqualsOrdered = outputOrder.toUpperCase().equals(ORDERED);

        this.outputOrder = inputEqualsOrdered ?
                OutputOrder.ORDERED : OutputOrder.UNORDERED;
    }

    @Override
    public String toString() {
        return "function1: " + function1 + "\n" +
                "function2: " + function2 + "\n" +
                "count: " + count + "\n" +
                "ordered: " + outputOrder;
    }
}
