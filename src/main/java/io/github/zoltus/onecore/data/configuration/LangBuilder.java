package io.github.zoltus.onecore.data.configuration;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.User;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LangBuilder {

    private String baseLangEntry;
    private final Map<PlaceHolder, Object> replacements = new HashMap<>();

    private static final OneCore plugin = OneCore.getPlugin();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer lcs = LegacyComponentSerializer.builder()
            .character('&') // Use the section symbol (§)
            // .formats(LEGACY_COLOR) //todo ?
            .useUnusualXRepeatedCharacterHexFormat()  // Enable BungeeCord's §x§R§R§G§G§B§B format //todo test
            .hexColors()    // Enable serialization of hex colors in the #RRGGBB format // todo test
            .build();

    //todo combine a bit
    public LangBuilder(IConfig baseLangEntry) {
        this.baseLangEntry = baseLangEntry.get();
        replacements.put(PlaceHolder.PREFIX_PH, Config.PREFIX.get());
    }

    public LangBuilder(String baseLangEntry) {
        this.baseLangEntry = baseLangEntry;
        replacements.put(PlaceHolder.PREFIX_PH, Config.PREFIX.get());
    }

    public LangBuilder rb(@NotNull PlaceHolder placeholder, @Nullable Object value) {
        replacements.put(placeholder, value);
        return this;
    }

    @NotNull
    public String buildString() {
        String colorTemplate = Lang.VARIABLE_COLOR.get();
        StringBuilder sb = new StringBuilder(baseLangEntry);
        // Always replace {p} with the prefix

        for (Map.Entry<PlaceHolder, Object> entry : replacements.entrySet()) {
            String placeholder = entry.getKey().getPlaceholder();
            Object value = entry.getValue();
            String valueStr = (value instanceof IConfig config) ? config.get() : Objects.toString(value, "");
            // Create the final colored replacement value string
            String coloredValue = colorTemplate.replace("<variable>", valueStr);
            // Replace placeholders
            int index = sb.indexOf(placeholder);
            while (index != -1) {
                sb.replace(index, index + placeholder.length(), coloredValue);
                index = sb.indexOf(placeholder, index + coloredValue.length());
            }
        }
        return sb.toString();
    }

    public void send(CommandSender... receivers) {
        Component builtComponent = buildComponent();
        BukkitAudiences adventure = plugin.adventure();

        for (CommandSender receiver : receivers) {
            Audience audience = adventure.sender(receiver);
            audience.sendMessage(builtComponent);
        }
    }

    public void send(@NotNull User user) {
        if (user.isOnline()) {
            send(user.getPlayer());
        }
    }

    public String buildLegacyString() {
        baseLangEntry = baseLangEntry.replaceAll("§", "&");
        Component component = buildComponent();
        String serialize = lcs.serialize(component);
        return ChatColor.translateAlternateColorCodes('&', serialize);
    }

    public Component buildComponent() {
        TextComponent deserialize = lcs.deserialize(buildString());
        String format = mm.serialize(deserialize);
        String replace = format.replace("\\<", "<"); //Removes escapes \
        replace = replace.replace("§f", ""); // replace random &f which minimessage adds for no reason
        return mm.deserialize(replace);
    }
}