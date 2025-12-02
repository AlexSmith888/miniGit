package domain.validators;

public interface Validation {
    public void isValid(String [] item) throws IllegalArgumentException;
}