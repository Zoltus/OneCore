package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import sh.zoltus.onecore.listeners.SignListener;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.utils.FakeBreak;

import java.util.List;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class SignEdit implements IOneCommand {

    private final NamespacedKey signData = new NamespacedKey(plugin, "copied_sign");

    //todo cleanup, color perms?
    @Override
    public void init() {
        //signedit set <line> <text>
        ApiCommand set = command(SIGNEDIT_SET_LABEL)
                .withPermission(SIGNEDIT_SET_PERMISSION)
                .withAliases(SIGNEDIT_SET_ALIASES)
                .withArguments(new IntegerArgument("1-4", 1, 4), signTextArg)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        int line = (int) args[0] - 1;
                        String text = BaseComponent.toPlainText((BaseComponent[]) args[1]);
                        setLine(player, sign, line, text);
                    }

                });
        //signedit clear
        ApiCommand clear = command(SIGNEDIT_CLEAR_LABEL)
                .withPermission(SIGNEDIT_CLEAR_PERMISSION)
                .withAliases(SIGNEDIT_CLEAR_ALIASES)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        for (int i = 0; i < 4; i++) {
                            setLine(player, sign, i, "");
                        }
                    }
                });
        //signedit clear <line>
        ApiCommand clearLine = command(SIGNEDIT_CLEAR_LABEL)
                .withPermission(SIGNEDIT_CLEAR_PERMISSION)
                .withAliases(SIGNEDIT_CLEAR_ALIASES)
                .withArguments(new IntegerArgument("1-4", 1, 4))
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        int line = (int) args[0] - 1;
                        setLine(player, sign, line, "");
                    }
                });
        //signedit copy
        ApiCommand copy = command(SIGNEDIT_COPY_LABEL)
                .withPermission(SIGNEDIT_COPY_PERMISSION)
                .withAliases(SIGNEDIT_COPY_ALIASES)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        player.getPersistentDataContainer()
                                .set(signData, PersistentDataType.STRING, String.join("\n", sign.getLines()));
                        player.sendMessage(SIGNEDIT_SIGN_COPIED.getString());
                    }
                });
        //signedit paste
        ApiCommand paste = command(SIGNEDIT_PASTE_LABEL)
                .withPermission(SIGNEDIT_PASTE_PERMISSION)
                .withAliases(SIGNEDIT_PASTE_ALIASES)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        String signData = player.getPersistentDataContainer()
                                .get(this.signData, PersistentDataType.STRING);
                        if (signData != null) {
                            String[] lines = signData.split("\n");
                            for (int line = 0; line < 4; line++) {
                                setLine(player, sign, line, lines[line]);
                            }
                        }
                    }
                });
        //signedit paste <line>
        ApiCommand pasteLine = command(SIGNEDIT_PASTE_LABEL)
                .withPermission(SIGNEDIT_PASTE_PERMISSION)
                .withAliases(SIGNEDIT_PASTE_ALIASES)
                .withArguments(new IntegerArgument("1-4", 1, 4))
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        int line = (int) args[0] - 1;
                        String signData = player.getPersistentDataContainer()
                                .get(this.signData, PersistentDataType.STRING);
                        if (signData != null) {
                            setLine(player, sign, line, signData);
                        }
                    }
                });

        command(SIGNEDIT_LABEL)
                .withAliases(SIGNEDIT_ALIASES)
                .withPermission(SIGNEDIT_PERMISSION)
                .withSubcommands(set, clear, clearLine, copy, paste, pasteLine)
                .override();
    }

    private void setLine(CommandSender sender, Sign sign, int line, String text) {
        sign.setLine(line, SignListener.toMineHex(text));
        sign.update(true);
        sender.sendMessage(SIGNEDIT_SIGN_UPDATED.getString());
    }

    private final Argument<BaseComponent[]> signTextArg = new ChatArgument(NODES_MESSAGE.getString())
            .withPreview((PreviewLegacy) info -> toComponents(SignListener.toMineHex(info.input())))
            .replaceSuggestions(ArgumentSuggestions.strings(info -> {
                Sign sign = canEdit(info.sender());
                Integer line = (Integer) info.previousArgs()[0] - 1;
                if (sign != null && sign.getLines().length != 0) {
                    return List.of(SignListener.toNormal(sign.getLine(line))).toArray(new String[0]);
                } else {
                    return new String[0];
                }
            }));

    //checks if player can edit sign, and break it
    private Sign canEdit(CommandSender sender) {
        Player p = (Player) sender;
        Sign sign = getTargetSign(p);
        if (sign != null && FakeBreak.canBreak(p, sign.getLocation())
                || sender.hasPermission(SIGNEDIT_BYPASS_PERMISSION.getAsPermission())) {
            return sign;
        } else {
            p.sendMessage(SIGNEDIT_SIGN_NOT_FOUND.getString());
            return null;
        }
    }

    //gets sign player looks at
    private Sign getTargetSign(Player p) {
        Block b = p.getTargetBlockExact(10);
        if (b != null && b.getType().name().contains("_SIGN")) {
            return (Sign) b.getState();
        } else {
            return null;
        }
    }
}
