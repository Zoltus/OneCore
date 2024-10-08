package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;

import java.util.stream.Stream;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Reload implements ICommand {

    @Override
    public void init() {
        //onecore reload
        new Command("onecore")
                .withAliases("oc")
                .then(multiLiteralArgument(RELOAD_LABEL, RELOAD_ALIASES)
                        .withPermission(RELOAD_PERMISSION.asPermission())
                        .executes((sender, args) -> {
                            //Reloads all ymls
                            Stream.of(Yamls.values()).forEach(yaml -> yaml.getYml().reload());
                            //Sets all data to enums
                            RELOAD_RELOADED.send(sender);
                        }))
                .override();
    }
}
