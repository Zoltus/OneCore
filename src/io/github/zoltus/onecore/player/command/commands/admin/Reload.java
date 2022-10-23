package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.IOneCommand;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class Reload implements IOneCommand {

    @Override
    public void init() {
       //ArgumentTree arg0 =

       // new Command(plugin.getName())
        //        .withAliases("oc");

        //onecore reload todo onecore reload all/config
        command(plugin.getName())
                .withAliases("oc")
                .withSubcommand(
                        command(Commands.RELOAD_LABEL)
                                .withPermission(Commands.RELOAD_PERMISSION)
                                .withAliases(Commands.RELOAD_ALIASES)
                                .executes((sender, args) -> {
                                    sender.sendMessage(Lang.RELOAD_RELOADING.getString());
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                        //Reloads all ymls
                                        Stream.of(Yamls.values()).forEach(yaml -> yaml.getYml().reload());
                                        //Sets all data to enums
                                        sender.sendMessage(Lang.RELOAD_RELOADED.getString());
                                    });
                                })).register();


    }
}
