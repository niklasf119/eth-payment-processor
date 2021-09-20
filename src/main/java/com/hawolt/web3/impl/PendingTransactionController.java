package com.hawolt.web3.impl;

import com.hawolt.address.IAddressListener;
import com.hawolt.web3.AbstractController;
import io.reactivex.Flowable;
import org.web3j.protocol.core.methods.response.Transaction;

public class PendingTransactionController extends AbstractController<IAddressListener, Transaction, Flowable<Transaction>> {

    public PendingTransactionController(Flowable<Transaction> flowable) {
        super(flowable);
    }

    @Override
    public void onEvent(Transaction transaction) {
        String to = transaction.getTo();
        for (int i = list.size() - 1; i >= 0; i--) {
            IAddressListener callback = list.get(i);
            if (callback.getAddress().equals(to)) {
                callback.onTransactionBroadcast(transaction);
            }
        }
    }
}
