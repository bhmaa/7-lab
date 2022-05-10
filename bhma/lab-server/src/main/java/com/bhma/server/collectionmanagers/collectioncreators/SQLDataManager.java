package com.bhma.server.collectionmanagers.collectioncreators;

import com.bhma.common.data.AstartesCategory;
import com.bhma.common.data.Chapter;
import com.bhma.common.data.Coordinates;
import com.bhma.common.data.MeleeWeapon;
import com.bhma.common.data.SpaceMarine;
import com.bhma.common.data.Weapon;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Hashtable;

public class SQLDataManager {
    private static final int NAME_INDEX = 1;
    private static final int X_INDEX = 2;
    private static final int Y_INDEX = 3;
    private static final int HEALTH_INDEX = 4;
    private static final int CATEGORY_INDEX = 5;
    private static final int WEAPON_INDEX = 6;
    private static final int MELEE_WEAPON_INDEX = 7;
    private static final int CHAPTER_NAME_INDEX = 8;
    private static final int CHAPTER_WORLD_INDEX = 9;
    private static final int CREATION_DATE_INDEX = 10;
    private static final int OWNER_INDEX = 11;
    private static final int ID_INDEX = 12;
    private static final int KEY_INDEX = 12;
    private final Connection connection;
    private final String spaceMarinesTableName;
    private final String usersTableName;
    private final Logger logger;

    public SQLDataManager(Connection connection, String spaceMarinesTableName, String usersTableName, Logger logger) {
        this.connection = connection;
        this.spaceMarinesTableName = spaceMarinesTableName;
        this.usersTableName = usersTableName;
        this.logger = logger;
    }

    private void createDataTable() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + spaceMarinesTableName
                    + "(id SERIAL PRIMARY KEY, "
                    + "key BIGINT NOT NULL, "
                    + "name VARCHAR(50) NOT NULL, "
                    + "x DOUBLE precision NOT NULL CHECK(x>-685), "
                    + "y BIGINT NOT NULL, "
                    + "health DOUBLE precision NOT NULL CHECK(health>0), "
                    + "astartes_category VARCHAR(100) NOT NULL, "
                    + "weapon VARCHAR(100) NOT NULL, "
                    + "melee_weapon VARCHAR(100), "
                    + "chapter_name VARCHAR(50) NOT NULL, "
                    + "chapter_world varchar(50), "
                    + "creation_date TIMESTAMP NOT NULL, "
                    + "owner VARCHAR(100) NOT NULL,"
                    + "FOREIGN KEY(owner) REFERENCES " + usersTableName + "(username))");
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public Hashtable<Long, SpaceMarine> initCollection() {
        createDataTable();
        Hashtable<Long, SpaceMarine> spaceMarines = new Hashtable<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + spaceMarinesTableName);
            while (result.next()) {
                spaceMarines.put(result.getLong("key"), getSpaceMarineFromTable(result));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info(() -> "added " + spaceMarines.size() + " objects from the database");
        return spaceMarines;
    }

    private SpaceMarine getSpaceMarineFromTable(ResultSet result) throws SQLException {
        SpaceMarine spaceMarine = new SpaceMarine(result.getString("name"),
                new Coordinates(result.getDouble("x"), result.getLong("y")),
                result.getDouble("health"),
                AstartesCategory.valueOf(result.getString("astartes_category")),
                Weapon.valueOf(result.getString("weapon")),
                result.getString("melee_weapon") != null ? MeleeWeapon.valueOf(result.getString("melee_weapon")) : null,
                new Chapter(result.getString("chapter_name"),
                        result.getString("chapter_world") != null ? result.getString("chapter_world") : null),
                result.getString("owner"));
        spaceMarine.setId(result.getLong("id"));
        spaceMarine.setCreationDate(result.getTimestamp("creation_date"));
        return spaceMarine;
    }

    public boolean removeById(long id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM "
                    + spaceMarinesTableName + " WHERE id=?");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean removeByKey(long key) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM "
                    + spaceMarinesTableName + " WHERE key=?");
            preparedStatement.setLong(1, key);
            preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public Long add(long key, SpaceMarine spaceMarine) {
        long id;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + spaceMarinesTableName
                    + "(id,name,x,y,health,astartes_category,weapon,melee_weapon,chapter_name,chapter_world,"
                    + "creation_date,owner,key) VALUES (default,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id");
            prepareStatement(preparedStatement, spaceMarine);
            preparedStatement.setLong(KEY_INDEX, key);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            id = result.getLong("id");
        } catch (SQLException e) {
            return null;
        }
        return id;
    }

    public boolean update(long id, SpaceMarine spaceMarine) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + spaceMarinesTableName + " SET ("
                    + "name=?, x=?, y=?, health=?, astartes_category=?, weapon=?, melee_weapon=?,"
                    + "chapter_name=?, chapter_world=?, creation_date=?, owner=?) WHERE id=?");
            prepareStatement(preparedStatement, spaceMarine);
            preparedStatement.setLong(ID_INDEX, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private void prepareStatement(PreparedStatement statement, SpaceMarine spaceMarine) throws SQLException {
        statement.setString(NAME_INDEX, spaceMarine.getName());
        statement.setDouble(X_INDEX, spaceMarine.getCoordinates().getX());
        statement.setLong(Y_INDEX, spaceMarine.getCoordinates().getY());
        statement.setDouble(HEALTH_INDEX, spaceMarine.getHealth());
        statement.setString(CATEGORY_INDEX, String.valueOf(spaceMarine.getCategory()));
        statement.setString(WEAPON_INDEX, String.valueOf(spaceMarine.getWeaponType()));
        statement.setString(MELEE_WEAPON_INDEX, String.valueOf(spaceMarine.getMeleeWeapon()));
        statement.setString(CHAPTER_NAME_INDEX, spaceMarine.getChapter().getName());
        statement.setString(CHAPTER_WORLD_INDEX, spaceMarine.getChapter().getWorld());
        statement.setTimestamp(CREATION_DATE_INDEX, new Timestamp(spaceMarine.getCreationDate().getTime()));
        statement.setString(OWNER_INDEX, spaceMarine.getOwnerUsername());
    }

    public boolean deleteAllOwned(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + spaceMarinesTableName
                    + " WHERE owner=?");
            statement.setString(1, username);
            statement.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
