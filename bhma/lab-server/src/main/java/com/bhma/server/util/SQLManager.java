package com.bhma.server.util;

import com.bhma.common.data.AstartesCategory;
import com.bhma.common.data.Chapter;
import com.bhma.common.data.Coordinates;
import com.bhma.common.data.MeleeWeapon;
import com.bhma.common.data.SpaceMarine;
import com.bhma.common.data.Weapon;
import com.bhma.common.util.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Hashtable;

public class SQLManager {
    private final Connection connection;
    private final String spaceMarinesTableName = "\"space_marines\"";
    private final String usersTableName = "\"space_marines_users\"";

    public SQLManager(Connection connection) {
        this.connection = connection;
    }

    public void createUserTable() {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("CREATE TABLE IF NOT EXIST " + usersTableName + " (username VARCHAR(100) NOT NULL UNIQUE,"
                    + " password VARCHAR(100) NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + usersTableName + " VALUES (?, ?)");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getHashPassword());
            statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isUsernameExist(String username) {
        boolean answer = false;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + usersTableName + " WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                answer = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public boolean checkPassword(User user) {
        boolean answer = false;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + usersTableName + " WHERE username = ?, password = ?");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getHashPassword());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                answer = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answer;
    }

    // id, key, name, x, y, health, astartes_category, weapon, melee_weapon, chapter_name, chapter_world, creation_date
    public void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("CREATE TYPE IF NOT EXIST asrartescategory AS ENUM ("
                    + "'SCOUT', 'INCEPTOR', 'TACTICAL', 'CHAPLAIN')");
            statement.executeQuery("CREATE TYPE IF NOT EXIST weapon AS ENUM ("
                    + "'HEAVY_BOLTGUN', 'BOLT_RIFLE', 'PLASMA_GUN', 'INFERNO_PISTOL')");
            statement.executeQuery("CREATE TYPE IF NOT EXIST meleeweapon AS ENUM ("
                    + "'CHAIN_AXE', 'MANREAPER', 'LIGHTING_CLAW', 'POWER_BLADE', 'POWER_FIST')");
            statement.executeQuery("CREATE TABLE IF NOT EXIST " + spaceMarinesTableName
                    + "(id SERIAL PRIMARY KEY, key LONG UNIQUE NOT NULL, name VARCHAR(50) NOT NULL, x DOUBLE NOT NULL CHECK(x>-685),"
                    + "y LONG NOT NULL, health DOUBLE NOT NULL CHECK(health>0), astartes_category asrartescategory NOT NULL,"
                    + "weapon weapon NOT NULL, melee_weapon meleeweapon, chapter_name VARCHAR(50) NOT NULL,"
                    + "chapter_world varchar(50), creation_date TIMESTAMP NOT NULL, owner VARCHAR(100) NOT NULL REFERENCES users(username))");
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

    private SpaceMarine getSpaceMarineFromTable(ResultSet result) throws SQLException {
        SpaceMarine spaceMarine = new SpaceMarine(result.getString("name"),
                new Coordinates(result.getDouble("x"), result.getLong("y")),
                result.getDouble("health"),
                (AstartesCategory) result.getObject("astartes_category"),
                (Weapon) result.getObject("weapon"),
                (MeleeWeapon) result.getObject("melee_weapon"),
                new Chapter(result.getString("chapter_name"), result.getString("chapter_world")),
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
                    + "default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? RETURNING id");
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
        statement.setObject(++i, spaceMarine.getCategory());
        statement.setObject(++i, spaceMarine.getWeaponType());
        statement.setObject(++i, spaceMarine.getMeleeWeapon());
        statement.setString(++i, spaceMarine.getChapter().getName());
        statement.setString(++i, spaceMarine.getChapter().getWorld());
        statement.setTimestamp(++i, new Timestamp(spaceMarine.getCreationDate().getTime()));
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
