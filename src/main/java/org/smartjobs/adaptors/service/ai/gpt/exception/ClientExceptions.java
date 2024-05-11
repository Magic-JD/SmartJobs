package org.smartjobs.adaptors.service.ai.gpt.exception;

public class ClientExceptions {

    private ClientExceptions() {
        //Private constructor to prevent instantiation
    }

    public static final class ServiceCallException extends RuntimeException {

        public ServiceCallException(String msg) {
            super(msg);
        }

    }
}
