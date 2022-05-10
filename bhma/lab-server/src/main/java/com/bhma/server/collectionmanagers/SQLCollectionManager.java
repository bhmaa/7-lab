package com.bhma.server.collectionmanagers;

import com.bhma.common.data.SpaceMarine;
import com.bhma.common.data.Weapon;
import com.bhma.server.collectionmanagers.collectioncreators.SQLDataManager;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLCollectionManager extends CollectionManager {
    private final Hashtable<Long, SpaceMarine> collection;
    private final SQLDataManager sqlDataManager;

    public SQLCollectionManager(Hashtable<Long, SpaceMarine> collection, SQLDataManager sqlDataManager) {
        super(collection);
        this.collection = collection;
        this.sqlDataManager = sqlDataManager;
    }

    @Override
    public void addToCollection(Long key, SpaceMarine spaceMarine) {
        Long id = sqlDataManager.add(key, spaceMarine);
        if (id != null) {
            spaceMarine.setId(id);
            collection.put(key, spaceMarine);
        }
    }

    @Override
    public void updateID(Long id, SpaceMarine newInstance) {
        SpaceMarine oldInstance = getById(id);
        if (oldInstance.getOwnerUsername().equals(newInstance.getOwnerUsername())) {
            if (sqlDataManager.update(id, newInstance)) {
                oldInstance.setName(newInstance.getName());
                oldInstance.setCoordinates(newInstance.getCoordinates());
                oldInstance.setHealth(newInstance.getHealth());
                oldInstance.setCategory(newInstance.getCategory());
                oldInstance.setWeaponType(newInstance.getWeaponType());
                oldInstance.setMeleeWeapon(newInstance.getMeleeWeapon());
                oldInstance.setChapter(newInstance.getChapter());
            }
        }
    }

    @Override
    public void remove(Long key) {
        if (sqlDataManager.removeByKey(key)) {
            collection.remove(key);
        }
    }

    @Override
    public void clear(String username) {
        if (sqlDataManager.deleteAllOwned(username)) {
            collection.entrySet().removeIf(e -> e.getValue().getOwnerUsername().equals(username));
        }
    }

    @Override
    public void removeGreater(SpaceMarine spaceMarine, String username) {
        List<Long> ids = collection.values().stream().filter(e -> e.compareTo(spaceMarine) > 0
                        && e.getOwnerUsername().equals(username)).map(SpaceMarine::getId).collect(Collectors.toList());
        ids.forEach(id -> {
            if (sqlDataManager.removeById(id)) {
                collection.remove(getById(id));
            }
        });
    }

    @Override
    public void removeLowerKey(Long key, String username) {
        List<Long> keys = collection.entrySet().stream().filter(e -> e.getKey() < key
                && e.getValue().getOwnerUsername().equals(username)).map(Map.Entry::getKey).collect(Collectors.toList());
        keys.forEach(k -> {
            if (sqlDataManager.removeByKey(k)) {
                collection.remove(k);
            }
        });
    }

    @Override
    public void removeAnyByWeaponType(Weapon weapon, String username) {
        collection.entrySet().stream().filter(e -> e.getValue().getWeaponType().equals(weapon)
                && e.getValue().getOwnerUsername().equals(username)).limit(1).forEach(e -> {
            if (sqlDataManager.removeByKey(e.getKey())) {
                collection.remove(e.getKey());
            }
        });
    }
}
