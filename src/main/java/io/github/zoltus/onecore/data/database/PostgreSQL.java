package io.github.zoltus.onecore.data.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.zoltus.onecore.OneCore;

public class PostgreSQL extends Database {

    public PostgreSQL(OneCore plugin, DBCredentials credentials, String createTables) {
        super(plugin, credentials, createTables);
    }



    @Override
    public HikariDataSource initHikari() {
        return null;
    }
}
