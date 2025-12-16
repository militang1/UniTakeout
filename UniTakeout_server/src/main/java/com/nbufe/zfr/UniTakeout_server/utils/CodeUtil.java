package com.nbufe.zfr.UniTakeout_server.utils;

import java.util.Random;

public class CodeUtil {
    private static final Random random = new Random();

    public static String generateCode() {
        return String.format("%06d", random.nextInt(1000000));
    }
}

