package io.github.zoltus.onecore.data.database;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import lombok.Getter;

public class DatabaseManager {

    @Getter
    private final Database database;
    private final OneCore plugin;

    public DatabaseManager(OneCore plugin) {
        this.plugin = plugin;
        this.database = switch (Config.DB_TYPE.getString().toLowerCase()) {
            case "h2" -> new H2(plugin);
            case "mysql" -> new MySQL(plugin);
            case "mariadb" -> new MariaDB(plugin);
            case "postgresql" -> new PostgreSQL(plugin);
            //default sqlite
            default ->  new SQLite(plugin);
        };
    }
}
