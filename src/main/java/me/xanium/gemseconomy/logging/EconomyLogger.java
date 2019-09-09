package me.xanium.gemseconomy.logging;

import me.xanium.gemseconomy.GemsEconomy;

public class EconomyLogger extends AbstractLogger {

    private final GemsEconomy plugin;

    public EconomyLogger(GemsEconomy plugin) {
        super(plugin);
        this.plugin = plugin;
    }


}
