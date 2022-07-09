package sh.zoltus.onecore.player.command.commands.admin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.TOP_TELEPORTED_TARGET;
import static sh.zoltus.onecore.configuration.yamls.Lang.TOP_TELPORTED;


public class Top implements IOneCommand {
    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //top
                command(TOP_LABEL)
                        .withPermission(TOP_PERMISSION)
                        .withAliases(TOP_ALIASES)
                        .executesPlayer((p, args) -> {
                    executes(p, p);
                }),
                //top <player>
                command(TOP_LABEL)
                        .withPermission(TOP_OTHER_PERMISSION)
                        .withAliases(TOP_ALIASES)
                        .withArguments(new PlayerArgument())
                        .executes((sender, args) -> {
                    executes(sender, (Player) args[0]);
                }),
        };
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
            sender.sendMessage(TOP_TELPORTED.getString());
        else {
            sender.sendMessage(TOP_TELEPORTED_TARGET.rp(PLAYER_PH, target.getName()));
        }
    }
}
