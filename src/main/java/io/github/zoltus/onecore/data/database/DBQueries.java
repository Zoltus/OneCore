package io.github.zoltus.onecore.data.database;

public record DBQueries(String createTables,
                        String insertHomes,
                        String insertPlayers,
                        String insertBalances,
                        String selectHomes,
                        String selectPlayersAndBalances) {
}
