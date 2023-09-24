package io.github.zoltus.onecore.data.database;

import io.github.zoltus.onecore.OneCore;

public class MariaDB extends Database {

    public MariaDB(OneCore plugin) {
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
