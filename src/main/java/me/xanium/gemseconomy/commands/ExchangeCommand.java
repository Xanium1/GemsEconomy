package me.xanium.gemseconomy.commands;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.economy.Account;
import me.xanium.gemseconomy.economy.AccountManager;
import me.xanium.gemseconomy.economy.Currency;
import me.xanium.gemseconomy.economy.EcoUtil;
import me.xanium.gemseconomy.file.F;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;

public class ExchangeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String v21315, String[] args) {
        GemsEconomy.doAsync(() -> {
            if (!sender.hasPermission("gemseconomy.command.exchange")) {
                sender.sendMessage(F.getNoPerms());
                return;
            }

            if (args.length == 0) {
                F.getExchangeHelp(sender);
            } else if (args.length == 3) {
                Currency toExchange = AccountManager.getCurrency(args[0]);
                Currency toReceive = AccountManager.getCurrency(args[1]);
                double amount;

                if (toExchange != null && toReceive != null) {
                    if (toReceive.isDecimalSupported()) {
                        try {
                            amount = Double.parseDouble(args[2]);
                            if (amount <= 0.0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    } else {
                        try {
                            amount = Integer.parseInt(args[2]);
                            if (amount <= 0.0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                            return;
                        }
                    }


                    Account account = AccountManager.getAccount(sender.getName());
                    if (account != null) {
                        double convert = EcoUtil.convert(toExchange.getExchangeRate(), toReceive.getExchangeRate(), BigDecimal.valueOf(amount));

                        if (account.withdraw(toExchange, convert)) {
                            account.deposit(toReceive, amount);
                            sender.sendMessage(F.getExchangeSuccess().replace("{currencycolor}", "" + toExchange.getColor()).replace("{exchangedCurr}", toExchange.getPlural())
                                    .replace("{currEx}", String.valueOf(convert)).replace("{currencycolor2}", "" + toReceive.getColor()).replace("{receivedCurr}", toReceive.getPlural())
                                    .replace("{amount}", String.valueOf(amount)));
                        }
                    }


                } else {
                    sender.sendMessage(F.getUnknownCurrency());
                }

            } else if (args.length == 4) {

                Currency toExchange = AccountManager.getCurrency(args[0]);
                Currency toReceive = AccountManager.getCurrency(args[2]);
                double toExchangeAmount;
                double toReceiveAmount;

                if (toExchange != null && toReceive != null) {
                    if (toReceive.isDecimalSupported()) {
                        try {
                            toExchangeAmount = Double.parseDouble(args[2]);
                            if (toExchangeAmount <= 0.0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                        }
                    } else {
                        try {
                            toExchangeAmount = Integer.parseInt(args[2]);
                            if (toExchangeAmount <= 0.0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(F.getUnvalidAmount());
                        }
                    }

                } else {
                    sender.sendMessage(F.getUnknownCurrency());
                }
            }
        });
        return true;
    }
}
