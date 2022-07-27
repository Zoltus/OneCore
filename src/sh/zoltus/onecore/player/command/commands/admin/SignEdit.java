package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.listeners.SignColorHandler;
import sh.zoltus.onecore.player.command.IOneCommand;

import java.util.List;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;

public class SignEdit implements IOneCommand {

    @Override
    public void init() {
        //signedit set <line> <text>
        //test signedit <line> <text>
        command(SIGNEDIT_LABEL)
                .withPermission(SIGNEDIT_PERMISSION)
                .withAliases(SIGNEDIT_ALIASES)
                .withArguments(new IntegerArgument("", 0, 3), stringArg)
                .executesPlayer((player, args) -> {
                    int line = (int) args[0];
                    String text =  (String) args[1];
                    Sign sign = getTargetSign(player);
                    sign.setLine(line, SignColorHandler.toMineHex(text));
                    sign.update(true);
                    //todo hex support
                }).register();

        //signedit clear <line>
        //signedit copy
        //signedit copy <line>
        //signedit paste <line>
    }
//
    private final Argument<String> stringArg = new GreedyStringArgument("text")
            .replaceSuggestions(ArgumentSuggestions.strings(info -> {
                Sign sign = getTargetSign((Player) info.sender());
                Integer line = (Integer) info.previousArgs()[0];
                if (sign.getLines().length != 0) {
                    return List.of(SignColorHandler.toNormal(sign.getLine(line))).toArray(new String[0]);
                } else {
                    return new String[0];
                }
            }));


    private Sign getTargetSign(Player p) {
        return (Sign) p.getWorld().getBlockAt(227,253,654).getState();
    }
}
