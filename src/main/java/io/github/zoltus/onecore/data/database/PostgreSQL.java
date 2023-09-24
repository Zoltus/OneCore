package io.github.zoltus.onecore.data.database;

import io.github.zoltus.onecore.OneCore;

public class PostgreSQL extends Database {

    public PostgreSQL(OneCore plugin) {
        super(plugin,
                String.format("jdbc:sqlite:%s/database.db", plugin.getDataFolder()),
                "createTables.sql",
                "insertHomes.sql",
                "insertPlayers.sql",
                "insertBalances.sql",
                "selectHomes.sql",
                "selectPlayersAndBalances.sql");
    }


}
