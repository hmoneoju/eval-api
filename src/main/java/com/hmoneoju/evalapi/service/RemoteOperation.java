package com.hmoneoju.evalapi.service;

import com.hmoneoju.evalapi.exception.RemoteOperationException;

public interface RemoteOperation<T,U> {
    U execute(T t) throws RemoteOperationException;
}
