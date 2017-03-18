package com.hmoneoju.evalapi.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Operation {
    private String expression;
    private String result;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
