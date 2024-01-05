package io.github.zoltus.onecore.data.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.zoltus.onecore.OneCore;

public class SQLite extends Database {

    public SQLite(OneCore plugin, DBCredentials credentials, DBQueries queries) {
        super(plugin, credentials, queries);
    }


    @Override
    public HikariDataSource initHikari() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbCredentials.dbName()); // Set your database URL here
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("temp_store", "MEMORY");
        config.addDataSourceProperty("synchronous", "NORMAL");
        config.addDataSourceProperty("foreign_keys", "1");
        return new HikariDataSource(config);
    }

    private DBQueries getQuerys() {
        return null;
    }
}
