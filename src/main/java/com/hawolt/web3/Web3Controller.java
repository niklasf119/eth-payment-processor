package com.hawolt.web3;

import com.hawolt.JsonSource;
import com.hawolt.address.IAddressListener;
import com.hawolt.address.Wallet;
import com.hawolt.block.IBlockUpdate;
import com.hawolt.exception.InvalidKeyException;
import com.hawolt.logging.Logger;
import com.hawolt.web3.impl.BlockController;
import com.hawolt.web3.impl.PendingTransactionController;
import com.hawolt.web3.impl.TransactionController;
import io.reactivex.Flowable;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Consumer;

public class Web3Controller {

    private static Web3j web3j;

    public static AbstractController<IBlockUpdate, EthBlock, Flowable<EthBlock>> BLOCK_CONTROLLER;
    public static AbstractController<IAddressListener, Transaction, Flowable<Transaction>> TRANSACTION_CONTROLLER, PENDING_TRANSACTION_CONTROLLER;

    static {
        try {
            JsonSource source = JsonSource.of("geth.json");
            String address = source.getOrThrow("rpc.addr", new InvalidKeyException("rpc.addr"));
            int port = Integer.parseInt(source.getOrThrow("rpc.port", new InvalidKeyException("rpc.port")));
            String target = String.format("http://%s:%s", address, port);
            HttpService service = new HttpService(target);
            Web3Controller.web3j = Web3j.build(service);
            Web3Controller.BLOCK_CONTROLLER = new BlockController(web3j.blockFlowable(false));
            Web3Controller.TRANSACTION_CONTROLLER = new TransactionController(web3j.transactionFlowable());
            Web3Controller.PENDING_TRANSACTION_CONTROLLER = new PendingTransactionController(web3j.transactionFlowable());
        } catch (IOException e) {
            Logger.fatal("Unable to find geth.json {}", e.getMessage());
            Logger.error(e);
        } catch (InvalidKeyException e) {
            Logger.fatal("Unable to find required key in geth.json");
            Logger.error(e);
        } catch (NumberFormatException e) {
            Logger.fatal("Unable to convert port to an Integer");
            Logger.error(e);
        }
    }

    public static void getBalance(String address, Consumer<EthGetBalance> s, Consumer<Throwable> t) {
        web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).flowable().subscribe(s::accept, t::accept).dispose();
    }

    public static EthGetBalance getBalance(String address) throws IOException {
        return web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
    }

    public static void getTransaction(String hash, Consumer<EthTransaction> s, Consumer<Throwable> t) {
        web3j.ethGetTransactionByHash(hash).flowable().subscribe(s::accept, t::accept).dispose();
    }

    public static EthTransaction getTransaction(String hash) throws IOException {
        return web3j.ethGetTransactionByHash(hash).send();
    }
}
