package io.github.zoltus.onecore.data.configuration.yamls;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.PlaceHolder;
import io.github.zoltus.onecore.player.User;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LangBuilder {

    private final String baseLangEntry;
    private final Map<PlaceHolder, Object> replacements = new HashMap<>();

    private static final OneCore plugin = OneCore.getPlugin();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer lcs = LegacyComponentSerializer.legacySection();

    public LangBuilder(IConfig baseLangEntry) {
        this.baseLangEntry = baseLangEntry.get();
    }

    public LangBuilder(String baseLangEntry) {
        this.baseLangEntry = baseLangEntry;
    }

/*    public LangBuilder rb(@NotNull IConfig placeholderKey, @Nullable Object value) {
        String placeholder = placeholderKey.get();
        replacements.put(placeholder, value);
        return this;
    }*/

    public LangBuilder rb(@NotNull PlaceHolder placeholder, @Nullable Object value) {
        replacements.put(placeholder, value);
        return this;
    }

    @NotNull
    public String buildString() {
        String colorTemplate = Lang.VARIABLE_COLOR.get();
        StringBuilder sb = new StringBuilder(baseLangEntry);
        // Always replace {p} with the prefix
        replacements.put(PlaceHolder.PREFIX_PH, Config.PREFIX.get());

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
        String str = buildString();
        str = lcs.serialize(mm.deserialize(str.replace("§", "&")));
        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }

/*    public Component buildLegacyString2() {
        //Converts &6test &1message to <green>test <blue>message
        TextComponent deserialize = lcs.deserialize(buildString());
        String format = mm.serialize(deserialize);
        //Removes escapes \
        String replace = format.replace("\\<", "<");
        //<hover:show_text:<red>Paina hylkääksesi!><click:run_command:/tpdeny>Hylkää!</click></hover>
        return mm.deserialize(replace);
    }*/
}