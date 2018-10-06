package net.savagedev.mywarps.commands.subcommands;

import org.bukkit.entity.Player;

public interface ISubCommand {
    void execute(Player user, String... args);
}
