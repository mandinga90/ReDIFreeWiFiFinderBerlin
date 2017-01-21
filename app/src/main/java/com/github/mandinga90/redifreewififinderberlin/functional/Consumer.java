package com.github.mandinga90.redifreewififinderberlin.functional;

public interface Consumer<T> {
    void apply(T t);
    Object get();
}
