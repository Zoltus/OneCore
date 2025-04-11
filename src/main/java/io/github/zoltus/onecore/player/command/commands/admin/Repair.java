package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.PlaceHolder.SLOT_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Repair implements ICommand {

    //todo cleanup, to enum
    private final List<String> slots = Arrays.asList(
            REPAIR_SLOT_HAND.asLegacyString().toLowerCase(),
            REPAIR_SLOT_ALL.asLegacyString().toLowerCase(),
            REPAIR_SLOT_OFFHAND.asLegacyString().toLowerCase(),
            REPAIR_SLOT_ARMOR.asLegacyString().toLowerCase(),
            REPAIR_SLOT_INVENTORY.asLegacyString().toLowerCase());

    private Argument<?> slotArg() {
        return new CustomArgument<>(new StringArgument(NODES_SLOT.get()), (info) -> {
            String input = info.input();
            if (!slots.contains(input.toLowerCase())) {
                throw CustomArgument.CustomArgumentException.fromBaseComponents(TextComponent.fromLegacyText(REPAIR_SLOT_INVALID_SLOT.get()));
            } else {
                return input;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> toSuggestion(info.currentArg(), slots.toArray(new String[0]))));
    }

    @Override
    public void init() {
        //repair <hand/all>
        Argument<?> args1 = slotArg()
                .executesPlayer((p, args) -> {
                    handleRepair(p, p, (String) args.get(0));
                });
        //repair <hand/all> <player>
        Argument<?> args2 = new PlayerArgument()
                .executes((sender, args) -> {
                    handleRepair(sender, (Player) args.get(1), (String) args.get(0));
                });
        //repair (hand) default
        new Command(Commands.REPAIR_LABEL)
                .withPermission(Commands.REPAIR_PERMISSION)
                .withAliases(Commands.REPAIR_ALIASES)
                .executesPlayer((p, args) -> {
                    handleRepair(p, p, slots.get(0));
                }).then(args1.then(args2))
                .override();
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
            REPAIR_NOTHING_TO_FIX.rb(SLOT_PH, slot).send(sender);
        } else if (sender == target.getPlayer()) {
            REPAIR_SELF_REPAIRED.rb(SLOT_PH, slot).send(target);
        } else {
            REPAIR_YOU_REPAIRED_TARGET.rb(SLOT_PH, slot)
                    .rb(PLAYER_PH, target.getName())
                    .send(sender);
            REPAIR_YOUR_ITEMS_GOT_REPAIRED.rb(SLOT_PH, slot).send(target);
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
