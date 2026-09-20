package com.org.bgv.user.kyc.service;

public interface ResponseMapper<S, T> {

    T map(S source);

}
