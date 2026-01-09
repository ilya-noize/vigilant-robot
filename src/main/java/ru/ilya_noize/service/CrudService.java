package ru.ilya_noize.service;

import java.util.Optional;

public interface CrudService<T> {
    T save(T t);

    Optional<T> find(int id);

    void remove(int id);
}
