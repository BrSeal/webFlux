package study.webFlux.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormData {
   private String function1;
   private String function2;
   private int count;
   private String outputOrder;

    @Override
    public String toString() {
        return "function1: "+function1+"\n"+
                "function2: "+function2+"\n"+
                "count: "+count+"\n"+
                "ordered: "+ outputOrder;
    }
}
