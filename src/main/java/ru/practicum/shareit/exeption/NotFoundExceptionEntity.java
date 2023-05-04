package ru.practicum.shareit.exeption;

public class NotFoundExceptionEntity extends RuntimeException {
    public NotFoundExceptionEntity(String message) {
        super(message);
    }
}
