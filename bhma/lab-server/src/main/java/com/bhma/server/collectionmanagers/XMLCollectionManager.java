package com.bhma.server.collectionmanagers;

import com.bhma.common.data.SpaceMarine;
import com.bhma.common.data.Weapon;
import com.bhma.server.collectionmanagers.collectioncreators.XMLDataManager;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;

@XmlRootElement(name = "spaceMarines")
public class XMLCollectionManager extends CollectionManager implements SavableCollectionManager {
    @XmlElement(name = "spaceMarine")
    private Hashtable<Long, SpaceMarine> collection = new Hashtable<>();
    private String filePath;

    public XMLCollectionManager(Hashtable<Long, SpaceMarine> collection, String filePath) {
        super(collection);
        this.collection = collection;
        this.filePath = filePath;
    }

    public XMLCollectionManager() {
        super(new Hashtable<>());
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void addToCollection(Long key, SpaceMarine spaceMarine) {
        spaceMarine.setId(getMaxId() + 1);
        spaceMarine.setCreationDate(new Date());
        collection.put(key, spaceMarine);
    }

    public void updateID(Long id, SpaceMarine newInstance) {
        SpaceMarine oldInstance = getById(id);
        oldInstance.setName(newInstance.getName());
        oldInstance.setCoordinates(newInstance.getCoordinates());
        oldInstance.setHealth(newInstance.getHealth());
        oldInstance.setCategory(newInstance.getCategory());
        oldInstance.setWeaponType(newInstance.getWeaponType());
        oldInstance.setMeleeWeapon(newInstance.getMeleeWeapon());
        oldInstance.setChapter(newInstance.getChapter());
    }

    public void remove(Long key) {
        collection.remove(key);
    }

    public void clear(String username) {
        collection.entrySet().removeIf(e -> e.getValue().getOwnerUsername().equals(username));
    }

    public void removeGreater(SpaceMarine spaceMarine, String username) {
        collection.entrySet().removeIf(e -> e.getValue().compareTo(spaceMarine) > 0
                && e.getValue().getOwnerUsername().equals(username));
    }

    public void removeLowerKey(Long key, String username) {
        collection.entrySet().removeIf(e -> e.getKey() < key && e.getValue().getOwnerUsername().equals(username));
    }

    /**
     * convert collection to xml and saves it to the file by filePath
     */
    public void save() throws IOException, JAXBException {
        XMLDataManager.convertToXML(this, filePath);
    }

    public void removeAnyByWeaponType(Weapon weapon, String username) {
        collection.entrySet().stream().filter(e -> e.getValue().getWeaponType().equals(weapon)
                && e.getValue().getOwnerUsername().equals(username))
                .findFirst().map(e -> collection.remove(e.getKey()));
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
