package edu.ntnu.arunang.wargames.unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a Factory class that creates Units from strings. This is usually done
 * when reading from files. When adding new Units, they should also be added to the factory.
 * This class is private because it only has static methods and variables, and there is therefore no point
 * to it being instantiated.
 */

public class UnitFactory {

    /**
     * Private constructor because the class should not be instantiated.
     */

    private UnitFactory() {
    }

    /**
     * Constructs a unit from a parsed line. The units is created with stats reset.
     *
     * @param type   unit type
     * @param name   unit name
     * @param health unit health
     * @return A constructed Unit
     * @throws IllegalArgumentException if the Unit type is not defined or wrong.
     */

    public static Unit constructUnitFromString(String type, String name, int health) throws IllegalArgumentException {

        return constructUnit(checkType(type), name, health);
    }

    /**
     * Constructs a unit by a give UnitType
     *
     * @param type   unit type
     * @param name   unit name
     * @param health unit health
     * @return A constructed Unit
     * @throws IllegalArgumentException if the Unit type is not constructable
     */

    public static Unit constructUnit(UnitType type, String name, int health) throws IllegalArgumentException {
        return switch (type) {
            case CavalryUnit -> new CavalryUnit(name, health);
            case CommanderUnit -> new CommanderUnit(name, health);
            case InfantryUnit -> new InfantryUnit(name, health);
            case RangedUnit -> new RangedUnit(name, health);
            default -> throw new IllegalArgumentException(String.format("Unittype %s does not exist", type));
        };

    }

    /**
     * Constructs units from a parsed line. The units is created with stats reset.
     *
     * @param type   unit type
     * @param name   unit name
     * @param health unit health
     * @param count  sum of units
     * @return A constructed Unit.
     * @throws IllegalArgumentException if the Unit type is not defined or wrong.
     */

    public static List<Unit> constructUnitsFromString(String type, String name, int health, int count) {
        List<Unit> units = new ArrayList<>();
        UnitType unitType = checkType(type);

        for (int i = 0; i < count; i++) {
            units.add(constructUnit(unitType, name, health));
        }
        return units;
    }

    /**
     * Helper method to check if the specified unit is an UnitType.
     *
     * @param type type in string form
     * @return UnitType
     * @throws IllegalArgumentException if the UnitType does not exist.
     */
    private static UnitType checkType(String type) throws IllegalArgumentException {
        UnitType unitType;
        try {
            unitType = UnitType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Unittype %s does not exist", type));
        }
        return unitType;
    }
}