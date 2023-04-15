package ru.practicum.shareit.util.exeption;

public class NotFoundExceptionEntity extends RuntimeException {
    public NotFoundExceptionEntity(String message) {
        super(message);
    }
}
