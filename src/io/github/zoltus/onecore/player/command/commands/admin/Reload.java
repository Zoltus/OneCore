package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;

public class Reload implements IOneCommand {

    @Override
    public void init() {
        //onecore reload todo onecore reload all/config
        new Command(plugin.getName())
                .withAliases("oc")
                .then(multiLiteralArgument(RELOAD_LABEL, RELOAD_ALIASES)
                        .withPermission(RELOAD_PERMISSION.asPermission())
                        .executes((sender, args) -> {
                            sender.sendMessage(Lang.RELOAD_RELOADING.getString());
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                //Reloads all ymls
                                Stream.of(Yamls.values()).forEach(yaml -> yaml.getYml().reload());
                                //Sets all data to enums
                                sender.sendMessage(Lang.RELOAD_RELOADED.getString());
                            });
                        }))
                .override();
    }
}
