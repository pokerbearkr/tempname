package org.example.siljeun.domain.reservation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

  private Set<String> values;

  @Override
  public void initialize(ValidEnum annotation) {
    Class<? extends Enum<?>> enumClass = annotation.enumClass();

    values = Arrays.stream(enumClass.getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null || values.contains(value.toUpperCase());
  }
}
