package sh.zoltus.onecore.configuration.yamls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sh.zoltus.onecore.configuration.IConfig;
import sh.zoltus.onecore.configuration.OneYml;
import sh.zoltus.onecore.configuration.Yamls;

@AllArgsConstructor
public enum Commands implements IConfig {
    BACK_ALIASES("back.aliases"),
    BACK_ENABLED("back.enabled"),
    BACK_LABEL("back.label"),
    BACK_PERMISSION("back.permission"),
    BROADCAST_ALIASES("broadcast.aliases"),
    BROADCAST_ENABLED("broadcast.enabled"),
    BROADCAST_LABEL("broadcast.label"),
    BROADCAST_PERMISSION("broadcast.permission"),
    CLEARCHAT_ALIASES("clearchat.aliases"),
    CLEARCHAT_ENABLED("clearchat.enabled"),
    CLEARCHAT_LABEL("clearchat.label"),
    CLEARCHAT_PERMISSION("clearchat.permission"),
    DELHOME_ALIASES("delhome.aliases"),
    DELHOME_ENABLED("delhome.enabled"),
    DELHOME_LABEL("delhome.label"),
    DELHOME_PERMISSION("delhome.permission"),
    ECONOMY_ALIASES("economy.aliases"),
    ECONOMY_BALANCE_ALIASES("economy.balance.aliases"),
    ECONOMY_BALANCE_LABEL("economy.balance.label"),
    ECONOMY_BALANCE_PERMISSION("economy.balance.permission"),
    ECONOMY_BALTOP_ALIASES("economy.baltop.aliases"),
    ECONOMY_BALTOP_LABEL("economy.baltop.label"),
    ECONOMY_BALTOP_PERMISSION("economy.baltop.permission"),
    ECONOMY_ENABLED("economy.enabled"),
    ECONOMY_GIVE_ALIASES("economy.give.aliases"),
    ECONOMY_GIVE_LABEL("economy.give.label"),
    ECONOMY_GIVE_PERMISSION("economy.give.permission"),
    ECONOMY_LABEL("economy.label"),
    ECONOMY_PAY_ALIASES("economy.pay.aliases"),
    ECONOMY_PAY_LABEL("economy.pay.label"),
    ECONOMY_PAY_PERMISSION("economy.pay.permission"),
    ECONOMY_PERMISSION("economy.permission"),
    ECONOMY_SET_ALIASES("economy.set.aliases"),
    ECONOMY_SET_LABEL("economy.set.label"),
    ECONOMY_SET_PERMISSION("economy.set.permission"),
    ECONOMY_TAKE_ALIASES("economy.take.aliases"),
    ECONOMY_TAKE_LABEL("economy.take.label"),
    ECONOMY_TAKE_PERMISSION("economy.take.permission"),
    ECONOMY_TRANSFER_ALIASES("economy.transfer.aliases"),
    ECONOMY_TRANSFER_LABEL("economy.transfer.label"),
    ECONOMY_TRANSFER_PERMISSION("economy.transfer.permission"),
    EnderChest_ALIASES("enderchest.aliases"),
    EnderChest_EDIT_PERMISSION("enderchest.edit-permission"),
    EnderChest_ENABLED("enderchest.enabled"),
    EnderChest_LABEL("enderchest.label"),
    EnderChest_OTHER_PERMISSION("enderchest.other-permission"),
    EnderChest_PERMISSION("enderchest.permission"),
    FEED_ALIASES("feed.aliases"),
    FEED_ENABLED("feed.enabled"),
    FEED_LABEL("feed.label"),
    FEED_PERMISSION("feed.permission"),
    FEED_PERMISSION_OTHER("feed.permission-other"),
    FLY_ALIASES("fly.aliases"),
    FLY_ENABLED("fly.enabled"),
    FLY_LABEL("fly.label"),
    FLY_PERMISSION("fly.permission"),
    FLY_PERMISSION_OTHER("fly.permission-other"),
    GAMEMODE_ALIASES("gamemode.aliases"),
    GAMEMODE_ALIASES_ADVENTURE("gamemode.aliases-adventure"),
    GAMEMODE_ALIASES_CREATIVE("gamemode.aliases-creative"),
    GAMEMODE_ALIASES_SPECTATOR("gamemode.aliases-spectator"),
    GAMEMODE_ALIASES_SURVIVAL("gamemode.aliases-survival"),
    GAMEMODE_ENABLED("gamemode.enabled"),
    GAMEMODE_LABEL("gamemode.label"),
    GAMEMODE_OTHER_PERMISSION("gamemode.other-permission"),
    GAMEMODE_PERMISSION("gamemode.permission"),
    GAMEMODE_MODE_PERMISSION("gamemode.mode-permission"),
    GAMEMODE_SUGGESTIONS("gamemode.suggestions"),
    GOD_ALIASES("god.aliases"),
    GOD_ENABLED("god.enabled"),
    GOD_LABEL("god.label"),
    GOD_PERMISSION("god.permission"),
    GOD_PERMISSION_OTHER("god.permission-other"),
    HEAL_ALIASES("heal.aliases"),
    HEAL_ENABLED("heal.enabled"),
    HEAL_LABEL("heal.label"),
    HEAL_PERMISSION("heal.permission"),
    HEAL_PERMISSION_OTHER("heal.permission-other"),
    HOME_ALIASES("home.aliases"),
    HOME_AMOUNT_PERMISSION("home.permission-amount"),
    HOME_DEFAULT_NAME("home.default-name"),
    HOME_ENABLED("home.enabled"),
    HOME_LABEL("home.label"),
    HOME_PERMISSION("home.permission"),
    HOME_PERMISSION_OTHER("home.permission-other"),
    INVSEE_ALIASES("invsee.aliases"),
    INVSEE_EDIT_PERMISSION("invsee.edit-permission"),
    INVSEE_ENABLED("invsee.enabled"),
    INVSEE_LABEL("invsee.label"),
    INVSEE_PERMISSION("invsee.permission"),
    KILLALL_ALIASES("killall.aliases"),
    KILLALL_ENABLED("killall.enabled"),
    KILLALL_LABEL("killall.label"),
    KILLALL_PERMISSION("killall.permission"),
    MSG_ALIASES("msg.aliases"),
    MSG_ENABLED("msg.enabled"),
    MSG_LABEL("msg.label"),
    MSG_PERMISSION("msg.permission"),
    PING_ALIASES("ping.aliases"),
    PING_ENABLED("ping.enabled"),
    PING_LABEL("ping.label"),
    PING_OTHER_PERMISSION("ping.other-permission"),
    PING_PERMISSION("ping.permission"),
    PLAYTIME_ALIASES("playtime.aliases"),
    PLAYTIME_ENABLED("playtime.enabled"),
    PLAYTIME_LABEL("playtime.label"),
    PLAYTIME_OTHER_PERMISSION("playtime.other-permission"),
    PLAYTIME_PERMISSION("playtime.permission"),
    RELOAD_ALIASES("reload.aliases"),
    RELOAD_ENABLED("reload.enabled"),
    RELOAD_LABEL("reload.label"),
    RELOAD_PERMISSION("reload.permission"),
    REPAIR_ALIASES("repair.aliases"),
    REPAIR_ENABLED("repair.enabled"),
    REPAIR_LABEL("repair.label"),
    REPAIR_PERMISSION("repair.permission"),
    RTP_ALIASES("rtp.aliases"),
    RTP_ENABLED("rtp.enabled"),
    RTP_LABEL("rtp.label"),
    RTP_PERMISSION("rtp.permission"),
    SEEN_ALIASES("seen.aliases"),
    SEEN_ENABLED("seen.enabled"),
    SEEN_LABEL("seen.label"),
    SEEN_PERMISSION("seen.permission"),
    SETHOME_ALIASES("sethome.aliases"),
    SETHOME_ENABLED("sethome.enabled"),
    SETHOME_LABEL("sethome.label"),
    SETHOME_PERMISSION("sethome.permission"),
    SETHOME_PERMISSION_OTHER("sethome.other-permission"),
    SETMAXPLAYERS_ALIASES("setmaxplayers.aliases"),
    SETMAXPLAYERS_ENABLED("setmaxplayers.enabled"),
    SETMAXPLAYERS_LABEL("setmaxplayers.label"),
    SETMAXPLAYERS_PERMISSION("setmaxplayers.permission"),
    SETSPAWN_ALIASES("setspawn.aliases"),
    SETSPAWN_ENABLED("setspawn.enabled"),
    SETSPAWN_LABEL("setspawn.label"),
    SETSPAWN_PERMISSION("setspawn.permission"),
    SETWARP_ALIASES("setwarp.aliases"),
    SETWARP_ENABLED("setwarp.enabled"),
    SETWARP_LABEL("setwarp.label"),
    SETWARP_PERMISSION("setwarp.permission"),
    SIGNEDIT_ALIASES("signedit.aliases"),
    SIGNEDIT_ENABLED("signedit.enabled"),
    SIGNEDIT_LABEL("signedit.label"),
    SIGNEDIT_PERMISSION("signedit.permission"),
    SPAWN_ALIASES("spawn.aliases"),
    SPAWN_ENABLED("spawn.enabled"),
    SPAWN_LABEL("spawn.label"),
    SPAWN_PERMISSION("spawn.permission"),
    SPAWN_PERMISSION_OTHER("spawn.permission-other"),
    SPEED_ALIASES("speed.aliases"),
    SPEED_ENABLED("speed.enabled"),
    SPEED_FLY_ALIASES("speed.fly-aliases"),
    SPEED_WALK_ALIASES("speed.walk-aliases"),
    SPEED_LABEL("speed.label"),
    SPEED_PERMISSION("speed.permission"),
    SPEED_PERMISSION_OTHER("speed.permission-other"),
    SYSTEM_ALIASES("system.aliases"),
    SYSTEM_ENABLED("system.enabled"),
    SYSTEM_LABEL("system.label"),
    SYSTEM_PERMISSION("system.permission"),
    TIME_AFTERNOON_ALIASES("time.afternoon-aliases"),
    TIME_ALIASES("time.aliases"),
    TIME_DAY_ALIASES("time.day-aliases"),
    TIME_ENABLED("time.enabled"),
    TIME_LABEL("time.label"),
    TIME_MORNING_ALIASES("time.morning-aliases"),
    TIME_NIGHT_ALIASES("time.night-aliases"),
    TIME_PERMISSION("time.permission"),
    TIME_SINGLE_WORD_CMDS("time.single-word-cmds"),
    TIME_SUGGESTIONS("time.suggestions"),
    TOP_ALIASES("top.aliases"),
    TOP_ENABLED("top.enabled"),
    TOP_LABEL("top.label"),
    TOP_OTHER_PERMISSION("top.other-permission"),
    TOP_PERMISSION("top.permission"),
    TPACCEPT_ALIASES("tpaccept.aliases"),
    TPACCEPT_ENABLED("tpaccept.enabled"),
    TPACCEPT_LABEL("tpaccept.label"),
    TPACCEPT_PERMISSION("tpaccept.permission"),
    TPAHERE_ALIASES("tpahere.aliases"),
    TPAHERE_ENABLED("tpahere.enabled"),
    TPAHERE_LABEL("tpahere.label"),
    TPAHERE_PERMISSION("tpahere.permission"),
    TPA_ALIASES("tpa.aliases"),
    TPA_ENABLED("tpa.enabled"),
    TPA_LABEL("tpa.label"),
    TPA_PERMISSION("tpa.permission"),
    TPDENY_ALIASES("tpdeny.aliases"),
    TPDENY_ENABLED("tpdeny.enabled"),
    TPDENY_LABEL("tpdeny.label"),
    TPDENY_PERMISSION("tpdeny.permission"),
    TPHERE_ALIASES("tphere.aliases"),
    TPHERE_ENABLED("tphere.enabled"),
    TPHERE_LABEL("tphere.label"),
    TPHERE_PERMISSION("tphere.permission"),
    TPTOGGLE_ALIASES("tptoggle.aliases"),
    TPTOGGLE_ENABLED("tptoggle.enabled"),
    TPTOGGLE_LABEL("tptoggle.label"),
    TPTOGGLE_PERMISSION("tptoggle.permission"),
    TP_ALIASES("tp.aliases"),
    TP_ENABLED("tp.enabled"),
    TP_LABEL("tp.label"),
    TP_PERMISSION("tp.permission"),
    TP_PERMISSION_OTHER("tp.permission-other"),
    WARP_ALIASES("warp.aliases"),
    WARP_ENABLED("warp.enabled"),
    WARP_LABEL("warp.label"),
    WARP_PERMISSION("warp.permission"),
    WARP_PERMISSION_OTHER("warp.permission-other"),
    WEATHER_ALIASES("weather.aliases"),
    WEATHER_CLEAR_ALIASES("weather.clear-aliases"),
    WEATHER_ENABLED("weather.enabled"),
    WEATHER_LABEL("weather.label"),
    WEATHER_PERMISSION("weather.permission"),
    WEATHER_RAIN_ALIASES("weather.rain-aliases"),
    WEATHER_SINGLE_WORD_CMDS("weather.single-word-cmds"),
    WEATHER_STORM_ALIASES("weather.storm-aliases"),
    WEATHER_SUGGESTIONS("weather.suggestions"),
    WORLDMANAGER_ALIASES("worldmanager.aliases"),
    WORLDMANAGER_ENABLED("worldmanager.enabled"),
    WORLDMANAGER_LABEL("worldmanager.label"),
    WORLDMANAGER_PERMISSION("worldmanager.permission");

    @Getter
    final String path;

    public OneYml yml() {
        return Yamls.COMMANDS.getYml();
    }
}
