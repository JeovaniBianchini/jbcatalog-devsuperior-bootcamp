package com.bianchini.jbcatalog.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError{   //Classe está herdando de standardError, adicioando atributo errors que é uma lista de FieldMessage

    private List<FieldMessage> errors = new ArrayList<>();

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String message){  //método que adiciona uma FieldMessage na lista de erros
        errors.add(new FieldMessage(fieldName, message));
    }
}
