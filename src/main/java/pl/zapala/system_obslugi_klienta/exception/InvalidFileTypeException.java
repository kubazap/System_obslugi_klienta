package pl.zapala.system_obslugi_klienta.exception;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String msg) { super(msg); }
}