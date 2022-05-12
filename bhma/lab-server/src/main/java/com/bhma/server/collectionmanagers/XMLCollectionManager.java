package com.bhma.server.collectionmanagers;

import com.bhma.common.data.SpaceMarine;
import com.bhma.common.data.Weapon;
import com.bhma.server.collectionmanagers.datamanagers.XMLDataManager;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@XmlRootElement(name = "spaceMarines")
public class XMLCollectionManager extends CollectionManager implements SavableCollectionManager {
    @XmlElement(name = "spaceMarine")
    private ConcurrentHashMap<Long, SpaceMarine> collection = new ConcurrentHashMap<>();
    private String filePath;

    public XMLCollectionManager(ConcurrentHashMap<Long, SpaceMarine> collection, String filePath) {
        super(collection);
        this.collection = collection;
        this.filePath = filePath;
    }

    public XMLCollectionManager() {
        super(new ConcurrentHashMap<>());
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void addToCollection(Long key, SpaceMarine spaceMarine) {
        spaceMarine.setId(getMaxId() + 1);
        spaceMarine.setCreationDate(new Date());
        collection.put(key, spaceMarine);
    }

    public boolean updateID(long id, SpaceMarine newInstance) {
        SpaceMarine oldInstance = getById(id);
        if (!oldInstance.getOwnerUsername().equals(newInstance.getOwnerUsername())) {
            return false;
        }
        oldInstance.setName(newInstance.getName());
        oldInstance.setCoordinates(newInstance.getCoordinates());
        oldInstance.setHealth(newInstance.getHealth());
        oldInstance.setCategory(newInstance.getCategory());
        oldInstance.setWeaponType(newInstance.getWeaponType());
        oldInstance.setMeleeWeapon(newInstance.getMeleeWeapon());
        oldInstance.setChapter(newInstance.getChapter());
        return true;
    }

    public boolean remove(long key) {
        if (!collection.containsKey(key)) {
            return false;
        }
        collection.remove(key);
        return true;
    }

    public boolean clear(String username) {
        collection.entrySet().removeIf(e -> e.getValue().getOwnerUsername().equals(username));
        return true;
    }

    public long removeGreater(SpaceMarine spaceMarine, String username) {
        collection.entrySet().removeIf(e -> e.getValue().compareTo(spaceMarine) > 0
                && e.getValue().getOwnerUsername().equals(username));
        return 0;
    }

    public long removeLowerKey(long key, String username) {
        collection.entrySet().removeIf(e -> e.getKey() < key && e.getValue().getOwnerUsername().equals(username));
        return 0;
    }

    /**
     * convert collection to xml and saves it to the file by filePath
     */
    public void save() throws IOException, JAXBException {
        XMLDataManager.convertToXML(this, filePath);
    }

    public boolean removeAnyByWeaponType(Weapon weapon, String username) {
        Optional<Map.Entry<Long, SpaceMarine>> item = collection.entrySet().stream().filter(e -> e.getValue().
                getWeaponType().equals(weapon) && e.getValue().getOwnerUsername().equals(username)).findFirst();
        if (!item.isPresent()) {
            return false;
        }
        collection.remove(item.get().getKey());
        return true;
    }

    /**
     * @return max id from the collection
     */
    public long getMaxId() {
        if (collection.size() > 0) {
            return collection.values().stream().max(Comparator.comparing(SpaceMarine::getId)).get().getId();
        } else {
            return 0;
        }
    }
}
