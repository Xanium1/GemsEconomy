package me.xanium.gemseconomy.event;

import me.xanium.gemseconomy.account.Account;
import me.xanium.gemseconomy.currency.Currency;
import me.xanium.gemseconomy.utils.TranactionType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GemsTransactionEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private Currency currency;
    private Account account;
    private double amount;
    private TranactionType type;
    private boolean cancel;

    public GemsTransactionEvent(Currency currency, Account account, double amount, TranactionType type) {
        super();
        this.currency = currency;
        this.account = account;
        this.amount = amount;
        this.type = type;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Account getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountFormatted(){
        return getCurrency().format(getAmount());
    }

    public TranactionType getType() {
        return type;
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
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}
