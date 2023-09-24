package io.github.zoltus.onecore.data.database;

import io.github.zoltus.onecore.OneCore;

public class MySQL extends Database {

    public MySQL(OneCore plugin) {
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
