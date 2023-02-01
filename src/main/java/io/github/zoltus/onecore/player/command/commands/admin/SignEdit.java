package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.utils.ChatUtils;
import io.github.zoltus.onecore.utils.FakeBreak;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.stream.Stream;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class SignEdit implements ICommand {
    private final NamespacedKey store = new NamespacedKey(plugin, "all_lines");
    //todo cleanup, color perms?
    @Override
    public void init() {
        //signedit set <line> <text>
        Argument<?> set = multiLiteralArgument(Commands.SIGNEDIT_SET_LABEL, Commands.SIGNEDIT_SET_ALIASES) //SIGNEDIT_SET_LABEL
                .then(new IntegerArgument("0-3", 0, 3)
                        .then(signTextArg));
        //signedit clear
        Argument<?> clear = multiLiteralArgument(Commands.SIGNEDIT_CLEAR_LABEL, Commands.SIGNEDIT_CLEAR_ALIASES) // SIGNEDIT_CLEAR_LABEL
                .withPermission(Commands.SIGNEDIT_CLEAR_PERMISSION.asPermission())
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        for (int i = 0; i < 4; i++) {
                            setSignText(player, sign, i, "");
                        }
                    }
                });
        //signedit clear <line>
        clear.then(new IntegerArgument("0-3", 0, 3)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        int line = (int) args.get(1);
                        setSignText(player, sign, line, "");
                    }
                }));
        //signedit copy
        Argument<?> copy = multiLiteralArgument(Commands.SIGNEDIT_COPY_LABEL, Commands.SIGNEDIT_COPY_ALIASES) // SIGNEDIT_COPY_ALIASES
                .withPermission(Commands.SIGNEDIT_COPY_PERMISSION.asPermission())
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        writeLines(player, sign.getLines());
                        SIGNEDIT_SIGN_COPIED.send(player);
                    }
                });
        //signedit copy <line>
        copy.then(new IntegerArgument("0-3", 0, 3)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    int line = (int) args.get(1);
                    if (sign != null) {
                        writeLine(player, line, sign.getLine(line));
                        SIGNEDIT_SIGN_COPIED_LINE.send(player, LINE_PH, line);
                    }
                }));
        //signedit paste
        Argument<?> paste = multiLiteralArgument(Commands.SIGNEDIT_PASTE_LABEL, Commands.SIGNEDIT_PASTE_ALIASES) // SIGNEDIT_PASTE_LABEL
                .withPermission(Commands.SIGNEDIT_PASTE_PERMISSION.asPermission())
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        String[] allLines = readLines(player);
                        for (int i = 0; i < allLines.length; i++) {
                            String line = allLines[i];
                            setSignText(player, sign, i, line);
                        }
                    }
                });
        //signedit paste <line>
        paste.then(new IntegerArgument("0-3", 0, 3)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        int i = (int) args.get(1);
                        setSignText(player, sign, i, readLine(player, i));
                    }
                }));
        new Command(Commands.SIGNEDIT_LABEL)
                .withAliases(Commands.SIGNEDIT_ALIASES)
                .withPermission(Commands.SIGNEDIT_PERMISSION)
                .then(set)
                .then(clear)
                .then(copy)
                .then(paste)
                .override();
    }

    private final Argument<BaseComponent[]> signTextArg = new ChatArgument(NODES_MESSAGE.getString())
            .replaceSuggestions(ArgumentSuggestions.strings(info -> {
                Sign sign = canEdit(info.sender());
                if (sign != null && sign.getLines().length != 0) {
                    return Stream.of(sign.getLines()).map(ChatUtils::toNormal).toArray(String[]::new);
                } else {
                    return new String[0];
                }
            }))
            .executesPlayer((player, args) -> {
                Sign sign = canEdit(player);
                if (sign != null) {
                    int line = (int) args.get(1);
                    String text = BaseComponent.toPlainText((BaseComponent[]) args.get(2));
                    setSignText(player, sign, line, text);
                }
            });

    private String readLine(Player p, int line) {
        String[] strings = readLines(p);
        return strings[line];
    }

    private String[] readLines(Player p) {
        PersistentDataContainer cont = p.getPersistentDataContainer();
        String combined = cont.get(store, PersistentDataType.STRING);
        return combined == null ? new String[]{"","","",""} : combined.split("\n");
    }

    private void writeLines(Player p, String[] signLines) {
        String combined = String.join("\n", signLines);
        PersistentDataContainer cont = p.getPersistentDataContainer();
        cont.set(store, PersistentDataType.STRING, combined);
    }

    private void writeLine(Player p, int lineID, String line) {
        String[] readLines = readLines(p);
        readLines[lineID] = line;
        writeLines(p, readLines);
    }

    private void setSignText(CommandSender sender, Sign sign, int line, String text) {
        sign.setLine(line, ChatUtils.toMineHex(text));
        sign.update(true);
        SIGNEDIT_SIGN_UPDATED.send(sender);
    }

    //checks if player can edit sign, and break it
    private Sign canEdit(CommandSender sender) {
        Player p = (Player) sender;
        Sign sign = getTargetSign(p);
        if (sign != null && FakeBreak.canBreak(p, sign.getLocation())
                || sender.hasPermission(Commands.SIGNEDIT_BYPASS_PERMISSION.asPermission())) {
            return sign;
        } else {
            SIGNEDIT_SIGN_NOT_FOUND.send(sender);
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
