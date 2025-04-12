package io.github.zoltus.onecore.data.configuration.yamls;

import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.data.configuration.Yamls;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Config implements IConfig {
    BACKUPS_STATS_ENABLED("backups.stats-enabled"),
    BACKUPS_DATABASE_ENABLED("backups.database-enabled"),
    BACKUPS_PLAYERDATA_ENABLED("backups.playerdata-enabled"),
    CHAT_FORMAT("chat.format"),
    CHAT_COLOR_PERMISSION("chat.color-permission"),
    CHAT_FORMATTER_ENABLED("chat.formatter-enabled"),
    CHAT_COLORS_ENABLED("chat.colors-enabled"),
    CHAT_REMOVE_DUPLICATE_SPACES("chat.remove-doublespaces"),
    CHAT_TRIM("chat.trim"),
    CURRENCY_PLURAL("economy.currency-plural"),
    CURRENCY_SINGULAR("economy.currency-singular"),
    DEFAULT_HOME_AMOUNT("default-home-amount"),
    DB_SAVE_INTERVAL("database.save-interval-minutes"),
    ECONOMY_HOOK("economy.enabled-hook"),
    ECONOMY_USE_ONEECONOMY("economy.use-oneeconomy"),
    KICKED_FOR_SPAMMING_BYPASS("kicked-for-spamming-bypass-permission"),
    MENTIONS_ENABLED("mentions.enabled"),
    MENTION_COLOR("mentions.color"),
    MENTION_PERMISSION("mentions.permission"),
    MENTION_EVERYONE_PERMISSION("mentions.mention-everyone-permission"),
    MENTION_SOUND("mentions.sound"),
    PERMISSION_PREFIX("permission-prefix"),
    PREFIX("prefix"),
    SIGN_COLOR_PERMISSION("sign.color-permission"),
    SIGN_SHIFT_EDIT_ENABLED("sign.shift-edit-enabled"),
    SIGN_SHIFT_EDIT_PERMISSION("sign.shift-edit-permission"),
    START_MONEY("economy.start-money"),
    TELEPORT_DELAY("teleport.delay"),
    TELEPORT_EXPIRE("teleport.expire"),
    TELEPORT_CD_BYPASS("teleport.cooldown-bypass-permission"),
    TELEPORT_VELOCITY_RESET("teleport.velocity-reset"),
    TELEPORT_WITH_VEHICLE("teleport.with-vehicle-permission"),
    TELEPORT_WITH_LEASH("teleport.with-leash-permission"),
    TELEPORT_SPAWN_ON_JOIN("teleport.force-spawn-on-join"),
    USER_CONSOLE_FILTER("user-console-filter");

    @Getter
    final String path;

    public OneYml yml() {
        return Yamls.CONFIG.getYml();
    }
}
