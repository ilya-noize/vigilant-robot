package ru.shummi.operation;

public interface OperationHandler {

    OperationType getType();

    String perform();
}
