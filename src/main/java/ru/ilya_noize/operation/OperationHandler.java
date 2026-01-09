package ru.ilya_noize.operation;

public interface OperationHandler {

    OperationType getType();

    String perform();
}
