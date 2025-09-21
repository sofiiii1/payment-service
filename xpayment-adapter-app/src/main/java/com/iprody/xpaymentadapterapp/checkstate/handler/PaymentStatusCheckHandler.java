package com.iprody.xpaymentadapterapp.checkstate.handler;

import java.util.UUID;

public interface PaymentStatusCheckHandler {

    boolean handle(UUID chargeGuid);
}
