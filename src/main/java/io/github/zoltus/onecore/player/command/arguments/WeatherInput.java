package io.github.zoltus.onecore.player.command.arguments;

import io.github.zoltus.onecore.player.command.commands.WeatherMode;

//Used to return the weather mode and the alias used
public record WeatherInput(String alias, WeatherMode weather) {
}

