package io.github.zoltus.onecore.player.command.arguments;

import io.github.zoltus.onecore.player.command.commands.WeatherMode;

//Used to return the weather mode and the input used
public record WeatherInput(String input, WeatherMode weather) {
}

