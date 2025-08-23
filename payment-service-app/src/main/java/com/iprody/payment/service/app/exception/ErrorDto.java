package com.iprody.payment.service.app.exception;

import java.time.Instant;
import java.util.UUID;

public record ErrorDto(int errorCode, String message, Instant timestamp, String operation, UUID entityId) {

}
