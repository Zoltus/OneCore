package sh.zoltus.onecore.player.command.commands;

import org.bukkit.Bukkit;
import sh.zoltus.onecore.configuration.Yamls;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;

import java.util.stream.Stream;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.RELOAD_RELOADED;
import static sh.zoltus.onecore.configuration.yamls.Lang.RELOAD_RELOADING;

public class Reload implements IOneCommand {

    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //onecore reload todo onecore reload all/config
                command(plugin.getName())
                        .withAliases("oc")
                        .withSubcommand(
                        command(RELOAD_LABEL)
                                .withPermission(RELOAD_PERMISSION)
                                .withAliases(RELOAD_ALIASES)
                                .executes((sender, args) -> {
                                    sender.sendMessage(RELOAD_RELOADING.getString());
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                        //Reloads all ymls
                                        Stream.of(Yamls.values()).forEach(yaml -> yaml.getYml().reload());
                                        //Sets all data to enums
                                        sender.sendMessage(RELOAD_RELOADED.getString());
                                    });
                                }))

        };
    }
}
