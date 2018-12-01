package me.xanium.gemseconomy.economy;

import me.xanium.gemseconomy.utils.UtilServer;

import java.math.BigDecimal;

public class EcoUtil {

    public static double convert(double fromRate, double toRate, BigDecimal amount) {
        double rate = fromRate - toRate;
        UtilServer.consoleLog("Before calc: " + fromRate + " : " + toRate);
        UtilServer.consoleLog("Rate: " + rate);
        BigDecimal difference = amount.multiply(new BigDecimal(rate + ""));
        UtilServer.consoleLog("Difference: " + difference);

        UtilServer.consoleLog("Amount: " + amount.add(difference).doubleValue());
        return amount.add(difference).doubleValue();
    }

    public static double exchange(double from, double to, double amount){
        if(from < to){



        }else{



        }

        return -1;
    }
}
