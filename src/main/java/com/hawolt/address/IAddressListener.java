package com.hawolt.address;

import org.web3j.protocol.core.methods.response.Transaction;

public interface IAddressListener {
    void onTransaction(Transaction transaction);

    void onTransactionBroadcast(Transaction transaction);

    String getAddress();
}
