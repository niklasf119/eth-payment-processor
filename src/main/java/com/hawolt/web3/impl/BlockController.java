package com.hawolt.web3.impl;

import com.hawolt.block.IBlockUpdate;
import com.hawolt.web3.AbstractController;
import io.reactivex.Flowable;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;

public class BlockController extends AbstractController<IBlockUpdate, EthBlock, Flowable<EthBlock>> {

    public BlockController(Flowable<EthBlock> flowable) {
        super(flowable);
    }

    @Override
    public void onEvent(EthBlock block) {
        BigInteger b = block.getBlock().getNumber();
        for (int i = list.size() - 1; i >= 0; i--) {
            IBlockUpdate callback = list.get(i);
            callback.onBlock(b);
        }
    }
}
