package com.hawolt.block.impl;

import com.hawolt.block.IBlockUpdate;
import com.hawolt.logging.Logger;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;

import static com.hawolt.web3.Web3Controller.BLOCK_CONTROLLER;

public class ConfirmationListener implements IBlockUpdate {

    private final IConfirmationCallback callback;
    private final Transaction transaction;

    public static ConfirmationListener register(IConfirmationCallback callback, Transaction transaction) {
        return new ConfirmationListener(callback, transaction);
    }

    private ConfirmationListener(IConfirmationCallback callback, Transaction transaction) {
        this.transaction = transaction;
        this.callback = callback;
        BLOCK_CONTROLLER.register(this);
        Logger.debug("{} registered ConfirmationListener", transaction.getHash());
    }

    @Override
    public void onBlock(BigInteger blockId) {
        long confirmations = blockId.subtract(transaction.getBlockNumber()).longValue();
        if (confirmations > 6) {
            BLOCK_CONTROLLER.unregister(this);
            callback.onConfirmation(transaction);
        } else {
            callback.addConfirmation(transaction);
        }
    }
}
