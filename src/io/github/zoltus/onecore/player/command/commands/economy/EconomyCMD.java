package io.github.zoltus.onecore.player.command.commands.economy;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.economy.OneEconomy;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.UserArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.AMOUNT_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.BALANCE_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.PLAYER2_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class EconomyCMD implements ICommand {

    @Override
    public void init() {
        // balance2 //ECONOMY_BALANCE_LABEL
        CommandAPICommand balance = new CommandAPICommand(ECONOMY_BALANCE_LABEL.getString())
                .withAliases(ECONOMY_BALANCE_ALIASES.getAsArray())
                .withPermission(ECONOMY_BALANCE_PERMISSION.asPermission())
                .executesPlayer((sender, args) -> {
                    handleBalance(sender.getPlayer(), User.of(sender));
                });
        // balance2 <player>
        CommandAPICommand balanceOther = new CommandAPICommand(ECONOMY_BALANCE_LABEL.getString())
                .withAliases(ECONOMY_BALANCE_ALIASES.getAsArray())
                .withPermission(ECONOMY_BALANCE_PERMISSION.asPermission())
                .withArguments(new UserArgument())
                .executes((sender, args) -> {
                    handleBalance(sender, (User) args[0]);
                });
        // pay <player> <amount>
        CommandAPICommand pay = new CommandAPICommand(ECONOMY_PAY_LABEL.getString())
                .withAliases(ECONOMY_PAY_ALIASES.getAsArray())
                .withPermission(ECONOMY_PAY_PERMISSION.asPermission())
                .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.getString()))
                .executes((sender, args) -> {
                    transfer(User.of((OfflinePlayer) sender), (User) args[0], (double) args[1], null);
                });
        // give <player> <amount>
        CommandAPICommand give = new CommandAPICommand(ECONOMY_GIVE_LABEL.getString())
                .withAliases(ECONOMY_GIVE_ALIASES.getAsArray())
                .withPermission(ECONOMY_GIVE_PERMISSION.asPermission())
                .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.getString()))
                .executes((sender, args) -> {
                    User target = (User) args[0];
                    double amount = (double) args[1];
                    if (target.deposit(amount)) {
                        ECONOMY_GIVE_GAVE.send(sender,
                                PLAYER_PH, target.getName(),
                                AMOUNT_PH, amount, BALANCE_PH,
                                target.getBalance());
                        ECONOMY_GIVE_YOUR_BALANCE_WAS_INCREACED.send(target,
                                PLAYER_PH, sender.getName(),
                                AMOUNT_PH, amount,
                                BALANCE_PH, target.getBalance());
                    }
                });
        // transfer <player> <player> <amount>
        CommandAPICommand transfer = new CommandAPICommand(ECONOMY_TRANSFER_LABEL.getString())
                .withAliases(ECONOMY_TRANSFER_ALIASES.getAsArray())
                .withPermission(ECONOMY_TRANSFER_PERMISSION.asPermission())
                .withArguments(new UserArgument("1"), new UserArgument("2"), new DoubleArgument(NODES_AMOUNT.getString()))
                .executes((sender, args) -> {
                    double amount = (double) args[2];
                    transfer((User) args[0], (User) args[1], amount, sender);
                });
        // take <player> <amount>
        CommandAPICommand take = new CommandAPICommand(ECONOMY_TAKE_LABEL.getString())
                .withAliases(ECONOMY_TAKE_ALIASES.getAsArray())
                .withPermission(ECONOMY_TAKE_PERMISSION.asPermission())
                .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.getString()))
                .executes((sender, args) -> {
                    User target = (User) args[1];
                    double amount = (double) args[2];
                    if (target.withdraw(amount)) {
                        ECONOMY_TAKE_TOOK.send(sender,
                                PLAYER_PH, target.getName(),
                                AMOUNT_PH, amount,
                                BALANCE_PH, target.getBalance());
                        ECONOMY_TAKE_YOUR_BALANCE_REDUCED.send(target,
                                AMOUNT_PH, amount,
                                BALANCE_PH, target.getBalance());
                    } else {
                        ECONOMY_TARGET_DOESNT_HAVE_ENOUGHT_MONEY.send(sender,
                                PLAYER_PH, target.getName(),
                                AMOUNT_PH, amount,
                                BALANCE_PH, target.getBalance());
                    }
                });
        // set <player> <amount>
        CommandAPICommand set = new CommandAPICommand(ECONOMY_SET_LABEL.getString())
                .withAliases(ECONOMY_SET_ALIASES.getAsArray())
                .withPermission(ECONOMY_SET_PERMISSION.asPermission())
                .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.getString()))
                .executes((sender, args) -> {
                    User target = (User) args[1];
                    double amount = (double) args[2];
                    if (target.setBalance(amount)) {
                        ECONOMY_SET_SET.send(sender,
                                PLAYER_PH, target.getName(),
                                AMOUNT_PH, amount);
                        ECONOMY_SET_YOUR_BALANCE_WAS_SET.send(target, AMOUNT_PH, amount);
                    }
                });
        // baltop, todo /baltop reload
        CommandAPICommand baltop = new CommandAPICommand(ECONOMY_BALTOP_LABEL.getString())
                .withAliases(ECONOMY_BALTOP_ALIASES.getAsArray())
                .withPermission(ECONOMY_BALTOP_PERMISSION.asPermission())
                .executesPlayer((sender, args) -> {
                    if (!Config.ECONOMY.getBoolean() || !Config.ECONOMY_USE_ONEECONOMY.getBoolean()) {
                        sender.sendMessage("To use baltop cmd u need to use oneeconomy & have economy enabled!");
                    } else {
                        ConcurrentHashMap<UUID, Double> top = OneEconomy.getBalances();
                        if (!top.isEmpty()) {
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> top.entrySet()
                                    .stream()
                                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                    .limit(10)
                                    .forEachOrdered(x -> {
                                        UUID uuid = x.getKey();
                                        double value = x.getValue();
                                        String name = Bukkit.getOfflinePlayer(uuid).getName();
                                        ECONOMY_BALTOP_LINE.send(sender, PLAYER_PH, name, AMOUNT_PH, value);
                                    }));
                        } else {
                            sender.sendMessage(ECONOMY_BALTOP_EMPTY.getString());
                        }
                    }
                });

        //Singles
        balance.register();
        balanceOther.register();
        give.override();
        transfer.override();
        take.override();
        pay.override();
        set.override();
        baltop.override();

        new CommandAPICommand(ECONOMY_LABEL.getString())
                .withAliases(ECONOMY_ALIASES.getAsArray())
                .withPermission(ECONOMY_PERMISSION.asPermission())
                .withSubcommand(balance)
                .withSubcommand(balanceOther)
                .withSubcommand(give)
                .withSubcommand(pay)
                .withSubcommand(transfer)
                .withSubcommand(take)
                .withSubcommand(set)
                .withSubcommand(baltop)
                .override();
    }

    //todo if admin tries to transfer and target doesnt have enought money he gets notified
    private void transfer(User from, User to, double amount, CommandSender admin) {
        if (from.getBalance() < amount) { // Not enought money
            from.sendMessage(ECONOMY_NOT_ENOUGHT.getString());
            if (admin != null) {
                ECONOMY_TARGET_DOESNT_HAVE_ENOUGHT_MONEY.send(admin, PLAYER_PH, from.getName());
            }
        } else if (from.withdraw(amount) && to.deposit(amount)) { // Sent
            ECONOMY_PAY_SENT.send(from,
                    AMOUNT_PH, amount,
                    PLAYER_PH, to.getName(),
                    BALANCE_PH, from.getBalance());
            ECONOMY_PAY_RECEIVED.send(to,
                    AMOUNT_PH, amount,
                    PLAYER_PH, from.getName(),
                    BALANCE_PH, from.getBalance());
            if (admin != null) {
                ECONOMY_TRANSFER_TRANSFERED.send(admin,
                        AMOUNT_PH, amount,
                        PLAYER_PH, from.getName(),
                        PLAYER2_PH, to.getName());
            }
        }
    }

    private void handleBalance(CommandSender sender, User target) {
        double balance = target.getBalance();
        if (sender == target.getPlayer()) {
            ECONOMY_BALANCE_YOUR_BALANCE.send(sender, BALANCE_PH, balance);
        } else {
            ECONOMY_BALANCE_TARGETS_BALANCE.send(sender,
                    PLAYER_PH, target.getName(),
                    BALANCE_PH, balance);
        }
    }
}
