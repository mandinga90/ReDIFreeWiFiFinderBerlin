package com.github.aprofromindia.playmapview.functional;

public interface Consumer<T> {
    void apply(T t);
    Object get();
}
