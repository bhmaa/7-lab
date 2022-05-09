package com.bhma.server.collectionmanagers;

import com.bhma.common.data.Chapter;
import com.bhma.common.data.SpaceMarine;
import com.bhma.common.data.Weapon;

import java.util.Date;
import java.util.Hashtable;
import java.util.StringJoiner;

public abstract class CollectionManager {
    private final Hashtable<Long, SpaceMarine> collection;
    private final Date dateOfInitialization = new Date();

    public CollectionManager(Hashtable<Long, SpaceMarine> collection) {
        this.collection = collection;
    }

    public Hashtable<Long, SpaceMarine> getCollection() {
        return collection;
    }

    /**
     * @return string with collection's class, date of creation and size
     */
    public String collectionInfo() {
        return "Collection type: " + collection.getClass().getName() + "\n"
                + "Date of initialization: " + dateOfInitialization + "\n"
                + "Collection size: " + collection.size();
    }

    public abstract void addToCollection(Long key, SpaceMarine spaceMarine);

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        if (collection.size() > 0) {
            collection.forEach((k, v) -> stringJoiner.add(k + ": " + v.toString()));
        } else {
            stringJoiner.add("The collection is empty");
        }
        return stringJoiner.toString();
    }

    /**
     * @param id id of a space-marine instance
     * @return space-marine instance whose id is equal to the entered one
     */
    public SpaceMarine getById(Long id) {
        return collection.values().stream().filter(v -> v.getId().equals(id)).findFirst().get();
    }

    public abstract void updateID(Long id, SpaceMarine newInstance);

    /**
     * @param id checking id
     * @return true if the collection has an element with that id, and false otherwise
     */
    public boolean containsId(Long id) {
        return collection.values().stream().anyMatch(v -> v.getId().equals(id));
    }

    /**
     * @param key checking key
     * @return true if the collection has a key which is equal to the checking one, and false otherwise
     */
    public boolean containsKey(Long key) {
        return collection.containsKey(key);
    }

    /**
     * @param key
     * @return an element in collection which is the value for the entered key
     */
    public SpaceMarine getByKey(Long key) {
        return collection.get(key);
    }

    /**
     * @param key key to the element that will be removed from the collection
     */
    public abstract void remove(Long key);

    /**
     * removes all elements from the collection
     */
    public abstract void clear(String username);

    /**
     * removes all elements which is greater than param from the collection
     * @param spaceMarine
     */
    public abstract void removeGreater(SpaceMarine spaceMarine, String username);

    /**
     * removes all elements which have key that lower than param
     * @param key
     */
    public abstract void removeLowerKey(Long key, String username);

    /**
     * removes one element whose weapon type is equals to param
     * @param weapon
     */
    public abstract void removeAnyByWeaponType(Weapon weapon, String username);

    /**
     * @return average value of the health field in collection
     */
    public double averageOfHealth() {
        return collection.values().stream().mapToDouble(SpaceMarine::getHealth).average().orElse(0);
    }

    /**
     * @param chapter
     * @return number of elements whose value of chapter field is equal to the param
     */
    public long countByChapter(Chapter chapter) {
        return collection.values().stream().filter(v -> v.getChapter().equals(chapter)).count();
    }
}
