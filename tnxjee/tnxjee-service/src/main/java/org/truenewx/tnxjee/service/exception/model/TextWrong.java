package org.truenewx.tnxjee.service.exception.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本错误
 *
 * @author jianglei
 */
public class TextWrong {

    private String value;
    private List<CodedError> errors = new ArrayList<>();

    public TextWrong(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public List<CodedError> getErrors() {
        return this.errors;
    }

}
