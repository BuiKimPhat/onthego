package com.leobkdn.onthego.data.result;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return success.getData().toString();
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            if (error.getErrMessage() != null)  return error.getErrMessage();
            return error.getError().toString();
        }
        return "";
    }
    // Success sub-class
    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }
        public T getData() {
            return this.data;
        }
        public boolean checkTypeString(){
            if (data instanceof String) return true;
            return false;
        }
    }

    // Error sub-class
    public final static class Error extends Result {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }
        public Exception getError() {
            return this.error;
        }
        public String getErrMessage(){ return this.error.getMessage(); }
    }
}