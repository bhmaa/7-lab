package com.bhma.server.util;

import com.bhma.common.data.AstartesCategory;
import com.bhma.common.data.Chapter;
import com.bhma.common.data.Coordinates;
import com.bhma.common.data.MeleeWeapon;
import com.bhma.common.data.SpaceMarine;
import com.bhma.common.data.Weapon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Hashtable;

public class SQLManager {
    private final Connection connection;
    private final String spaceMarinesTableName = "space_marines";
    private final String usersTableName = "spacemarinesusers";

    public SQLManager(Connection connection) {
        this.connection = connection;
    }

    // id, key, name, x, y, health, astartes_category, weapon, melee_weapon, chapter_name, chapter_world, creation_date, owner
    public void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + spaceMarinesTableName
                    + "(id SERIAL PRIMARY KEY, key BIGINT NOT NULL, name VARCHAR(50) NOT NULL, x DOUBLE precision NOT NULL CHECK(x>-685),"
                    + "y BIGINT NOT NULL, health DOUBLE precision NOT NULL CHECK(health>0), astartes_category VARCHAR(100) NOT NULL,"
                    + "weapon VARCHAR(100) NOT NULL, melee_weapon VARCHAR(100), chapter_name VARCHAR(50) NOT NULL,"
                    + "chapter_world varchar(50), creation_date TIMESTAMP NOT NULL, owner VARCHAR(100) NOT NULL,"
                    + "FOREIGN KEY(owner) REFERENCES " + usersTableName + "(username))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Hashtable<Long, SpaceMarine> initCollection() throws SQLException {
        Hashtable<Long, SpaceMarine> spaceMarines = new Hashtable<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM " + spaceMarinesTableName);
        while (result.next()) {
            spaceMarines.put(result.getLong("key"), getSpaceMarineFromTable(result));
        }
        return spaceMarines;
    }

    // null can be only meleeweapon chapterworld
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
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + spaceMarinesTableName + " WHERE id = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean removeByKey(long key) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + spaceMarinesTableName + " WHERE key = ?");
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
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + spaceMarinesTableName + " VALUES ("
                    + "default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? RETURNING id");
            prepareStatement(preparedStatement, spaceMarine, key);
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
                    + "key = ?, name = ?, x = ?, y = ?, health = ?, astartes_category = ?, weapon = ?, melee_weapon = ?,"
                    + "chapter_name = ?, chapter_world = ?, creation_date = ? WHERE id = ?");


        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private void prepareStatement(PreparedStatement statement, SpaceMarine spaceMarine, long key) throws SQLException {
        int i = 0;
        statement.setLong(++i, key);
        statement.setString(++i, spaceMarine.getName());
        statement.setDouble(++i, spaceMarine.getCoordinates().getX());
        statement.setLong(++i, spaceMarine.getCoordinates().getY());
        statement.setDouble(++i, spaceMarine.getHealth());
        statement.setString(++i, String.valueOf(spaceMarine.getCategory()));
        statement.setString(++i, String.valueOf(spaceMarine.getWeaponType()));
        statement.setString(++i, String.valueOf(spaceMarine.getMeleeWeapon()));
        statement.setString(++i, spaceMarine.getChapter().getName());
        statement.setString(++i, spaceMarine.getChapter().getWorld());
        statement.setTimestamp(++i, new Timestamp(spaceMarine.getCreationDate().getTime()));
        statement.setString(++i, spaceMarine.getOwnerUsername());
    }

    public boolean clear(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + spaceMarinesTableName + " WHERE owner = ?");
            statement.setString(1, username);
            statement.executeQuery();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
