package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.LocationArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.commands.regular.Spawn;
import org.bukkit.Location;

public class SetSpawn implements IOneCommand {

    @Override
    public void init() {
        //setspawn <location>
        ArgumentTree arg0 = new LocationArgument(Lang.NODES_LOCATION.getString())
                .executesPlayer((p, args) -> {
                    Spawn.setSpawn((Location) args[0]);
                    p.sendMessage(Lang.SETSPAWN_SET.getString());
                });
        //setspawn
        new Command(Commands.SETSPAWN_LABEL)
                .withPermission(Commands.SETSPAWN_PERMISSION)
                .withAliases(Commands.SETSPAWN_ALIASES)
                .executesPlayer((p, args) -> {
                    Spawn.setSpawn(p.getLocation());
                    p.sendMessage(Lang.SETSPAWN_SET.getString());
                }).then(arg0).override();
    }
}
