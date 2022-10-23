package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.economy.OneEconomy;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.ApiCommand;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.command.arguments.UserArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.AMOUNT_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.BALANCE_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.PLAYER2_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class EconomyCMD implements IOneCommand {

    //private Economy economy = plugin.getVault();

    @Override
    public void init() {
        // Economy
       /* command(ECONOMY_LABEL)
                .withPermission(ECONOMY_PERMISSION)
                .withAliases(ECONOMY_ALIASES)
                // .executes((sender, args) -> {
                //todo
                //    sender.sendMessage("not done");
                // })
                //.withSubcommands(pay, give, transfer, set, take)
               // .withSeparateSubcommands(balance, balance2, pay, balTop)
                .override();*/

        // balance //ECONOMY_BALANCE_LABEL
        ArgumentTree balance = multiLiteralArgument(ECONOMY_BALANCE_LABEL, ECONOMY_BALANCE_ALIASES)
                .withPermission(ECONOMY_BALANCE_PERMISSION.getString())
                .executesPlayer((sender, args) -> {
                    handleBalance(sender.getPlayer(), User.of(sender));
                });

        // balance <player>
        ArgumentTree balanceOther = new OfflinePlayerArgument()
                .withPermission(ECONOMY_BALANCE_PERMISSION.getString())
                .executes((sender, args) -> {
                    handleBalance(sender, User.of((OfflinePlayer) args[0]));
                });

        ArgumentTree pay = multiLiteralArgument(ECONOMY_PAY_LABEL, ECONOMY_PAY_ALIASES)
                .withPermission(ECONOMY_PAY_PERMISSION.getString())
                .then(new OfflinePlayerArgument()
                        .then(new DoubleArgument(NODES_AMOUNT.getString())
                                .executes((sender, args) -> {
                                    transfer(User.of((OfflinePlayer) sender), (User) args[0], (double) args[1], null)
                                })));




        new Command(ECONOMY_LABEL)
                .withPermission(ECONOMY_PERMISSION)
                .withAliases(ECONOMY_ALIASES)
                .executes((sender, args) -> sender.sendMessage("not done"))
                .then(balance.then(balanceOther))
                .then(pay)
                .override();

    }

    // baltop, /baltop reload
    private final ApiCommand balTop = command(ECONOMY_BALTOP_LABEL) //todo make toplist only work with oneeconomy
            .withPermission(ECONOMY_BALTOP_PERMISSION)
            .withAliases(ECONOMY_BALTOP_ALIASES)
            .executesPlayer((sender, args) -> {
                if (!Config.ECONOMY.getBoolean() || !Config.ECONOMY_USE_ONEECONOMY.getBoolean()) {
                    sender.sendMessage("To use baltop cmd u need to use oneeconomy & have economy enabled!");
                    return;
                }

                ConcurrentHashMap<UUID, Double> top = OneEconomy.getBalances();
                if (!top.isEmpty()) { //todo cleanup
                    ConcurrentHashMap<UUID, Double> sortedMap = new ConcurrentHashMap<>();
                    top.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) //todo improve
                            .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        sender.sendMessage(ECONOMY_BALTOP_TOP_PLAYERS.getString() + " size: " + top.size());
                        Iterator<Map.Entry<UUID, Double>> it = sortedMap.entrySet().iterator();
                        int topAmount = 10;
                        while (it.hasNext() && topAmount != 0) {
                            Map.Entry<UUID, Double> entry = it.next();
                            UUID uuid = entry.getKey();
                            double value = entry.getValue();
                            String name = Bukkit.getOfflinePlayer(uuid).getName();
                            sender.sendMessage(ECONOMY_BALTOP_LINE.rp(PLAYER_PH, name, AMOUNT_PH, value));
                            topAmount--;
                        }
                    });
                } else {
                    sender.sendMessage(ECONOMY_BALTOP_EMPTY.getString());
                }
            });

    // give <player> <amount>
    private final ApiCommand give = command(ECONOMY_GIVE_LABEL)
            .withPermission(ECONOMY_GIVE_PERMISSION)
            .withAliases(ECONOMY_GIVE_ALIASES)
            .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.getString()))
            .executes((sender, args) -> {
                User target = (User) args[0];
                double amount = (double) args[1];
                if (target.deposit(amount)) {
                    sender.sendMessage(ECONOMY_GIVE_GAVE.rp(
                            PLAYER_PH, target.getName(),
                            AMOUNT_PH, amount,
                            BALANCE_PH, target.getBalance()
                    ));
                    target.sendMessage(ECONOMY_GIVE_YOUR_BALANCE_WAS_INCREACED.rp(
                            PLAYER_PH, sender.getName(), AMOUNT_PH, amount,
                            BALANCE_PH, target.getBalance()
                    ));
                }
            });

    // transfer <player> <player> <amount>
    private final ApiCommand transfer = command(ECONOMY_TRANSFER_LABEL)
            .withPermission(ECONOMY_TRANSFER_PERMISSION)
            .withAliases(ECONOMY_TRANSFER_ALIASES)
            .withArguments(new UserArgument(), new UserArgument("2"), new DoubleArgument(NODES_AMOUNT.getString()))
            .executes((sender, args) -> {
                double amount = (double) args[2];
                transfer((User) args[0], (User) args[1], amount, sender);
            });

    // set <player> <amount>
    private final ApiCommand set = command(ECONOMY_SET_LABEL)
            .withPermission(ECONOMY_SET_PERMISSION)
            .withAliases(ECONOMY_SET_ALIASES)
            .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.getString()))
            .executes((sender, args) -> {
                User target = (User) args[0];
                double amount = (double) args[1];
                if (target.setBalance(amount)) {
                    sender.sendMessage(ECONOMY_SET_SET.rp(PLAYER_PH, target.getName(), AMOUNT_PH, amount));
                    target.sendMessage(ECONOMY_SET_YOUR_BALANCE_WAS_SET.rp(AMOUNT_PH, amount));
                }
            });

    // take <player> <amount>
    private final ApiCommand take = command(ECONOMY_TAKE_LABEL)
            .withPermission(ECONOMY_TAKE_PERMISSION)
            .withAliases(ECONOMY_TAKE_ALIASES)
            .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.getString()))
            .executes((sender, args) -> {
                User target = (User) args[0];
                double amount = (double) args[1];
                if (target.withdraw(amount)) {
                    sender.sendMessage(ECONOMY_TAKE_TOOK.rp(
                            PLAYER_PH, target.getName(),
                            AMOUNT_PH, amount,
                            BALANCE_PH, target.getBalance()
                    ));
                    target.sendMessage(ECONOMY_TAKE_YOUR_BALANCE_REDUCED.rp(
                            AMOUNT_PH, amount,
                            BALANCE_PH, target.getBalance()
                    ));
                } else {
                    sender.sendMessage(ECONOMY_TARGET_DOESNT_HAVE_ENOUGHT_MONEY.rp(PLAYER_PH, target.getName()));
                }
            });


    //todo, to economyclass
    private void transfer(User from, User to, double amount, CommandSender admin) {
        if (from.getBalance() < amount) { // Not enought money
            from.sendMessage(ECONOMY_NOT_ENOUGHT.getString());
            if (admin != null) {
                admin.sendMessage(ECONOMY_TARGET_DOESNT_HAVE_ENOUGHT_MONEY.rp(PLAYER_PH, from.getName()));
            }
        } else if (from.withdraw(amount) && to.deposit(amount)) { // Sent
            from.sendMessage(ECONOMY_PAY_SENT.rp(
                    AMOUNT_PH, amount,
                    PLAYER_PH, to.getName(),
                    BALANCE_PH, from.getBalance()
            ));
            to.sendMessage(ECONOMY_PAY_RECEIVED.rp(
                    AMOUNT_PH, amount,
                    PLAYER_PH, from.getName(),
                    BALANCE_PH, from.getBalance()
            ));
            if (admin != null) {
                admin.sendMessage(ECONOMY_TRANSFER_TRANSFERED.rp(
                        AMOUNT_PH, amount,
                        PLAYER_PH, from.getName(),
                        PLAYER2_PH, to.getName())
                );
            }
        }
    }

    private void handleBalance(CommandSender sender, User target) {
        double balance = target.getBalance();
        if (sender == target.getPlayer()) {
            sender.sendMessage(ECONOMY_BALANCE_YOUR_BALANCE
                    .rp(BALANCE_PH, balance));
        } else {
            sender.sendMessage(ECONOMY_BALANCE_TARGETS_BALANCE
                    .rp(PLAYER_PH, target.getName(), BALANCE_PH, balance));
        }
    }
}
