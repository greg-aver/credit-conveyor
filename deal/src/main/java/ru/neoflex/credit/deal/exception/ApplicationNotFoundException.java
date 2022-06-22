package ru.neoflex.credit.deal.exception;

public class ApplicationNotFoundException extends RuntimeException{
    public ApplicationNotFoundException(String message) {
        super(message);
    }
}
