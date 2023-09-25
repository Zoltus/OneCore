package io.github.zoltus.onecore.data.database;

public record DBCredentials(String dbName, String username, String password, String address, int port) {
}
