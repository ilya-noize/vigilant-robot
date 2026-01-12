package ru.shummi.service;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface CrudService<T> {
    T save(T entity);

    Optional<T> find(int id);

    default T get(int id) {
        return find(id).orElseThrow(() -> new NoSuchElementException(
                "No such %s ID:%s%n".formatted(
                        getEntitySimpleClassName().toLowerCase(), id)
        ));
    }

    void remove(int id);

    String getEntitySimpleClassName();
}
