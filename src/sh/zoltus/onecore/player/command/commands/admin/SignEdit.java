package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.listeners.SignColorHandler;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;

import java.util.List;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_MESSAGE;

public class SignEdit implements IOneCommand {

    private void setLine(Sign sign, int line, String text) {
        sign.setLine(line, SignColorHandler.toMineHex(text));
        sign.update(true);
    }

    //todo color perms,
    //todo check if can edit sign in area/hasperms
    @Override
    public void init() {
        //signedit set <line> <text>
        ApiCommand set = command(SIGNEDIT_SET_LABEL)
                .withPermission(SIGNEDIT_SET_PERMISSION)
                .withAliases(SIGNEDIT_SET_ALIASES)
                .withArguments(new IntegerArgument("1-4", 1, 4), signTextArg)
                .executesPlayer((player, args) -> {
                    int line = (int) args[0] - 1;
                    String text = BaseComponent.toPlainText((BaseComponent[]) args[1]);
                    Sign sign = getTargetSign(player);
                    setLine(sign, line, text);
                });
        //withrequirements
        ApiCommand clear = command(SIGNEDIT_CLEAR_LABEL)
                .withPermission(SIGNEDIT_CLEAR_PERMISSION)
                .withAliases(SIGNEDIT_CLEAR_ALIASES)
                .executesPlayer((player, args) -> {
                    Sign sign = getTargetSign(player);
                    for (int i = 0; i < 4; i++) {
                        setLine(sign, i, "");
                    }
                });
        ApiCommand clearLine = command(SIGNEDIT_CLEAR_LABEL)
                .withPermission(SIGNEDIT_CLEAR_PERMISSION)
                .withAliases(SIGNEDIT_CLEAR_ALIASES)
                .withArguments(new IntegerArgument("1-4", 1, 4))
                .executesPlayer((player, args) -> {
                    int line = (int) args[0] - 1;
                    Sign sign = getTargetSign(player);
                    setLine(sign, line, "");
                });
        //ApiCommand copy = command(SIGNEDIT_LABEL);
        //ApiCommand paste = command(SIGNEDIT_LABEL);
        command(SIGNEDIT_LABEL)
                .withSubcommands(set, clear, clearLine)
                .register();


        //signedit clear
        //signedit clear <line>
        //signedit copy
        //signedit copy <line>
        //signedit paste <line>
    }

    //
    private final Argument<BaseComponent[]> signTextArg = new ChatArgument(NODES_MESSAGE.getString())
            .withPreview((PreviewLegacy) info -> {
                Bukkit.getConsoleSender().sendRawMessage(SignColorHandler.toMineHex(info.input()));
                return toComponents(SignColorHandler.toMineHex(info.input()));
            })
            .replaceSuggestions(ArgumentSuggestions.strings(info -> {
                Sign sign = getTargetSign((Player) info.sender());
                Integer line = (Integer) info.previousArgs()[0] -1;
                if (sign.getLines().length != 0) {
                    return List.of(SignColorHandler.toNormal(sign.getLine(line))).toArray(new String[0]);
                } else {
                    return new String[0];
                }
            }));


    private Sign getTargetSign(Player p) {
        return (Sign) p.getWorld().getBlockAt(227, 253, 654).getState();
    }
}
