package me.xanium.gemseconomy.logging;

import me.xanium.gemseconomy.GemsEconomy;

public class EcoLogger extends AbstractLogger {

    private final GemsEconomy plugin;

    public EcoLogger(GemsEconomy plugin) {
        super(plugin);
        this.plugin = plugin;
    }


}
