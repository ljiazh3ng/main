package com.notably.logic.commands;

import com.notably.logic.commands.exceptions.CommandException;
import com.notably.model.BlockManager;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract void execute(BlockManager model) throws CommandException;

}
