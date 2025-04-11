package io.github.zoltus.onecore.player.command.commands.economy;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import io.github.zoltus.onecore.economy.OneEconomy;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.UserArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.AMOUNT_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.BALANCE_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.PLAYER2_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Economy implements ICommand {

    @Override
    public void init() {
        // balance2 //ECONOMY_BALANCE_LABEL
        CommandAPICommand balance = new CommandAPICommand(ECONOMY_BALANCE_LABEL.asLegacyString())
                .withAliases(ECONOMY_BALANCE_ALIASES.getAsArray())
                .withPermission(ECONOMY_BALANCE_PERMISSION.asPermission())
                .executesPlayer((sender, args) -> {
                    handleBalance(sender.getPlayer(), User.of(sender));
                });
        // balance2 <player>
        CommandAPICommand balanceOther = new CommandAPICommand(ECONOMY_BALANCE_LABEL.asLegacyString())
                .withAliases(ECONOMY_BALANCE_ALIASES.getAsArray())
                .withPermission(ECONOMY_BALANCE_PERMISSION.asPermission())
                .withArguments(new UserArgument())
                .executes((sender, args) -> {
                    handleBalance(sender, (User) args.get(0));
                });
        // pay <player> <amount>
        CommandAPICommand pay = new CommandAPICommand(ECONOMY_PAY_LABEL.asLegacyString())
                .withAliases(ECONOMY_PAY_ALIASES.getAsArray())
                .withPermission(ECONOMY_PAY_PERMISSION.asPermission())
                .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.get()))
                .executes((sender, args) -> {
                    transfer(User.of((Player) sender), (User) args.get(0), (double) args.get(1), null);
                });
        // give <player> <amount>
        CommandAPICommand give = new CommandAPICommand(ECONOMY_GIVE_LABEL.asLegacyString())
                .withAliases(ECONOMY_GIVE_ALIASES.getAsArray())
                .withPermission(ECONOMY_GIVE_PERMISSION.asPermission())
                .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.get()))
                .executes((sender, args) -> {
                    User target = (User) args.get(0);
                    double amount = (double) args.get(1);
                    if (target.deposit(amount)) {
                        ECONOMY_GIVE_GAVE.rb(PLAYER_PH, target.getName())
                                .rb(AMOUNT_PH, amount)
                                .rb(BALANCE_PH, target.getBalance())
                                .send(sender);
                        ECONOMY_GIVE_YOUR_BALANCE_WAS_INCREACED
                                .rb(PLAYER_PH, sender.getName())
                                .rb(AMOUNT_PH, amount)
                                .rb(BALANCE_PH, target.getBalance())
                                .send(target);
                    }
                });
        // transfer <player> <player> <amount>
        CommandAPICommand transfer = new CommandAPICommand(ECONOMY_TRANSFER_LABEL.asLegacyString())
                .withAliases(ECONOMY_TRANSFER_ALIASES.getAsArray())
                .withPermission(ECONOMY_TRANSFER_PERMISSION.asPermission())
                .withArguments(new UserArgument("1"), new UserArgument("2"), new DoubleArgument(NODES_AMOUNT.get()))
                .executes((sender, args) -> {
                    double amount = (double) args.get(2);
                    transfer((User) args.get(0), (User) args.get(1), amount, sender);
                });
        // take <player> <amount>
        CommandAPICommand take = new CommandAPICommand(ECONOMY_TAKE_LABEL.asLegacyString())
                .withAliases(ECONOMY_TAKE_ALIASES.getAsArray())
                .withPermission(ECONOMY_TAKE_PERMISSION.asPermission())
                .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.get()))
                .executes((sender, args) -> {
                    User target = (User) args.get(1);
                    double amount = (double) args.get(2);
                    if (target.withdraw(amount)) {
                        ECONOMY_TAKE_TOOK.rb(PLAYER_PH, target.getName())
                                .rb(AMOUNT_PH, amount)
                                .rb(BALANCE_PH, target.getBalance())
                                .send(sender);
                        ECONOMY_TAKE_YOUR_BALANCE_REDUCED
                                .rb(AMOUNT_PH, amount)
                                .rb(BALANCE_PH, target.getBalance())
                                .send(target);
                    } else {
                        ECONOMY_TARGET_DOESNT_HAVE_ENOUGHT_MONEY.rb(PLAYER_PH, target.getName())
                                .rb(AMOUNT_PH, amount)
                                .rb(BALANCE_PH, target.getBalance())
                                .send(sender);
                    }
                });
        // set <player> <amount>
        CommandAPICommand set = new CommandAPICommand(ECONOMY_SET_LABEL.asLegacyString())
                .withAliases(ECONOMY_SET_ALIASES.getAsArray())
                .withPermission(ECONOMY_SET_PERMISSION.asPermission())
                .withArguments(new UserArgument(), new DoubleArgument(NODES_AMOUNT.get()))
                .executes((sender, args) -> {
                    User target = (User) args.get(0);
                    double amount = (double) args.get(1);
                    if (target.setBalance(amount)) {
                        ECONOMY_SET_SET.rb(PLAYER_PH, target.getName())
                                .rb(AMOUNT_PH, amount)
                                .send(sender);
                        ECONOMY_SET_YOUR_BALANCE_WAS_SET
                                .rb(AMOUNT_PH, amount)
                                .send(target);
                    }
                });
        // baltop, todo /baltop reload, automatic works
        CommandAPICommand baltop = new CommandAPICommand(ECONOMY_BALTOP_LABEL.asLegacyString())
                .withAliases(ECONOMY_BALTOP_ALIASES.getAsArray())
                .withPermission(ECONOMY_BALTOP_PERMISSION.asPermission())
                .executesPlayer((sender, args) -> {
                    printBalances(sender, 0);
                });

        //Baltop <page>
        CommandAPICommand baltopPage = new CommandAPICommand(ECONOMY_BALTOP_LABEL.asLegacyString())
                .withAliases(ECONOMY_BALTOP_ALIASES.getAsArray())
                .withPermission(ECONOMY_BALTOP_PERMISSION.asPermission())
                .withArguments(new IntegerArgument("page"))//todo to variable
                .executesPlayer((sender, args) -> {
                    int page = (int) args.get(0);
                    printBalances(sender, page);
                });

        //Singles
        balance.register();
        balanceOther.register();
        pay.override();
        baltop.override();
        baltopPage.register();

        new CommandAPICommand(ECONOMY_LABEL.asLegacyString())
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
                .withSubcommand(baltopPage)
                .override();
    }

    //todo if admin tries to transfer and target doesnt have enought money he gets notified
    private void transfer(User from, User to, double amount, CommandSender admin) {
        if (from.getBalance() < amount) { // Not enought money
            ECONOMY_NOT_ENOUGHT.send(from);
            if (admin != null) {
                ECONOMY_TARGET_DOESNT_HAVE_ENOUGHT_MONEY.rb(PLAYER_PH, from.getName()).send(admin);
            }
        } else if (from.withdraw(amount) && to.deposit(amount)) { // Sent
            ECONOMY_PAY_SENT
                    .rb(AMOUNT_PH, amount)
                    .rb(PLAYER_PH, to.getName())
                    .rb(BALANCE_PH, from.getBalance()).send(from);
            ECONOMY_PAY_RECEIVED
                    .rb(AMOUNT_PH, amount)
                    .rb(PLAYER_PH, from.getName())
                    .rb(BALANCE_PH, from.getBalance()).send(to);
            if (admin != null) {
                ECONOMY_TRANSFER_TRANSFERED
                        .rb(AMOUNT_PH, amount)
                        .rb(PLAYER_PH, from.getName())
                        .rb(PLAYER2_PH, to.getName()).send(admin);
            }
        }
    }

    private void handleBalance(CommandSender sender, User target) {
        double balance = target.getBalance();
        if (sender == target.getPlayer()) {
            ECONOMY_BALANCE_YOUR_BALANCE.rb(BALANCE_PH, balance).send(sender);
        } else {
            ECONOMY_BALANCE_TARGETS_BALANCE.rb(PLAYER_PH, target.getName())
                    .rb(BALANCE_PH, balance)
                    .send(sender);
        }
    }

    public void printBalances(CommandSender sender, int page) {
        Map<UUID, Double> balances = OneEconomy.getBalanceTop();
        if (balances.isEmpty()) {
            ECONOMY_BALTOP_EMPTY.send(sender);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int startIndex = Math.min(page * 10, balances.size());
                int endIndex = Math.min(startIndex + 10, balances.size());
                List<Map.Entry<UUID, Double>> pageEntries = new ArrayList<>(balances.entrySet())
                        .subList(startIndex, endIndex);
                if (pageEntries.isEmpty()) {
                    sender.sendMessage("No data available.");
                    return;
                }
                ECONOMY_BALTOP_TOP_PLAYERS.send(sender);
                pageEntries.forEach(entry -> {
                    String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                    ECONOMY_BALTOP_LINE.rb(PLAYER_PH, name)
                            .rb(AMOUNT_PH, entry.getValue())
                            .send(sender);
                });
            });

        }
    }
}
            /*
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                //todo cleanup
                int startIndex = page * 10;
                int endIndex = startIndex + 10;
                if (startIndex > balances.size()) {
                    //Get the last 10 players from the map balances:
                    List<Map.Entry<UUID, Double>> lastEntries = new ArrayList<>(balances.entrySet());
                    lastEntries.subList(Math.max(lastEntries.size() - 10, 0), lastEntries.size())
                            .forEach(entry -> {
                                UUID uuid = entry.getKey();
                                double balance = entry.getValue();
                                String name = Bukkit.getOfflinePlayer(uuid).getName();
                                ECONOMY_BALTOP_LINE.send(sender, PLAYER_PH, name, AMOUNT_PH, balance);
                            });
                } else {
                    sender.sendMessage(ECONOMY_BALTOP_TOP_PLAYERS.getString());
                    int index = 0;
                    for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
                        if (startIndex <= index && index < endIndex) {
                            UUID uuid = entry.getKey();
                            double value = entry.getValue();
                            String name = Bukkit.getOfflinePlayer(uuid).getName();
                            ECONOMY_BALTOP_LINE.send(sender, PLAYER_PH, name, AMOUNT_PH, value);
                        }
                        index++;
                    }
                }
            });
            */
