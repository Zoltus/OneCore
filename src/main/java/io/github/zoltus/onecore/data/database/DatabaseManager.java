package io.github.zoltus.onecore.data.database;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import lombok.Getter;

public class DatabaseManager {

    @Getter
    private final Database database;
    private final OneCore plugin;
    private final DBCredentials credentials;
    String createTables = "";

    public DatabaseManager(OneCore plugin) {
        this.plugin = plugin;
        this.credentials = getCredentials();
        this.database = switch (Config.DB_TYPE.getString().toLowerCase()) {
            case "h2" -> new H2(plugin, credentials, createTables);
            case "mysql" -> new MySQL(plugin, credentials, createTables);
            case "mariadb" -> new MariaDB(plugin, credentials, createTables);
            case "postgresql" -> new PostgreSQL(plugin, credentials, createTables);
            //default sqlite
            default -> new SQLite(plugin, credentials, createTables);
        };
    }

    private DBCredentials getCredentials() {
        int port = Config.DB_PORT.getInt();
        String userName = Config.DB_USERNAME.getString();
        String password = Config.DB_PASSWORD.getString();
        String address = Config.DB_ADDRESS.getString();
        String dbName = Config.DB_DATABASE.getString();
        return new DBCredentials(dbName, userName, password, address, port);
    }
}
