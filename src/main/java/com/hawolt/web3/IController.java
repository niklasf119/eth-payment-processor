package com.hawolt.web3;

public interface IController<T> {
    void register(T t);

    void unregister(T t);
}
