package io.github.zoltus.onecore.data.configuration.yamls;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.player.User;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class LangBuilder {

    private final IConfig baseLangEntry;
    private final Map<String, Object> replacements = new HashMap<>();

    private static final OneCore plugin = OneCore.getPlugin();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer lcs = LegacyComponentSerializer.legacySection();

    public LangBuilder rb(@NotNull IConfig placeholderKey, @Nullable Object value) {
        String placeholder = placeholderKey.get();
        replacements.put(placeholder, value);
        return this;
    }

    public LangBuilder rb(@NotNull String placeholder, @Nullable Object value) {
        if (!placeholder.isEmpty()) {
            replacements.put(placeholder, value);
        }
        return this;
    }

    @NotNull
    public String buildString() {
        String template = baseLangEntry.get();
        String colorTemplate = Lang.VARIABLE_COLOR.get();
        StringBuilder sb = new StringBuilder(template);
        // Always replace {p} with the prefix
        replacements.put("{p}", Config.PREFIX.get());

        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            String placeholder = entry.getKey();
            Object value = entry.getValue();
            String valueStr = (value instanceof IConfig config) ? config.get() : Objects.toString(value, "");
            // Create the final colored replacement value string
            String coloredValue = colorTemplate.replace("<variable>", valueStr);
            // Replace ALL occurrences of the placeholder
            int index = sb.indexOf(placeholder);
            while (index != -1) {
                sb.replace(index, index + placeholder.length(), coloredValue);
                index = sb.indexOf(placeholder, index + coloredValue.length());
            }
        }
        return sb.toString();
    }

    public void send(@NotNull User user) {
        if (user.isOnline()) {
            send(user.getPlayer());
        }
    }

    public void send(CommandSender sender) {
        Component deserialized = mm.deserialize(buildString());
        BukkitAudiences adventure = plugin.adventure();
        Audience audience = adventure.sender(sender);
        audience.sendMessage(deserialized);
    }

    public String buildLegacyString() {
        Component deserialized = mm.deserialize(buildString());
        return lcs.serialize(deserialized);
    }
}