package com.bhma.server.util;

import com.bhma.server.collectionmanagers.XMLCollectionManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * responsible for converting xml files to the collection manager instance and converting collection manager instance
 * to the xml file
 */
public final class XMLManager {
    private XMLManager() {
    }

    /**
     * converts the collection manager instance to the file
     * @param collectionManager
     * @param fileName where will be saves this xml-file
     */
    public static void convertToXML(XMLCollectionManager collectionManager, String fileName) throws IOException, JAXBException {
            JAXBContext context = JAXBContext.newInstance(XMLCollectionManager.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName));
            marshaller.marshal(collectionManager, bufferedOutputStream);
            bufferedOutputStream.close();
    }

    /**
     * converts xml-file to the collection manager instance
     * @param fileName where is the file
     * @return collection manager instance
     * @throws JAXBException if xml-file cannot be converted to java object
     */
    public static XMLCollectionManager convertToJavaObject(File fileName) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(XMLCollectionManager.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (XMLCollectionManager) unmarshaller.unmarshal(fileName);
    }

    public static XMLCollectionManager getCollectionManagerFromXML(String filePath, Logger logger) throws JAXBException {
        File file = new File(filePath);
        XMLCollectionManager collectionManager;
        if (file.exists() && file.length() != 0) {
            collectionManager = convertToJavaObject(file);
            collectionManager.setFilePath(filePath);
        } else {
            collectionManager = new XMLCollectionManager(new Hashtable<>(), filePath);
        }
        if (file.exists()) {
            logger.info("The collection was successfully loaded from the file " + filePath);
        } else {
            logger.info("No file with this name was found. A new empty collection has been created");
        }
        return collectionManager;
    }
}
