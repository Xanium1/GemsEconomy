package me.xanium.gemseconomy.event;

import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.Currency;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GemsConversionEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private boolean cancel;
    private Currency exchanged;
    private Currency received;
    private Account account;
    private double amountExchanged;
    private double amountReceived;

    public GemsConversionEvent(Currency exchanged, Currency received, Account account, double amountExchanged, double amountReceived) {
        this.exchanged = exchanged;
        this.received = received;
        this.account = account;
        this.amountExchanged = amountExchanged;
        this.amountReceived = amountReceived;
    }

    public Currency getExchanged() {
        return exchanged;
    }

    public Currency getReceived() {
        return received;
    }

    public Account getAccount() {
        return account;
    }

    public double getAmountExchanged() {
        return amountExchanged;
    }

    public double getAmountReceived() {
        return amountReceived;
    }

    public String getAmountReceivedFormatted(){
        return getReceived().format(getAmountReceived());
    }

    public String getAmountExchangedFormatted(){
        return getExchanged().format(getAmountExchanged());
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }
}
