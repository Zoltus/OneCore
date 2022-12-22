package io.github.zoltus.onecore.data.configuration.yamls;

import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.data.configuration.Yamls;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Config implements IConfig {
    BACK_HISTORY_SIZE("back-history-size"),
    BACKUPS_STATS_ENABLED("backups.stats-enabled"),
    BACKUPS_DATABASE_ENABLED("backups.database-enabled"),
    BACKUPS_PLAYERDATA_ENABLED("backups.playerdata-enabled"),
    BALTOP_UPDATE_INTERVAL("baltop-update-interval"),
    CHAT_COLOR_PERMISSION("chat-color-permission"),
    CHAT_COLORS_ENABLED("chat-colors-enabled"),
    CURRENCY_PLURAL("economy.currency-plural"),
    CURRENCY_SINGULAR("economy.currency-singular"),
    DATA_SAVE_INTERVAL("data-save-interval-minutes"),
    ECONOMY("economy.enabled"),
    ECONOMY_USE_ONEECONOMY("economy.use-oneeconomy"),
    KICKED_FOR_SPAMMING_BYPASS("kicked-for-spamming-bypass"),
    MENTIONS_ENABLED("mentions-enabled"),
    PERMISSION_PREFIX("permission-prefix"),
    PREFIX("prefix"),
    RTP_QUEUE_TICK("rtp.queue-tick:"),
    RTP_DELAY_BETWEEN_LOCATION_SCAN("rtp.delay-between-location-scan"),
    RTP_SCANS_TILL_TIMEOUT("rtp.scans-till-time-out"),
    RTP_RADIUS("rtp.radius"),
    SIGN_COLOR_PERMISSION("sign-color-permission"),
    SIGN_SHIFT_EDIT_ENABLED("sign-shift-edit-enabled"),
    SIGN_SHIFT_EDIT_PERMISSION("sign-shift-edit-permission"),
    START_MONEY("economy.start-money"),
    TELEPORT_DELAY("teleport-delay"),
    TELEPORT_EXPIRE("teleport-expire"),
    TELEPORT_VELOCITY_RESET("teleport-velocity-reset"),
    TELEPORT_WITH_VEHICLE("teleport-with-vehicle"),
    USER_CONSOLE_FILTER("user-console-filter");

    @Getter
    final String path;

    public OneYml yml() {
        return Yamls.CONFIG.getYml();
    }
}
