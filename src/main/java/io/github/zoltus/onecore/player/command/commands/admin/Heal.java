package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.IConfig.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.HEAL_YOU_GOT_HEALED;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.HEAL_YOU_HEALED_TARGET;

public class Heal implements ICommand {
    @Override
    public void init() {
        //heal <player>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(Commands.HEAL_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    OfflinePlayer offP = (OfflinePlayer) args.get(0);
                    heal(offP);
                    if (sender != offP.getPlayer()) {
                        HEAL_YOU_HEALED_TARGET.send(sender, PLAYER_PH, offP.getName());
                    }
                });
        //heal
        new Command(Commands.HEAL_LABEL)
                .withPermission(Commands.HEAL_PERMISSION)
                .withAliases(Commands.HEAL_ALIASES)
                .executesPlayer((player, args) -> {
                    heal(player);
                }).then(arg0).override();
    }

    private void heal(OfflinePlayer target) {
        Player p = target.getPlayer();
        if (p != null) {
            AttributeInstance amount = p.getAttribute(Attribute.MAX_HEALTH);
            if (amount != null) {
                p.setHealth(amount.getDefaultValue());
            }
            HEAL_YOU_GOT_HEALED.send(p);
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(target);
            nbtPlayer.setHealth(nbtPlayer.getMaxHealth());
            nbtPlayer.save();
        }
    }
}
