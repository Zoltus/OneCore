package io.github.zoltus.onecore.data.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.zoltus.onecore.OneCore;

public class SQLite extends Database {

    public SQLite(OneCore plugin) {
        super(plugin,
                String.format("jdbc:sqlite:%s/database.db", plugin.getDataFolder()),
                "createTables.sql",
                "insertHomes.sql",
                "insertPlayers.sql",
                "insertBalances.sql",
                "selectHomes.sql",
                "selectPlayersAndBalances.sql");
    }

    @Override
    public HikariDataSource initHikari() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseURL); // Set your database URL here
        //config.setUsername(Config.DB_USERNAME.getString());
       // config.setPassword(Config.DB_PASSWORD.getString());
        // Set SQLite specific configurations
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("temp_store", "MEMORY");
        config.addDataSourceProperty("synchronous", "NORMAL");
        config.addDataSourceProperty("foreign_keys", "1");

        return new HikariDataSource(config);
    }
}
