/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.xanium.gemseconomy.commands;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.account.Account;
import me.xanium.gemseconomy.currency.Currency;
import me.xanium.gemseconomy.data.DataStorage;
import me.xanium.gemseconomy.file.F;
import me.xanium.gemseconomy.utils.SchedulerUtils;
import me.xanium.gemseconomy.utils.UtilServer;
import me.xanium.gemseconomy.utils.UtilString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

public class CurrencyCommand implements CommandExecutor {

    private final GemsEconomy plugin = GemsEconomy.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s124, String[] args) {
        SchedulerUtils.runAsync(() -> {
            if (!sender.hasPermission("gemseconomy.command.currency")) {
                sender.sendMessage(F.getNoPerms());
                return;
            }
            if (args.length == 0) {
                F.sendCurrencyUsage(sender);
            } else {
                String cmd = args[0];
                if (cmd.equalsIgnoreCase("create")) {
                    if (args.length == 3) {
                        String single = args[1];
                        String plural = args[2];
                        if (plugin.getCurrencyManager().currencyExist(single) || plugin.getCurrencyManager().currencyExist(plural)) {
                            sender.sendMessage(F.getPrefix() + "§cCurrency already exists.");
                            return;
                        }

                        plugin.getCurrencyManager().createNewCurrency(single, plural);
                        sender.sendMessage(F.getPrefix() + "§7Created currency: §a" + single);
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Create());
                    }
                } else if (cmd.equalsIgnoreCase("list")) {
                    sender.sendMessage(F.getPrefix() + "§7There are §f" + plugin.getCurrencyManager().getCurrencies().size() + "§7 currencies.");
                    for (Currency currency : plugin.getCurrencyManager().getCurrencies()) {
                        sender.sendMessage("§a§l>> §e" + currency.getSingular());
                    }
                } else if (cmd.equalsIgnoreCase("view")) {
                    if (args.length == 2) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            sender.sendMessage(F.getPrefix() + "§7ID: §c" + currency.getUuid().toString());
                            sender.sendMessage(F.getPrefix() + "§7Singular: §a" + currency.getSingular() + "§7, Plural: §a" + currency.getPlural());
                            sender.sendMessage(F.getPrefix() + "§7Start Balance: " + currency.getColor() + currency.format(currency.getDefaultBalance()) + "§7.");
                            sender.sendMessage(F.getPrefix() + "§7Decimals: " + (currency.isDecimalSupported() ? "§aYes" : "§cNo"));
                            sender.sendMessage(F.getPrefix() + "§7Default: " + (currency.isDefaultCurrency() ? "§aYes" : "§cNo"));
                            sender.sendMessage(F.getPrefix() + "§7Payable: " + (currency.isPayable() ? "§aYes" : "§cNo"));
                            sender.sendMessage(F.getPrefix() + "§7Color: " + currency.getColor() + currency.getColor().name());
                            sender.sendMessage(F.getPrefix() + "§7Rate: " + currency.getColor() + currency.getExchangeRate());
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_View());
                    }
                } else if (cmd.equalsIgnoreCase("startbal")) {
                    if (args.length == 3) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            double amount;
                            block76:
                            {
                                if (currency.isDecimalSupported()) {
                                    try {
                                        amount = Double.parseDouble(args[2]);
                                        if (amount <= 0.0) {
                                            throw new NumberFormatException();
                                        }
                                        break block76;
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(F.getUnvalidAmount());
                                        return;
                                    }
                                }
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
                            currency.setDefaultBalance(amount);
                            sender.sendMessage(F.getPrefix() + "§7Starting balance for §f" + currency.getPlural() + " §7set: §a" + UtilString.format(currency.getDefaultBalance()));
                            plugin.getDataStore().saveCurrency(currency);
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Startbal());
                    }
                } else if (cmd.equalsIgnoreCase("color")) {
                    if (args.length == 3) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            try {
                                ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                                if (color.isFormat()) {
                                    throw new Exception();
                                }
                                currency.setColor(color);
                                sender.sendMessage(F.getPrefix() + "§7Color for §f" + currency.getPlural() + " §7updated: " + color + color.name());
                                plugin.getDataStore().saveCurrency(currency);
                            } catch (Exception ex) {
                                sender.sendMessage(F.getPrefix() + "§cInvalid chat color.");
                            }
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Color());
                    }
                } else if (cmd.equalsIgnoreCase("colorlist")) {
                    sender.sendMessage("§0§lBLACK §7= black");
                    sender.sendMessage("§1§lDARK BLUE §7= dark_blue");
                    sender.sendMessage("§2§lDARK GREEN §7= dark_green");
                    sender.sendMessage("§3§lDARK AQUA §7= dark_aqua");
                    sender.sendMessage("§4§lDARK RED §7= dark_red");
                    sender.sendMessage("§5§lDARK PURPLE §7= dark_purple");
                    sender.sendMessage("§6§lGOLD §7= gold");
                    sender.sendMessage("§7§lGRAY §7= gray");
                    sender.sendMessage("§8§lDARK GRAY §7= dark_gray");
                    sender.sendMessage("§9§lBLUE §7= blue");
                    sender.sendMessage("§a§lGREEN §7= green");
                    sender.sendMessage("§b§lAQUA §7= aqua");
                    sender.sendMessage("§c§lRED §7= red");
                    sender.sendMessage("§d§lLIGHT PURPLE §7= light_purple");
                    sender.sendMessage("§e§lYELLOW §7= yellow");
                    sender.sendMessage("§f§lWHITE §7= white|reset");
                } else if (cmd.equalsIgnoreCase("symbol")) {
                    if (args.length == 3) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            String symbol = args[2];
                            if (symbol.equalsIgnoreCase("remove")) {
                                currency.setSymbol(null);
                                sender.sendMessage(F.getPrefix() + "§7Currency symbol removed for §f" + currency.getPlural());
                                plugin.getDataStore().saveCurrency(currency);
                            } else if (symbol.length() == 1) {
                                currency.setSymbol(symbol);
                                sender.sendMessage(F.getPrefix() + "§7Currency symbol for §f" + currency.getPlural() + " §7updated: §a" + symbol);
                                plugin.getDataStore().saveCurrency(currency);
                            } else {
                                sender.sendMessage(F.getPrefix() + "§7Symbol must be 1 character, or remove it with \"remove\".");
                            }
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Symbol());
                    }
                } else if (cmd.equalsIgnoreCase("default")) {
                    if (args.length == 2) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            Currency c = plugin.getCurrencyManager().getDefaultCurrency();
                            if (c != null) {
                                c.setDefaultCurrency(false);
                                plugin.getDataStore().saveCurrency(c);
                            }
                            currency.setDefaultCurrency(true);
                            sender.sendMessage(F.getPrefix() + "§7Set default currency to §f" + currency.getPlural());
                            plugin.getDataStore().saveCurrency(currency);
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Default());
                    }
                } else if (cmd.equalsIgnoreCase("payable")) {
                    if (args.length == 2) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            currency.setPayable(!currency.isPayable());
                            sender.sendMessage(F.getPrefix() + "§7Toggled payability for §f" + currency.getPlural() + "§7: " + (currency.isPayable() ? "§aYes" : "§cNo"));
                            plugin.getDataStore().saveCurrency(currency);
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Payable());
                    }
                } else if (cmd.equalsIgnoreCase("decimals")) {
                    if (args.length == 2) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            currency.setDecimalSupported(!currency.isDecimalSupported());
                            sender.sendMessage(F.getPrefix() + "§7Toggled Decimal Support for §f" + currency.getPlural() + "§7: " + (currency.isDecimalSupported() ? "§aYes" : "§cNo"));
                            plugin.getDataStore().saveCurrency(currency);
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Decimals());
                    }
                } else if (cmd.equalsIgnoreCase("delete")) {
                    if (args.length == 2) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            plugin.getAccountManager().getAccounts().stream().filter(account -> account.getBalances().containsKey(currency)).forEach(account -> account.getBalances().remove(currency));
                            plugin.getDataStore().deleteCurrency(currency);
                            plugin.getCurrencyManager().getCurrencies().remove(currency);
                            sender.sendMessage(F.getPrefix() + "§7Deleted currency: §a" + currency.getPlural());
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Delete());
                    }
                } else if (cmd.equalsIgnoreCase("setrate")) {
                    if (args.length == 3) {
                        Currency currency = plugin.getCurrencyManager().getCurrency(args[1]);
                        if (currency != null) {
                            double amount;

                            try {
                                amount = Double.parseDouble(args[2]);
                                if (amount <= 0.0) {
                                    throw new NumberFormatException();
                                }
                            } catch (NumberFormatException ex) {
                                sender.sendMessage(F.getUnvalidAmount());
                                return;
                            }
                            currency.setExchangeRate(amount);
                            plugin.getDataStore().saveCurrency(currency);
                            sender.sendMessage(F.getExchangeRateSet().replace("{currencycolor}", "" + currency.getColor()).replace("{currency}", currency.getPlural()).replace("{amount}", String.valueOf(amount)));
                        } else {
                            sender.sendMessage(F.getUnknownCurrency());
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Rate());
                    }
                } else if (cmd.equalsIgnoreCase("convert")) {
                    if (args.length == 2) {
                        String method = args[1];
                        DataStorage current = plugin.getDataStore();
                        DataStorage ds = DataStorage.getMethod(method);

                        if (current == null) {
                            sender.sendMessage(F.getPrefix() + "§7Current Data Store is null. Did something go wrong on startup?");
                            return;
                        }

                        if (ds != null) {
                            if (current.getName().equalsIgnoreCase(ds.getName())) {
                                sender.sendMessage(F.getPrefix() + "§7You can't convert to the same datastore.");
                                return;
                            }

                            plugin.getConfig().set("storage", ds.getName());
                            plugin.saveConfig();

                            sender.sendMessage(F.getPrefix() + "§aLoading data..");
                            plugin.getAccountManager().getAccounts().clear();

                            sender.sendMessage(F.getPrefix() + "§aStored accounts.");
                            ArrayList<Account> offline = new ArrayList<>(plugin.getDataStore().getOfflineAccounts());
                            UtilServer.consoleLog("Stored Accounts: " + offline.size());
                            if (GemsEconomy.getInstance().isDebug()) {
                                for (Account account : offline) {
                                    UtilServer.consoleLog("Account: " + account.getNickname() + " (" + account.getUuid().toString() + ")");
                                    for (Currency currency : account.getBalances().keySet()) {
                                        UtilServer.consoleLog("Balance: " + currency.format(account.getBalance(currency)));
                                    }
                                }
                            }

                            ArrayList<Currency> currencies = new ArrayList<>(plugin.getCurrencyManager().getCurrencies());
                            sender.sendMessage(F.getPrefix() + "§aStored currencies.");
                            plugin.getCurrencyManager().getCurrencies().clear();

                            if (plugin.isDebug()) {
                                for (Currency c : currencies) {
                                    UtilServer.consoleLog("Currency: " + c.getSingular() + "(" + c.getPlural() + "): " + c.format(1000000));
                                }
                            }

                            sender.sendMessage(F.getPrefix() + "§aSwitching from §f" + current.getName() + " §ato §f" + ds.getName() + "§a.");

                            if (ds.getName().equalsIgnoreCase("yaml")) {
                                SchedulerUtils.run(() -> {
                                    File data = new File(GemsEconomy.getInstance().getDataFolder() + File.separator + "data.yml");
                                    if (data.exists()) {
                                        data.delete();
                                    }
                                });
                            }

                            if (plugin.getDataStore() != null) {
                                plugin.getDataStore().close();

                                sender.sendMessage(F.getPrefix() + "§aDataStore is closed. Plugin is essentially dead now.");
                            }

                            plugin.initializeDataStore(ds.getName(), false);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            sender.sendMessage(F.getPrefix() + "§aInitialized " + ds.getName() + " Data Store. Check console for wrong username/password if using mysql.");
                            sender.sendMessage(F.getPrefix() + "§aIf there are sql login errors, you can just retry after you have fixed the credentials, changed the datastore back to what you were using and restarted the server!");

                            if (plugin.getDataStore().getName() != null) {
                                for (Currency c : currencies) {
                                    Currency newCurrency = new Currency(c.getUuid(), c.getSingular(), c.getPlural());
                                    newCurrency.setExchangeRate(c.getExchangeRate());
                                    newCurrency.setDefaultCurrency(c.isDefaultCurrency());
                                    newCurrency.setSymbol(c.getSymbol());
                                    newCurrency.setColor(c.getColor());
                                    newCurrency.setDecimalSupported(c.isDecimalSupported());
                                    newCurrency.setPayable(c.isPayable());
                                    newCurrency.setDefaultBalance(c.getDefaultBalance());
                                    plugin.getDataStore().saveCurrency(newCurrency);
                                }
                                sender.sendMessage(F.getPrefix() + "§aSaved currencies to storage.");
                                plugin.getDataStore().loadCurrencies();
                                sender.sendMessage(F.getPrefix() + "§aLoaded all currencies as usual.");

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }

                                for (Account a : offline) {
                                    plugin.getDataStore().saveAccount(a);
                                }
                                sender.sendMessage(F.getPrefix() + "§aAll accounts saved to storage.");

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }

                                for (Player players : Bukkit.getOnlinePlayers()) {
                                    plugin.getDataStore().loadAccount(players.getUniqueId(), account -> plugin.getAccountManager().add(account));
                                }
                                sender.sendMessage(F.getPrefix() + "§aLoaded all accounts for online players.");
                                sender.sendMessage(F.getPrefix() + "§aData storage conversion is done.");
                            }
                        } else {
                            sender.sendMessage(F.getPrefix() + "§cData Storing method not found.");
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Convert());
                    }
                } else if (cmd.equalsIgnoreCase("backend")) {
                    if (args.length == 2) {
                        String method = args[1];
                        DataStorage current = plugin.getDataStore();
                        DataStorage ds = DataStorage.getMethod(method);

                        if (current == null) {
                            sender.sendMessage(F.getPrefix() + "§7Current Data Store is null. Did something go wrong on startup?");
                            return;
                        }

                        if (ds != null) {
                            if (current.getName().equalsIgnoreCase(ds.getName())) {
                                sender.sendMessage(F.getPrefix() + "§7You can't convert to the same datastore.");
                                return;
                            }


                            plugin.getConfig().set("storage", ds.getName());
                            plugin.saveConfig();

                            sender.sendMessage(F.getPrefix() + "§aSaving data and closing up...");

                            if (plugin.getDataStore() != null) {
                                plugin.getDataStore().close();

                                plugin.getAccountManager().getAccounts().clear();
                                plugin.getCurrencyManager().getCurrencies().clear();

                                sender.sendMessage(F.getPrefix() + "§aSuccessfully shutdown. Booting..");
                            }

                            sender.sendMessage(F.getPrefix() + "§aSwitching from §f" + current.getName() + " §ato §f" + ds.getName() + "§a.");

                            plugin.initializeDataStore(ds.getName(), true);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            for (Player players : Bukkit.getOnlinePlayers()) {
                                plugin.getDataStore().loadAccount(players.getUniqueId(), account -> plugin.getAccountManager().add(account));
                            }
                            sender.sendMessage(F.getPrefix() + "§aLoaded all accounts for online players.");
                        }
                    } else {
                        sender.sendMessage(F.getCurrencyUsage_Backend());
                    }
                } else {
                    sender.sendMessage(F.getUnknownSubCommand());
                }
            }

        });
        return true;
    }

}

