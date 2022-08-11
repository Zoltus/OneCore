package sh.zoltus.onecore.player.command.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.HEAL_YOU_GOT_HEALED;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.HEAL_YOU_HEALED_TARGET;

public class Heal implements IOneCommand {
    @Override
    public void init() {
        //heal
        command(HEAL_LABEL)
                .withPermission(HEAL_PERMISSION)
                .withAliases(HEAL_ALIASES)
                .executesPlayer((player, args) -> {
                    heal(player);
                    player.sendMessage(HEAL_YOU_GOT_HEALED.getString());
                }).override();
        //heal <player>
        command(HEAL_LABEL)
                .withPermission(HEAL_PERMISSION_OTHER)
                .withAliases(HEAL_ALIASES)
                .withArguments(new OfflinePlayerArgument())
                .executes((sender, args) -> {
                    User target = (User) args[0];
                    Player onlineTarget = target.getPlayer();
                    heal(onlineTarget);
                    if (sender != target.getPlayer()) {
                        sender.sendMessage(HEAL_YOU_HEALED_TARGET.rp(MODE_PH, target.getName()));
                    }
                }).register();
    }

    private void heal(OfflinePlayer target) {
        Player p = target.getPlayer();
        if (p != null) {
            AttributeInstance amount = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (amount != null) {
                p.setHealth(amount.getDefaultValue());
            }
            p.sendMessage(HEAL_YOU_GOT_HEALED.getString());
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(target);
            nbtPlayer.setHealth(nbtPlayer.getMaxHealth());
            nbtPlayer.save();
        }
    }
}
