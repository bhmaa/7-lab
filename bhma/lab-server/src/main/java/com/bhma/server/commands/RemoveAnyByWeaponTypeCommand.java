package com.bhma.server.commands;

import com.bhma.common.data.Weapon;
import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandObjectRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.server.collectionmanagers.CollectionManager;

/**
 * remove_any_by_weapon_type command
 */
public class RemoveAnyByWeaponTypeCommand extends Command {
    private final CollectionManager collectionManager;

    public RemoveAnyByWeaponTypeCommand(CollectionManager collectionManager) {
        super("remove_any_by_weapon_type", "удалить из коллекции один элемент, значение поля weaponType"
                + " которого эквивалентно заданному", CommandObjectRequirement.WEAPON, false);
        this.collectionManager = collectionManager;
    }

    /**
     * removes one element from the collection which weapon type is equal to the entered one
     * @param argument must be empty
     * @throws InvalidCommandArguments if argument isn't empty
     */
    public ServerResponse execute(String argument, Object weapon, String username) throws InvalidCommandArguments {
        if (!argument.isEmpty() || weapon == null || weapon.getClass() != Weapon.class) {
            throw new InvalidCommandArguments();
        }
        if (!collectionManager.removeAnyByWeaponType((Weapon) weapon, username)) {
            return new ServerResponse("No element with that weapon type or cannot delete it", ExecuteCode.SERVER_ERROR);
        }
        return new ServerResponse(ExecuteCode.SUCCESS);
    }
}
