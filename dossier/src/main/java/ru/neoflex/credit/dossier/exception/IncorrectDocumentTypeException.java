package ru.neoflex.credit.dossier.exception;

public class IncorrectDocumentTypeException extends RuntimeException{
    public IncorrectDocumentTypeException(String message) {
        super(message);
    }
}
