package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Top implements IOneCommand {
    @Override
    public void init() {
        //top <player>
        ArgumentTree arg0 = new PlayerArgument()
                .executes((sender, args) -> {
                    executes(sender, (Player) args[0]);
                });
        //top
        new Command(Commands.TOP_LABEL)
                .withPermission(Commands.TOP_PERMISSION)
                .withAliases(Commands.TOP_ALIASES)
                .executesPlayer((p, args) -> {
                    executes(p, p);
                }).then(arg0)
                .override();
    }

    private void executes(CommandSender sender, Player target) {
        World w = target.getWorld();
        Location loc = target.getLocation();
        //Gets highest block and adds +1 to y
        loc.setY(w.getHighestBlockYAt(loc) + 1);
        //Sets fall distance to 0 so player wont take any fall damage
        target.setFallDistance(0);
        //Resets player velocity so player is falling and teleports to water floating, it wont just fall throught because of the velocity
        target.teleport(loc);
        if (target == sender)
            sender.sendMessage(Lang.TOP_TELPORTED.getString());
        else {
            sender.sendMessage(Lang.TOP_TELEPORTED_TARGET.rp(IConfig.PLAYER_PH, target.getName()));
        }
    }
}
