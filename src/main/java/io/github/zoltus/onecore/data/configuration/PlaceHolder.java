package io.github.zoltus.onecore.data.configuration;

import lombok.Getter;

//Placeholders
public enum PlaceHolder {
    PREFIX_PH("{p}"),
    PLAYER_PH("<player>"),
    PLAYER2_PH("<player2>"),
    AMOUNT_PH("<amount>"),
    BALANCE_PH("<balance>"),
    HOME_PH("<home>"),
    TOGGLE_PH("<toggle>"),
    SECONDS_PH("<seconds>"),
    MODE_PH("<mode>"),
    LIST_PH("<list>"),
    TYPE_PH("<type>"),
    RADIUS_PH("<radius>"),
    MESSAGE_PH("<message>"),
    PING_PH("<ping>"),
    TIME_PH("<time>"),
    SLOT_PH("<slot>"),
    WARP_PH("<warp>"),
    WORLD_PH("<world>"),
    WEATHER_PH("<weather>"),
    LINE_PH("<line>"),
    PERM_PH("<perm>");

    @Getter
    private final String placeholder;

    PlaceHolder(String placeholder) {
        this.placeholder = placeholder;
    }
}
