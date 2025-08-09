package com.iprody.payment.service.app.exception;

import java.time.Instant;

public record ErrorDto(int errorCode, String message, Instant timestamp) {

}
