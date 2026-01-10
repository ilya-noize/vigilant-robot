package ru.ilya_noize.service;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface CrudService<T> {
    T create(Object param);

    Optional<T> find(int id);

    default T get(int id) {
        return find(id).orElseThrow(() -> new NoSuchElementException(
                "No such %s ID:%s%n".formatted(
                        getEntitySimpleClassName().toLowerCase(), id)
        ));
    }

    boolean remove(int id);

    String getEntitySimpleClassName();
}
