package com.hawolt.web3;

import com.hawolt.logging.Logger;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractController<S, T, E extends Flowable<T>> implements IController<S> {
    protected List<S> list = new ArrayList<>();

    private final Disposable disposable;

    public AbstractController(E flowable) {
        this.disposable = flowable.subscribe(this::onEvent, this::handle);
    }

    private void handle(Throwable throwable) {
        Logger.warn("Controller threw an exception");
        Logger.error(throwable);
    }

    public abstract void onEvent(T t);

    public Disposable getDisposable() {
        return disposable;
    }

    @Override
    public void register(S s) {
        this.list.add(s);
    }

    @Override
    public void unregister(S s) {
        this.list.remove(s);
    }
}
