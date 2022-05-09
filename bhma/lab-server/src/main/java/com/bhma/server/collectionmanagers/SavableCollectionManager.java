package com.bhma.server.collectionmanagers;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface SavableCollectionManager {
    void save() throws IOException, JAXBException;
}
