package me.xanium.gemseconomy.economy;

import java.math.BigDecimal;

public class EcoUtil {

    public static double convert(double fromRate, double toRate, BigDecimal amount) {
        double rate = fromRate - toRate;
        BigDecimal difference = amount.multiply(new BigDecimal(rate + ""));

        return amount.add(difference).doubleValue();
    }
}
