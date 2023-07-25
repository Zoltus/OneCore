package io.github.zoltus.onecore.data.configuration.yamls;

import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.listeners.ChatListener;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.utils.ChatUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
public enum Lang implements IConfig {
    VARIABLE_COLOR("variable-color"),
    BACK_NO_HISTORY("back.no-history"),
    BACK_TARGET_SENT("back.target-sent"),
    BROADCAST_PREFIX("broadcast.prefix"),
    CLEAR_PLAYER_CLEARED_CHAT("clear.player-cleared-chat"),
    DELHOME_DELETED("homes.delete.deleted"),
    DELHOME_OTHER("homes.delete.other"),
    DELWARP_DELETED("delwarp.warp-deleted"),
    SETHOME_OTHER("homes.set.other"),
    ECONOMY_BALANCE_TARGETS_BALANCE("economy.balance.targets-balance"),
    ECONOMY_BALANCE_YOUR_BALANCE("economy.balance.your-balance"),
    ECONOMY_BALTOP_EMPTY("economy.baltop.empty"),
    ECONOMY_BALTOP_LINE("economy.baltop.line"),
    ECONOMY_BALTOP_TOP_PLAYERS("economy.baltop.top-players"),
    ECONOMY_GIVE_GAVE("economy.give.gave"),
    ECONOMY_GIVE_YOUR_BALANCE_WAS_INCREACED("economy.give.your-balance-was-increaced"),
    ECONOMY_NOT_ENOUGHT("economy.not-enought"),
    ECONOMY_PAY_RECEIVED("economy.pay.received"),
    ECONOMY_PAY_SENT("economy.pay.sent"),
    ECONOMY_SET_SET("economy.set.set"),
    ECONOMY_SET_YOUR_BALANCE_WAS_SET("economy.set.your-balance-was-set"),
    ECONOMY_TAKE_TOOK("economy.take.took"),
    ECONOMY_TAKE_YOUR_BALANCE_REDUCED("economy.take.your-balance-reduced"),
    ECONOMY_TARGET_DOESNT_HAVE_ENOUGHT_MONEY("economy.target-doesnt-have-enought-money"),
    ECONOMY_TRANSFER_TRANSFERED("economy.transfer.transfered"),
    FEED_YOU_FED_TARGET("feed.you-fed-target"),
    FEED_YOU_HAVE_BEEN_HEALED("feed.you-have-been-healed"),
    FLY_YOUR_FLIGHT_IS_NOW("fly.your-flight-is-now"),
    FLY_YOU_SWITCHED_TARGET("fly.you-switched-target"),
    GAMEMODE_SURVIVAL("gamemode.survival"),
    GAMEMODE_CREATIVE("gamemode.creative"),
    GAMEMODE_SPECTATOR("gamemode.spectator"),
    GAMEMODE_ADVENTURE("gamemode.adventure"),
    GAMEMODE_CHANGED("gamemode.changed"),
    GAMEMODE_INVALID_GAMEMODE("gamemode.invalid-gamemode"),
    GAMEMODE_TARGETS_GAMEMODE_CHANGED("gamemode.targets-gamemode-changed"),
    GAMEMODE_TARGET_ALREADY_IN_GAMEMODE("gamemode.target-already-in-gamemode"),
    GOD_OTHER("god.other"),
    GOD_SELF("god.self"),
    GOD_ACTION_BAR("god.actionbar"),
    HEAL_YOU_GOT_HEALED("heal.you-got-healed"),
    HEAL_YOU_HEALED_TARGET("heal.you-healed-target"),
    HOME_LIST("homes.home.list"),
    HOME_TELEPORT_OTHERS("homes.home.teleport-other"),
    INVALID_RANGE("other.invalid-range"),
    JOINED("other.joined"),
    KICKED_FOR_SPAMMING("other.kicked-for-spamming-reason"),
    KILLALL_REMOVED_ENTITYS("killall.removed-entitys"),
    MSG_RECEIVED_MSG("msg.received-msg"),
    MSG_SENT_MSG("msg.sent-msg"),
    NODES_AMOUNT("other.nodes.amount"),
    NODES_ENTITY_TYPE("other.nodes.entity-type"),
    NODES_GAMEMODE("other.nodes.gamemode"),
    NODES_HOME_NAME("other.nodes.home-name"),
    NODES_HOME_NAME_OR_PLAYER("other.nodes.home-home/player-name"),
    NODES_LOCATION("other.nodes.location"),
    NODES_MESSAGE("other.nodes.message"),
    NODES_PLAYER("other.nodes.player"),
    NODES_RANGE("other.nodes.range"),
    NODES_SLOT("other.nodes.slot"),
    NODES_SPEED("other.nodes.speed"),
    NODES_TIME("other.nodes.time"),
    NODES_TRUE_FALSE("other.nodes.true-false"),
    NODES_WARP_NAME("other.nodes.warp-name"),
    NODES_WEATHER("other.nodes.weather"),
    NODES_WORLD_NAME("other.nodes.world-name"),
    NO_PERMISSION("other.no-permission"),
    PING_TARGETS_PING("ping.targets-ping"),
    PING_YOUR_PING("ping.your-ping"),
    PLAYER_NEVER_VISITED_SERVER("other.player-never-visited-server"),
    PWEATHER_CHANGED("pweather.changed"),
    PWEATHER_OTHER_CHANGED("pweather.changed-other"),
    PLAYTIME_TARGETS_PLAYTIME("playtime.targets-playtime"),
    PLAYTIME_TIME_FORMAT("playtime.time-format"),
    PLAYTIME_YOUR_PLAYTIME("playtime.your-playtime"),
    QUIT("other.quit"),
    RELOAD_RELOADED("reload.reloaded"),
    REPAIR_NOTHING_TO_FIX("repair.nothing-to-fix"),
    REPAIR_SELF_REPAIRED("repair.self-repaired"),
    REPAIR_SLOT_ALL("repair.slot-all"),
    REPAIR_SLOT_ARMOR("repair.slot-armor"),
    REPAIR_SLOT_HAND("repair.slot-hand"),
    REPAIR_SLOT_INVALID_SLOT("repair.slot-invalid-slot"),
    REPAIR_SLOT_INVENTORY("repair.slot-inventory"),
    REPAIR_SLOT_OFFHAND("repair.slot-offhand"),
    REPAIR_YOUR_ITEMS_GOT_REPAIRED("repair.your-items-got-repaired"),
    REPAIR_YOU_REPAIRED_TARGET("repair.you-repaired-target"),
    SEEN_DATE_FORMAT("seen.date-format"),
    SEEN_LAST_SEEN("seen.last-seen"),
    SETHOME_FULL_HOMES("homes.set.full-homes"),
    SETHOME_SET("homes.set.set"),
    SETMAXPLAYERS_SET("setmaxplayers.set"),
    SETSPAWN_SET("setspawn.set"),
    SETWARP_SET("setwarp.set"),
    SIGNEDIT_SIGN_NOT_FOUND("signedit.sign-not-found"),
    SIGNEDIT_SIGN_UPDATED("signedit.sign-updated"),
    SIGNEDIT_SIGN_COPIED("signedit.sign-copied"),
    SIGNEDIT_SIGN_COPIED_LINE("signedit.sign-copied-line"),
    SPAWN_IS_NOT_SET("spawn.is-not-set"),
    SPAWN_TARGET_SENT("spawn.target-sent"),
    SPEED_MODE_FLY("speed.mode-fly"),
    SPEED_MODE_INVALID_MODE("speed.mode-invalid-mode"),
    SPEED_MODE_INVALID_SPEED("speed.mode-invalid-speed"),
    SPEED_MODE_WALK("speed.mode-walk"),
    SPEED_YOUR_SPEED_SET("speed.your-speed-set"),
    SPEED_YOU_SET_SPEED("speed.you-set-speed"),
    SYSTEM_DISK_USAGE("system.disk-usage"),
    SYSTEM_JAVA_VERSION("system.java-version"),
    SYSTEM_OS("system.os"),
    SYSTEM_PROCESSORS("system.processors"),
    SYSTEM_RAM_USAGE("system.ram-usage"),
    SYSTEM_SERVER_VERSION("system.server-version"),
    SYSTEM_USERNAME("system.username"),
    SYSTEM_VERSION("system.version"),
    TIME_CHANGED("time.changed"),
    TIME_INVALID_TIME("time.invalid-time"),
    PTIME_CHANGED("ptime.changed"),
    PTIME_CHANGED_OTHER("ptime.changed-other"),
    TOP_TELEPORTED_TARGET("top.teleported-target"),
    TOP_TELPORTED("top.teleported"),
    TPHERE_TELEPORTED("tphere.teleported"),
    TPHERE_OFFLINE_TARGET("tphere.offline-target"),
    TP_ACCEPTED("tp.accepted"),
    TP_CANCELLED_BY_DAMAGE("tp.cancelled-by-damage"),
    TP_CANCELLED_BY_NEW_TELE("tp.cancelled-by-new-teleport"),
    TP_CANCELLED_BY_MOVEMENT("tp.cancelled-by-movement"),
    TP_CANT_SELF_TELEPORT("tp.cant-self-teleport"),
    TP_DENIED("tp.denied"),
    TP_EXPIRED("tp.expired"),
    TP_NO_REQUESTS("tp.no-requests"),
    TP_NO_SAFE_LOCATIONS("tp.no-safe-locations"),
    TP_TELEPORTED_OFFLINE_TARGET("tp.teleported-offline-target"),
    TP_TELEPORTED_OFFLINE_TARGETS("tp.teleported-offline-targets"),
    TP_TELEPORTED_TARGETS("tp.teleported-targets"),
    TP_TELEPORTED_TARGET("tp.teleported-target"),
    TP_RECEIVED("tp.received"),
    TP_SENT("tp.sent"),
    TP_STARTED("tp.started"),
    TP_TARGET_QUIT("tp.target-quit"),
    TP_TOGGLE_IS_OFF("tp.toggle-is-off"),
    TP_TOGGLE_SWITCHED("tp.toggle-switched"),
    TP_YOU_ACCEPTED("tp.you-accepted"),
    TP_YOU_ALREADY_SENT_REQUEST("tp.you-already-sent-request"),
    TP_YOU_DENIED("tp.you-denied"),
    VANISH_SELF("vanish.self"),
    VANISH_OTHER("vanish.other"),
    VANISH_VISIBLE("vanish.visible"),
    VANISH_INVISIBLE("vanish.invisible"),
    VANISH_INVISIBLE_ACTION_BAR("vanish.invisible-actionbar"),
    WARP_LIST("warp.list"),
    WARP_NOT_FOUND("warp.not-found"),
    WARP_TARGET_SENT("warp.target-sent"),
    WEATHER_CHANGED("weather.changed"),
    WEATHER_INVALID_WEATHER("weather.invalid-weather"),
    WORLD_NOT_FOUND("other.world-not-found");

    @Getter
    final String path;

    public OneYml yml() {
        return Yamls.LANG.getYml();
    }

    public void send(CommandSender sender, Object... replaces) {
        String replaced = replace(replaces);
        ChatUtils.mmSend(sender, replaced);
    }

    public void send(User user, Object... replaces) {
        if (user.isOnline()) {
            send(user.getPlayer(), replaces);
        }
    }

    public String replaceColored(Object... replaces) {
        return ChatListener.translateColors(replace(replaces));
    }

    public String replace(Object... replaces) {
        String ph = null;
        String message = getString();
        for (Object objRp : replaces) {
            String value = objRp instanceof IConfig config ? config.getString() : String.valueOf(objRp);
            //color
            if (ph == null) {
                ph = value;
            } else {
                //  ph              = <balance>
                //  value           = 100
                //  variable-color  =
                // <color:#ff6666><variable></color>
                String variableColors = VARIABLE_COLOR.get();
                //<color:#ff6666><balance></color>
                String coloredVar = variableColors.replace("<variable>", ph);
                // '{p} Rahasi: <balance>.'
                message = message.replaceAll(ph, coloredVar);
                // '{p} Rahasi: <color:#ff6666><balance></color>.'
                message = message.replaceAll(ph, value);
                // '{p} Rahasi: <color:#ff6666>value</color>.'
                ph = null;
            }
        }
        return message;
    }
}
