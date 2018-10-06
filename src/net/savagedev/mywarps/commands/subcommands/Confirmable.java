package net.savagedev.mywarps.commands.subcommands;

import org.bukkit.entity.Player;

public interface Confirmable extends ISubCommand {
    void onConfirm(Player user, String... args);
}
