package net.savagedev.mywarps.commands.state;

import net.savagedev.mywarps.commands.subcommands.Confirmable;
import org.bukkit.Location;

public class ConfirmationManager {
    private Confirmable command;
    private String[] arguments;
    private CommandState state;
    private Location location;

    public ConfirmationManager(CommandState state) {
        this.state = state;
    }

    public void setArguments(String... arguments) {
        this.arguments = arguments;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCommand(Confirmable command) {
        this.command = command;
    }

    public void setState(CommandState state) {
        this.state = state;
    }

    public void reset() {
        this.state = CommandState.READY;
        this.arguments = null;
        this.location = null;
        this.command = null;
    }

    public Location getLocation() {
        return this.location;
    }

    public String[] getArguments() {
        return this.arguments;
    }

    public Confirmable getCommand() {
        return this.command;
    }

    public CommandState getState() {
        return this.state;
    }
}
