package app.model;

/**
 * an equipment model. utilizes `Property` classes to help alert other objects of value changes. an equipment represents
 * an object that can be supplied and equipped with a given station an equipment can belong to a group to represent a
 * generic kind. these properties are made in mind with an inventory system for classification being easier to manage.
 */
public class Equipment extends Requestable {

    private Equipment(String name) {
        super(name);
    }

    public static Equipment equipmentFactory(String equipmentName) {
        return EquipmentFactory.initFactory(new Equipment(equipmentName));
    }

}
