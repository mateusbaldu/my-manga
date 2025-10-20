package fatecipi.progweb.mymanga.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }
        return s.matches("^[a-zA-Z0-9_.-]{5,20}$");

        //TODO: melhorar verificação, garantir que a cada parametro errado (tamanho, caractere invalido) ele retorne algo diferente
    }
}
