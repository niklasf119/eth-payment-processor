package com.hawolt.address;

import com.hawolt.block.impl.ConfirmationListener;
import com.hawolt.block.impl.IConfirmationCallback;
import com.hawolt.logging.Logger;
import com.hawolt.web3.Web3Controller;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.hawolt.web3.Web3Controller.PENDING_TRANSACTION_CONTROLLER;
import static com.hawolt.web3.Web3Controller.TRANSACTION_CONTROLLER;

public class Wallet implements IAddressListener, IConfirmationCallback {

    private final String address;

    private BigInteger balance;
    private IBalanceCallback callback;

    private Map<Transaction, Integer> transactions = new HashMap<>();

    public static void listen(IBalanceCallback callback, String address) throws IOException {
        new Wallet(callback, address);
    }

    private Wallet(IBalanceCallback callback, String address) throws IOException {
        this.address = address;
        this.callback = callback;
        this.balance = Web3Controller.getBalance(address).getBalance();
        Logger.debug("{} initial {}", address, balance.toString());
        PENDING_TRANSACTION_CONTROLLER.register(this);
        TRANSACTION_CONTROLLER.register(this);
        callback.onBalanceUpdate(balance);
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void onTransactionBroadcast(Transaction transaction) {
        Logger.debug("{} broadcast", transaction.getHash());
    }

    @Override
    public void onTransaction(Transaction transaction) {
        ConfirmationListener.register(this, transaction);
        Logger.debug("{} detected", transaction.getHash());
        transactions.put(transaction, 0);
    }

    @Override
    public void onConfirmation(Transaction transaction) {
        Web3Controller.getTransaction(transaction.getHash(), o -> {
            Transaction t = o.getResult();
            if (t.equals(transaction)) {
                Logger.debug("{} received {}", address, transaction.getValue().toString());
                this.balance = balance.add(transaction.getValue());
                Logger.debug("{} balance {}", address, balance.toString());
                transactions.remove(transaction);
                callback.onBalanceUpdate(balance);
            } else {
                Logger.info("{} does not match {}", transaction.getHash(), t.getHash());
            }
        }, throwable -> {
            Logger.warn("Failed to fetch tx {}", transaction.getHash());
            Logger.error(throwable);
        });
    }

    @Override
    public void addConfirmation(Transaction transaction) {
        int confirmations = transactions.getOrDefault(transaction, 0) + 1;
        Logger.debug("{} confirmations {}", transaction.getHash(), confirmations);
        transactions.put(transaction, confirmations);
    }
}
