package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;

public class Heal implements IOneCommand {
    @Override
    public void init() {
        //heal <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    User target = (User) args[0];
                    Player onlineTarget = target.getPlayer();
                    heal(onlineTarget);
                    if (sender != target.getPlayer()) {
                        sender.sendMessage(Lang.HEAL_YOU_HEALED_TARGET.rp(IConfig.MODE_PH, target.getName()));
                    }
                });
        //heal
        new Command(Commands.HEAL_LABEL)
                .withPermission(Commands.HEAL_PERMISSION)
                .withAliases(Commands.HEAL_ALIASES)
                .executesPlayer((player, args) -> {
                    heal(player);
                    player.sendMessage(Lang.HEAL_YOU_GOT_HEALED.getString());
                }).then(arg0).override();
    }

    private void heal(OfflinePlayer target) {
        Player p = target.getPlayer();
        if (p != null) {
            AttributeInstance amount = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (amount != null) {
                p.setHealth(amount.getDefaultValue());
            }
            p.sendMessage(Lang.HEAL_YOU_GOT_HEALED.getString());
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(target);
            nbtPlayer.setHealth(nbtPlayer.getMaxHealth());
            nbtPlayer.save();
        }
    }
}
