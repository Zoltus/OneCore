package io.github.zoltus.onecore.data.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.zoltus.onecore.OneCore;

public class MySQL extends Database {

    public MySQL(OneCore plugin, DBCredentials credentials, DBQueries queries) {
        super(plugin, credentials, queries);
    }


    @Override
    public HikariDataSource initHikari() {
        return null;
    }
}
