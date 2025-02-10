package com.cpplab.security.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CipherConstant {

    TRANSFORMATION("AES/CBC/PKCS5Padding"),
    PRIVATE_KEY_AES256("202401_COWEE_AES_256_PRIVATE_KEY"), // 정확히 32바이트여야함
    AES("AES");

    private final String value;

}
