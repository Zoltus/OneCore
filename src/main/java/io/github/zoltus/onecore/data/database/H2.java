package io.github.zoltus.onecore.data.database;

import io.github.zoltus.onecore.OneCore;

public class H2 extends Database {

    public H2(OneCore plugin) {
        super(plugin,
                String.format("jdbc:h2:%s/database.db", plugin.getDataFolder()),
                "createTables.sql",
                "insertHomes.sql",
                "insertPlayers.sql",
                "insertBalances.sql",
                "selectHomes.sql",
                "selectPlayersAndBalances.sql");
    }


}
