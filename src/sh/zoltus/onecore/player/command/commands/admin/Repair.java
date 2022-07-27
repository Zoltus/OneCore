package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;

import java.util.Arrays;
import java.util.List;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class Repair implements IOneCommand {

    //todo cleanup, to enum
    private final List<String> slots = Arrays.asList(
            REPAIR_SLOT_HAND.getString().toLowerCase(),
            REPAIR_SLOT_ALL.getString().toLowerCase(),
            REPAIR_SLOT_OFFHAND.getString().toLowerCase(),
            REPAIR_SLOT_ARMOR.getString().toLowerCase(),
            REPAIR_SLOT_INVENTORY.getString().toLowerCase());

    private Argument<?> slotArg() {
        return new CustomArgument<>(new StringArgument(NODES_SLOT.getString()), (info) -> {
            String input = info.input();
            if (!slots.contains(input.toLowerCase())) {
                throw new CustomArgument.CustomArgumentException(REPAIR_SLOT_INVALID_SLOT.getString());
            } else {
                return input;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> toSuggestion(info.currentArg(), slots.toArray(new String[0]))));
    }

    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //repair (hand)
                command(REPAIR_LABEL)
                        .withPermission(REPAIR_PERMISSION)
                        .withAliases(REPAIR_ALIASES)
                        .executesPlayer((p, args) -> {
                    handleRepair(p, p, slots.get(0));
                }),
                //repair <hand/all>
                command(REPAIR_LABEL)
                        .withPermission(REPAIR_PERMISSION)
                        .withAliases(REPAIR_ALIASES)
                        .withArguments(slotArg())
                        .executesPlayer((p, args) -> {
                    handleRepair(p, p, (String) args[0]);
                }),

                //repair <hand/all> <player>
                command(REPAIR_LABEL)
                        .withPermission(REPAIR_PERMISSION)
                        .withAliases(REPAIR_ALIASES)
                        .withArguments(slotArg(), new PlayerArgument())
                        .executes((sender, args) -> handleRepair(sender, (Player) args[1], (String) args[0]))
        };
    }

    private void handleRepair(CommandSender sender, Player target, String slot) {
        PlayerInventory inv = target.getInventory();
        boolean fixedAny = false;

        if (slots.get(0).equalsIgnoreCase(slot)) {
            fixedAny = repair(inv.getItemInMainHand());
        } else if (slots.get(1).equalsIgnoreCase(slot)) {
            fixedAny = repair(inv.getContents());
        } else if (slots.get(2).equalsIgnoreCase(slot)) {
            fixedAny = repair(inv.getItemInOffHand());
        } else if (slots.get(3).equalsIgnoreCase(slot)) {
            fixedAny = repair(inv.getArmorContents());
        } else if (slots.get(4).equalsIgnoreCase(slot)) {
            fixedAny = repair(inv.getStorageContents());
        }

        if (!fixedAny) {
            sender.sendMessage(REPAIR_NOTHING_TO_FIX.rp(SLOT_PH, slot));
        } else if (sender == target.getPlayer()) {
            sender.sendMessage(REPAIR_SELF_REPAIRED.rp(SLOT_PH, slot));
        } else {
            sender.sendMessage(REPAIR_YOU_REPAIRED_TARGET.rp(SLOT_PH, slot, PLAYER_PH, target.getName()));
            target.sendMessage(REPAIR_YOUR_ITEMS_GOT_REPAIRED.rp(SLOT_PH, slot));
        }
    }

    private boolean repair(ItemStack... stacks) {
        boolean fixedAny = false;
      /*  Stream.of(stacks)
                .filter(stack -> stack != null && stack.hasItemMeta())
                .filter(stack -> stack.getItemMeta() instanceof Damageable damageMeta && damageMeta.hasDamage())
                .forEach(stack -> {
                    Damageable damageMeta = (Damageable) stack.getItemMeta();
                    damageMeta.setDamage(-stack.getType().getMaxDurability());
                    stack.setItemMeta(damageMeta);
                    //fixedAny = true;
                });*/ //todo

        for (ItemStack stack : stacks) {
            if (stack != null && stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();
                if (meta instanceof Damageable damageMeta && damageMeta.hasDamage()) {
                    damageMeta.setDamage(-stack.getType().getMaxDurability());
                    stack.setItemMeta(meta);
                    fixedAny = true;
                }
            }
        }
        return fixedAny;
    }
}
