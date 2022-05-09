package com.bhma.client.utility;

import com.bhma.client.exceptions.InvalidInputException;
import com.bhma.client.exceptions.NoConnectionException;
import com.bhma.client.exceptions.ScriptException;
import com.bhma.common.util.ClientRequest;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.PasswordEncoder;
import com.bhma.common.util.PullingResponse;
import com.bhma.common.util.RegistrationCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.common.util.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class ConsoleManager {
    private final InputManager inputManager;
    private final OutputManager outputManager;
    private final SpaceMarineFiller spaceMarineFiller;
    private final Requester requester;
    private HashMap<String, CommandRequirement> commands;
    private User user;

    public ConsoleManager(InputManager inputManager, OutputManager outputManager, SpaceMarineFiller spaceMarineFiller,
                          Requester requester) {
        this.inputManager = inputManager;
        this.outputManager = outputManager;
        this.spaceMarineFiller = spaceMarineFiller;
        this.requester = requester;
    }

    /**
     * starts read commands and execute it while it is not an exit command
     */
    public void start() throws IOException, ClassNotFoundException, InvalidInputException, NoConnectionException, InterruptedException {
        authorize();
        boolean executeFlag = true;
        while (executeFlag) {
            String input = inputManager.read();
            if (!input.trim().isEmpty()) {
                String inputCommand = input.split(" ")[0].toLowerCase(Locale.ROOT);
                String argument = "";
                if (input.split(" ").length > 1) {
                    argument = input.replaceFirst(inputCommand + " ", "");
                }
                try {
                    ClientRequest request = new ClientRequest(inputCommand, argument, getObjectArgument(inputCommand), user);
                    ServerResponse response = (ServerResponse) requester.send(request);
                    executeFlag = processServerResponse(response);
                } catch (ScriptException e) {
                    inputManager.finishReadScript();
                    outputManager.printlnImportantColorMessage(e.getMessage(), Color.RED);
                }
            } else {
                outputManager.printlnColorMessage("Please type any command. To see list of command type \"help\"",
                        Color.RED);
            }
        }
    }

    private Object getObjectArgument(String commandName) throws ScriptException, InvalidInputException {
        Object object = null;
        if (commands.containsKey(commandName)) {
            CommandRequirement requirement = commands.get(commandName);
            switch (requirement) {
                case CHAPTER:
                    object = spaceMarineFiller.fillChapter();
                    break;
                case SPACE_MARINE:
                    object = spaceMarineFiller.fillSpaceMarine(user.getUsername());
                    break;
                case WEAPON:
                    object = spaceMarineFiller.fillWeaponType();
                    break;
                default:
                    break;
            }
        }
        return object;
    }

    /**
     * process the ExecuteCode of ServerResponse. print messages and finish read script if there's an error
     * @param serverResponse received response
     * @return false if it was exit command, true otherwise
     */
    private boolean processServerResponse(ServerResponse serverResponse) {
        ExecuteCode executeCode = serverResponse.getExecuteCode();
        switch (executeCode) {
            case ERROR:
                inputManager.finishReadScript();
                outputManager.printlnColorMessage(executeCode.getMessage(), Color.RED);
                outputManager.printlnColorMessage(serverResponse.getMessage(), Color.RED);
                break;
            case SUCCESS:
                outputManager.printlnColorMessage(executeCode.getMessage(), Color.GREEN);
                break;
            case VALUE:
                outputManager.printlnImportantMessage(executeCode.getMessage());
                outputManager.printlnImportantMessage(serverResponse.getMessage());
                break;
            case READ_SCRIPT:
                inputManager.startReadScript(serverResponse.getMessage());
                break;
            case EXIT:
                outputManager.printlnImportantColorMessage(executeCode.getMessage(), Color.RED);
                return false;
            default:
                outputManager.printlnImportantColorMessage("incorrect server's response...", Color.RED);
        }
        return true;
    }

    private void authorize() throws InvalidInputException, NoConnectionException, IOException, InterruptedException,
            ClassNotFoundException {
        boolean isAuthorized = false;
        do {
            outputManager.printlnImportantMessage("enter username:");
            String username = inputManager.read();
            outputManager.printlnImportantMessage("enter password:");
            String password = PasswordEncoder.encode(inputManager.read());
            User newUser = new User(username, password);
            PullingResponse response = requester.sendPullingRequest(newUser);
            if (response.getRegistrationCode() == RegistrationCode.AUTHORIZED
                    || response.getRegistrationCode() == RegistrationCode.REGISTERED) {
                isAuthorized = true;
                commands = response.getRequirements();
                this.user = user;
            }
            outputManager.printlnImportantMessage(response.getRegistrationCode().getMessage());
        } while (!isAuthorized);
    }
}
