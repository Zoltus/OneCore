package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Reload implements ICommand {

    @Override
    public void init() {
        //onecore reload todo onecore reload all/config
        new Command(plugin.getName())
                .withAliases("oc")
                .then(multiLiteralArgument(RELOAD_LABEL, RELOAD_ALIASES)
                        .withPermission(RELOAD_PERMISSION.asPermission())
                        .executes((sender, args) -> {
                            RELOAD_RELOADING.send(sender);
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                //Reloads all ymls
                                Stream.of(Yamls.values()).forEach(yaml -> yaml.getYml().reload());
                                //Sets all data to enums
                                RELOAD_RELOADED.send(sender);
                            });
                        }))
                .override();
    }
}
