package com.hawolt.address;

import java.math.BigInteger;

public interface IBalanceCallback {
    void onBalanceUpdate(BigInteger b);
}
