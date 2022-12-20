package com.bianchini.jbcatalog.services.validation;

import com.bianchini.jbcatalog.dto.UserUpdateDto;
import com.bianchini.jbcatalog.entities.User;
import com.bianchini.jbcatalog.repositories.UserRepository;
import com.bianchini.jbcatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDto> {  //Classe que faz uma validação para não inserir um usuário com email ja existente no banco de dados

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserUpdateValid ann) {
    }

    @Override
    public boolean isValid(UserUpdateDto dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        var uri = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        long userId = Long.parseLong(uri.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        User user = userRepository.findByEmail(dto.getEmail());
        if (user != null && userId != user.getId()) {
            list.add(new FieldMessage("Email", "Email já existe"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}