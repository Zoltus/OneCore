package sh.zoltus.onecore.data.configuration.yamls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sh.zoltus.onecore.data.configuration.IConfig;
import sh.zoltus.onecore.data.configuration.OneYml;
import sh.zoltus.onecore.data.configuration.Yamls;

@AllArgsConstructor
public enum Config implements IConfig {
    BACK_HISTORY_SIZE("back-history-size"),
    BALTOP_UPDATE_INTERVAL("baltop-update-interval"),
    CHAT_COLOR_PERMISSION("chat-color-permission"),
    CHAT_COLORS_ENABLED("chat-colors-enabled"),
    CURRENCY_PLURAL("economy.currency-plural"),
    CURRENCY_SINGULAR("economy.currency-singular"),
    DATABASE_NAME("database-name"),
    DATA_SAVE_INTERVAL("data-save-interval-minutes"),
    ECONOMY("economy.enabled"),
    ECONOMY_USE_ONEECONOMY("economy.use-oneeconomy"),
    KEEP_USERS_IN_CACHE("keep-users-in-cache"),
    KICKED_FOR_SPAMMING_BYPASS("kicked-for-spamming-bypass"),
    MENTIONS_ENABLED("mentions-enabled"),
    PERMISSION_PREFIX("permission-prefix"),
    PREFIX("prefix"),
    RTP_COOLDOWN_SECONDS("rtp-cooldown-seconds"),
    RTP_RADIUS("rtp-radius"),
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
