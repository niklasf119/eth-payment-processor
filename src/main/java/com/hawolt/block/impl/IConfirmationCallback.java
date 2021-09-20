package com.hawolt.block.impl;

import org.web3j.protocol.core.methods.response.Transaction;

public interface IConfirmationCallback {
    void onConfirmation(Transaction transaction);

    void addConfirmation(Transaction transaction);
}
