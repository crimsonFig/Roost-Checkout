package app.util.io;

import app.controller.ViewStrategy;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class InventoryConfigAccessor {
    public static final char PREFIX_DELIM = '_';

    private final Map<String, Set<String>> consoleMap;
    private final Map<String, Set<String>> stationMap;
    private final Set<String>              equipmentSet;
    private final Map<String, Integer>     totalMap;

    public InventoryConfigAccessor() {
        totalMap = new HashMap<>();
        equipmentSet = new HashSet<>();
        stationMap = new HashMap<>();
        consoleMap = new HashMap<>();
        readFromConfig();
    }

    private void readFromConfig() {
        try {
            File                   fXmlFile  = new File(ViewStrategy.RESOURCES.ITEM_CONFIG.getViewURL().toURI());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder        dBuilder  = dbFactory.newDocumentBuilder();
            Document               doc       = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            // First - initialize console mapping
            NodeList consoles = doc.getElementsByTagName("console");
            for (int i = 0; i < consoles.getLength(); i++) {
                Node    node     = consoles.item(i);
                Element eConsole = (Element) node;
                consoleMap.put(eConsole.getAttribute("prepend"), new HashSet<>());
            }

            // Second - Map out the equipment
            NodeList equipments = doc.getElementsByTagName("equipment");
            for (int i = 0; i < equipments.getLength(); i++) {
                Element eEquipment    = (Element) equipments.item(i);
                String  equipmentName = eEquipment.getAttribute("name");
                int equipmentTotal = (eEquipment.getAttribute("total").matches("\\d+"))
                                     ? Integer.parseInt(eEquipment.getAttribute("total"))
                                     : 0;
                String consolePrepend = equipmentName.substring(0, 3);
                if (consoleMap.containsKey(consolePrepend)) consoleMap.get(consolePrepend).add(equipmentName);
                equipmentSet.add(equipmentName);
                totalMap.put(equipmentName, equipmentTotal);
            }

            // Thirdly - Map out the stations
            NodeList stations = doc.getElementsByTagName("station");
            for (int i = 0; i < stations.getLength(); i++) {
                Element eStation    = (Element) stations.item(i);
                String  stationName = eStation.getAttribute("name");
                int stationTotal = (eStation.getAttribute("total").matches("\\d+"))
                                   ? Integer.parseInt(eStation.getAttribute("total"))
                                   : 0;
                stationMap.put(stationName, new HashSet<>());
                totalMap.put(stationName, stationTotal);
                NodeList equipables = ((Element) stations.item(i)).getElementsByTagName("equipable");
                for (int j = 0; j < equipables.getLength(); j++) {
                    Element eEquipable = (Element) equipables.item(j);
                    stationMap.get(stationName).add(eEquipable.getAttribute("name"));
                }
                NodeList consolables = eStation.getElementsByTagName("consolable");
                for (int j = 0; j < consolables.getLength(); j++) {
                    Element eConsolable = (Element) consolables.item(j);
                    if (consoleMap.containsKey(eConsolable.getAttribute("prepend"))) {
                        stationMap.get(stationName).addAll(consoleMap.get(eConsolable.getAttribute("prepend")));
                    }
                }
            }

            // Finally - create Requestable objects
            // TODO - set up observables for the map values (which are collections) to propagate updates easily
            // TODO - set up factory/prototype objects that requestable objects share property references with for update changes
        } catch (Exception e) {
            // TODO - have actual error control
            e.printStackTrace();
        }
    }
    // todo: write/update stations and equipment to config


    public Map<String, Set<String>> getConsoleMap() {
        return consoleMap;
    }

    public Map<String, Set<String>> getStationMap() {
        return stationMap;
    }

    public Set<String> getEquipmentSet() {
        return equipmentSet;
    }

    public Map<String, Integer> getTotalMap() {
        return totalMap;
    }
}
