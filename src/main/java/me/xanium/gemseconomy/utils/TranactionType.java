package me.xanium.gemseconomy.utils;

public enum TranactionType {

    /**
     * Use DEPOSIT for adding currency to a player
     * Use WITHDRAW for removing currency from a player.
     * Use SET for setting a players currency balance.
     * Use CONVERSION for currency exchanges.
     */
    DEPOSIT,
    WITHDRAW,
    SET,
    CONVERSION
}
