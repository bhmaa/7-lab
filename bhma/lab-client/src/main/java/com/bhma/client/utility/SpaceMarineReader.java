package com.bhma.client.utility;

import com.bhma.client.data.AstartesCategory;
import com.bhma.client.data.MeleeWeapon;
import com.bhma.client.data.Weapon;
import com.bhma.client.exceptions.IllegalValueException;

import java.util.Locale;

/**
 * responsible for reading and checking values of fields in SpaceMarine
 */
public class SpaceMarineReader {
    private static final int MIN_X = -685;
    private final InputManager inputManager;

    public SpaceMarineReader(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    /**
     * @return entered value of x field in Coordinates if it was correct
     * @throws IllegalValueException if entered value was lower than -685
     * @throws NumberFormatException if entered value was not a number
     */
    public double readX() throws IllegalValueException, NumberFormatException {
        double x;
        String stringX;
        stringX = inputManager.read();
        x = Double.parseDouble(stringX);
        if (x <= MIN_X) {
            throw new IllegalValueException("Value must be greater than " + MIN_X);
        }
        return x;
    }

    /**
     * @return entered value of y field in Coordinates if it was correct
     * @throws NumberFormatException if entered value was not a number
     */
    public long readY() throws NumberFormatException {
        long y;
        String stringY;
        stringY = inputManager.read();
        y = Long.parseLong(stringY);
        return y;
    }

    /**
     * @return entered value of health field in SpaceMarine if it was correct
     * @throws IllegalValueException if entered value was lower than 0
     * @throws NumberFormatException if entered value was not a number
     */
    public double readHealth() throws IllegalValueException, NumberFormatException {
        String stringHealth;
        stringHealth = inputManager.read();
        double health = Double.parseDouble(stringHealth);
        if (health <= 0) {
            throw new IllegalValueException("Value must be greater than 0");
        }
        return health;
    }

    /**
     * @return entered value of category field in SpaceMarine if it was correct
     * @throws IllegalArgumentException if entered value isn't one of values in AstartesCategory
     */
    public AstartesCategory readCategory() throws IllegalArgumentException {
        AstartesCategory category;
        category = AstartesCategory.valueOf(inputManager.read().toUpperCase(Locale.ROOT));
        return category;
    }

    /**
     * @return entered value of weaponType field in SpaceMarine if it was correct
     * @throws IllegalArgumentException if entered value isn't one of values in Weapon
     */
    public Weapon readWeaponType() throws IllegalArgumentException {
        Weapon weapon;
        weapon = Weapon.valueOf(inputManager.read().toUpperCase(Locale.ROOT));
        return weapon;
    }

    /**
     * @return entered value of meleeWeapon field in SpaceMarine if it was correct (or null)
     * @throws IllegalArgumentException if entered value isn't one of values in MeleeWeapon and isn't empty
     */
    public MeleeWeapon readMeleeWeapon() throws IllegalArgumentException {
        MeleeWeapon meleeWeapon = null;
        String stringMeleeWeapon = inputManager.read();
        if (!stringMeleeWeapon.isEmpty()) {
            meleeWeapon = MeleeWeapon.valueOf(stringMeleeWeapon.toUpperCase(Locale.ROOT));
        }
        return meleeWeapon;
    }

    /**
     * @return entered value if it was not empty
     * @throws IllegalValueException if entered value was empty
     */
    public String readNotNullString() throws IllegalValueException {
        String string;
        string = inputManager.read();
        if (string.isEmpty()) {
            throw new IllegalValueException("This field cannot be null");
        }
        return string;
    }

    public String readNotEmptyString() throws IllegalValueException {
        String string;
        string = readNotNullString();
        if (string.trim().isEmpty()) {
            throw new IllegalValueException("This field cannot be empty");
        }
        return string;
    }
}
