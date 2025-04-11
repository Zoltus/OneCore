package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.player.command.IArgument;
import io.github.zoltus.onecore.player.command.commands.regular.Warp;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_WORLD_NAME;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.WARP_NOT_FOUND;

public class WarpArgument extends CustomArgument<Warp.WarpObj, String> implements IArgument {

    //todo cleanup


    public WarpArgument() {
        this("");
    }

    public WarpArgument(String add) {
        super(new StringArgument(NODES_WORLD_NAME.get() + add), info -> {
            String input = info.input();
            Location warp =  Yamls.WARPS.getYml().getLocation(input);
            if (warp == null) {
                throw CustomArgument.CustomArgumentException
                        .fromBaseComponents(TextComponent
                                .fromLegacyText(WARP_NOT_FOUND.rb(LIST_PH, Warp.getWarps()).buildString()));
            } else {
                return new Warp.WarpObj(input, warp);
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> toSuggestion(info.currentArg(), Warp.getWarps().toArray(new String[0]))));
    }

}
