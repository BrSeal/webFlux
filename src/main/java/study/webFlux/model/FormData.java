package study.webFlux.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "user input form")
public class FormData {
    private static final String ORDERED = "ORDERED";
    private static final String UNORDERED = "UNORDERED";

    @ApiModelProperty(value = "First JS function", example = "function qwe(num){return num+100;}")
    private String function1;

    @ApiModelProperty(value = "Second JS function", example = "function qwe(num){return num+200;}")
    private String function2;

    @ApiModelProperty(value = "How many times functions will be invoked", example = "10")
    private int count;

    @ApiModelProperty(value = "is output ordered. If the value is \"ordered\" (not case sensitive) it will be ordered? else not)", example = "ordered")
    private OutputOrder outputOrder;

    public void setOutputOrder(String outputOrder) {
        boolean inputEqualsOrdered = outputOrder.toUpperCase().equals(ORDERED);

        this.outputOrder = inputEqualsOrdered ?
                OutputOrder.ORDERED : OutputOrder.UNORDERED;
    }
}
