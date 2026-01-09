package ru.ilya_noize.operation.impl;

import org.springframework.stereotype.Component;
import ru.ilya_noize.Application;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;

@Component
public class ExitHandler implements OperationHandler {
    @Override
    public OperationType getType() {
        return OperationType.EXIT;
    }

    @Override
    public String perform() {
        Application.close();
        return "Application close";
    }
}
