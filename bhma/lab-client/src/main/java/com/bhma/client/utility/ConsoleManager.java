package com.bhma.client.utility;

import com.bhma.client.exceptions.InvalidInputException;
import com.bhma.client.exceptions.NoConnectionException;
import com.bhma.client.exceptions.ScriptException;
import com.bhma.common.util.ClientRequest;
import com.bhma.common.util.CommandObjectRequirement;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.PullingResponse;
import com.bhma.common.util.RegistrationCode;
import com.bhma.common.util.ServerResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class ConsoleManager {
    private final InputManager inputManager;
    private final OutputManager outputManager;
    private final SpaceMarineFiller spaceMarineFiller;
    private final Requester requester;
    private HashMap<String, CommandRequirement> commands;
    private String username;
    private String password;

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
                    ClientRequest request = new ClientRequest(inputCommand, argument, getObjectArgument(inputCommand, argument),
                            username, password);
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

    private Object getObjectArgument(String commandName, String argument) throws ScriptException, InvalidInputException {
        Object object = null;
        if (commands.containsKey(commandName)) {
            if (commands.get(commandName).isCommandNeedsStringArgument() == !argument.isEmpty()) {
                CommandObjectRequirement requirement = commands.get(commandName).getCommandObjectRequirement();
                switch (requirement) {
                    case CHAPTER:
                        object = spaceMarineFiller.fillChapter();
                        break;
                    case SPACE_MARINE:
                        object = spaceMarineFiller.fillSpaceMarine(username);
                        break;
                    case WEAPON:
                        object = spaceMarineFiller.fillWeaponType();
                        break;
                    default:
                        break;
                }
            }
        }
        return object;
    }

    /**
     * process the ExecuteCode of ServerResponse. print messages and finish read script if there's an error
     *
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
            case SERVER_ERROR:
                outputManager.printlnImportantColorMessage(executeCode.getMessage(), Color.RED);
                if (serverResponse.getMessage() != null) {
                    outputManager.printlnImportantColorMessage("cause:", Color.RED);
                    outputManager.printlnImportantColorMessage(serverResponse.getMessage(), Color.RED);
                }
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
            outputManager.print("enter username:");
            String newUsername = inputManager.read();
            outputManager.print("enter password:");
            String newPassword = inputManager.read();
            PullingResponse response = requester.sendPullingRequest(newUsername, newPassword);
            if (response.getRegistrationCode() == RegistrationCode.AUTHORIZED
                    || response.getRegistrationCode() == RegistrationCode.REGISTERED) {
                isAuthorized = true;
                commands = response.getRequirements();
                username = newUsername;
                password = newPassword;
            }
            outputManager.printlnImportantMessage(response.getRegistrationCode().getMessage());
        } while (!isAuthorized);
    }
}
