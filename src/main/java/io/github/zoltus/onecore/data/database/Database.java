package io.github.zoltus.onecore.data.database;

import java.sql.Connection;

public interface Database {
    Connection connection();

    void closeConnection();

    void createTables();

    void initAutoSave();

    void saveData(boolean async);

    void loadData();

    void updateBalTop();

    void restartDatabase();


}
