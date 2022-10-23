package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SignEdit implements IOneCommand {

    private final NamespacedKey line1 = new NamespacedKey(plugin, "line1");
    private final NamespacedKey line2 = new NamespacedKey(plugin, "line2");
    private final NamespacedKey line3 = new NamespacedKey(plugin, "line3");
    private final NamespacedKey line4 = new NamespacedKey(plugin, "line4");

    private List<String> getLines(Player p) {
        PersistentDataContainer cont = p.getPersistentDataContainer();
        List<String> lines = Arrays.asList("", "", "", "");
        lines.set(0, cont.get(line1, PersistentDataType.STRING));
        lines.set(1, cont.get(line2, PersistentDataType.STRING));
        lines.set(2, cont.get(line3, PersistentDataType.STRING));
        lines.set(3, cont.get(line4, PersistentDataType.STRING));
        return lines;
    }

    private void setLine(Player p, int line, String text) {
        text = text == null ? "" : text;
        PersistentDataContainer cont = p.getPersistentDataContainer();
        switch (line) {
            case 0 -> cont.set(line1, PersistentDataType.STRING, text);
            case 1 -> cont.set(line2, PersistentDataType.STRING, text);
            case 2 -> cont.set(line3, PersistentDataType.STRING, text);
            case 3 -> cont.set(line4, PersistentDataType.STRING, text);
        }
    }

    //todo cleanup, color perms?
    @Override
    public void init() {
        //signedit set <line> <text>
        ArgumentTree set = multiLiteralArgument(Commands.SIGNEDIT_SET_LABEL, Commands.SIGNEDIT_SET_ALIASES) //SIGNEDIT_SET_LABEL
                .then(new IntegerArgument("1-4", 1, 4)
                        .then(signTextArg));
        //signedit clear
        ArgumentTree clear = multiLiteralArgument(Commands.SIGNEDIT_CLEAR_LABEL, Commands.SIGNEDIT_CLEAR_ALIASES) // SIGNEDIT_CLEAR_LABEL
                .withPermission(Commands.SIGNEDIT_CLEAR_PERMISSION.asPermission())
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        for (int i = 0; i < 4; i++) {
                            setLine(player, sign, i, "");
                        }
                    }
                });
        //signedit clear <line>
        clear.then(new IntegerArgument("1-4", 1, 4)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        int line = (int) args[0] - 1;
                        setLine(player, sign, line, "");
                    }
                }));
        //signedit copy
        ArgumentTree copy = multiLiteralArgument(Commands.SIGNEDIT_COPY_LABEL, Commands.SIGNEDIT_COPY_ALIASES) // SIGNEDIT_COPY_ALIASES
                .withPermission(Commands.SIGNEDIT_COPY_PERMISSION.asPermission())
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        String[] signLines = sign.getLines();
                        for (int i = 0, signLinesLength = signLines.length; i < signLinesLength; i++) {
                            setLine(player, i, signLines[i]);
                        }
                        player.sendMessage(Lang.SIGNEDIT_SIGN_COPIED.getString());
                    }
                });
        //signedit copy <line>
        copy.then(new IntegerArgument("1-4", 1, 4)
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        for (int i = 0; i < 4; i++) {
                            setLine(player, i, sign.getLine(i));
                        }
                        player.sendMessage(Lang.SIGNEDIT_SIGN_COPIED.getString());
                    }
                }));
        //signedit paste
        ArgumentTree paste = multiLiteralArgument(Commands.SIGNEDIT_PASTE_LABEL, Commands.SIGNEDIT_PASTE_ALIASES) // SIGNEDIT_PASTE_LABEL
                .withPermission(Commands.SIGNEDIT_PASTE_PERMISSION.asPermission())
                .executesPlayer((player, args) -> {
                    Sign sign = canEdit(player);
                    if (sign != null) {
                        List<String> lines = getLines(player);
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            setLine(player, sign, i, line);
                        }
                    }
                });
        //signedit paste <line> <text>
        paste.then(new IntegerArgument("1-44", 1, 4)
                .then(new IntegerArgument("1-4", 1, 4)
                        .executesPlayer((player, args) -> {
                            Sign sign = canEdit(player);
                            if (sign != null) {
                                int i = (int) args[1] - 1;
                                List<String> lines = getLines(player);
                                setLine(player, sign, i, lines.get(i));
                            }
                        })));
        new Command(Commands.SIGNEDIT_LABEL)
                .withAliases(Commands.SIGNEDIT_ALIASES)
                .withPermission(Commands.SIGNEDIT_PERMISSION)
                .then(set)
                .then(clear)
                .then(copy)
                .then(paste)
                .override();
    }

    private void setLine(CommandSender sender, Sign sign, int line, String text) {
        sign.setLine(line, ChatUtils.toMineHex(text));
        sign.update(true);
        sender.sendMessage(Lang.SIGNEDIT_SIGN_UPDATED.getString());
    }

    private final Argument<BaseComponent[]> signTextArg = (Argument<BaseComponent[]>) new ChatArgument(Lang.NODES_MESSAGE.getString())
            .withPreview((PreviewLegacy) info -> ChatUtils.toComponents(ChatUtils.toMineHex(info.input())))
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
                    int line = (int) args[1] - 1;
                    String text = BaseComponent.toPlainText((BaseComponent[]) args[2]);
                    setLine(player, sign, line, text);
                }
            });

    //checks if player can edit sign, and break it
    private Sign canEdit(CommandSender sender) {
        Player p = (Player) sender;
        Sign sign = getTargetSign(p);
        if (sign != null && FakeBreak.canBreak(p, sign.getLocation())
                || sender.hasPermission(Commands.SIGNEDIT_BYPASS_PERMISSION.asPermission())) {
            return sign;
        } else {
            p.sendMessage(Lang.SIGNEDIT_SIGN_NOT_FOUND.getString());
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
