package com.seetharam.urlshortener.config;

import java.util.ArrayList;

public class Base62Encoder {

    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int BASE = 62;

    public static String encode(Long id){
        if(id == null || id < 0){
            throw new IllegalArgumentException("The ID should be provided or greater than zero");
        }
        if(id == 0){
            return String.valueOf(CHARACTERS.charAt(0));
        }
        Long number = id;
        StringBuilder result = new StringBuilder();
        while(number > 0){
            result.append(CHARACTERS.charAt((int) (number%BASE)));
            number/=BASE;
        }
        result.reverse();
        return result.toString();
    }
}
