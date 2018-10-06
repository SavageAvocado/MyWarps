package net.savagedev.mywarps.commands;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.commands.subcommands.*;
import net.savagedev.mywarps.utils.CaseInsensitiveString;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWarpsCmd implements CommandExecutor, TabCompleter {
    private Map<String, SubCommand> subCommands;
    private MyWarps plugin;

    public MyWarpsCmd(MyWarps plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        this.subCommands = new HashMap<>();
        this.subCommands.put("cancel", new CancelCmd(this.plugin, new Permission("mywarps.cancel")));
        this.subCommands.put("confirm", new ConfirmCmd(this.plugin, new Permission("mywarps.confirm")));
        this.subCommands.put("create", new CreateCmd(this.plugin, new Permission("mywarps.create")));
        this.subCommands.put("delete", new DeleteCmd(this.plugin, new Permission("mywarps.delete")));
        this.subCommands.put("help", new HelpCmd(this.plugin, new Permission("mywarps.help")));
        this.subCommands.put("list", new ListCmd(this.plugin, new Permission("mywarps.list")));
        this.subCommands.put("move", new MoveCmd(this.plugin, new Permission("mywarps.move")));
        this.subCommands.put("owner", new OwnerCmd(this.plugin, new Permission("mywarps.owner")));
        this.subCommands.put("rename", new RenameCmd(this.plugin, new Permission("mywarps.rename")));
        this.subCommands.put("transfer", new TransferCmd(this.plugin, new Permission("mywarps.transfer")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String d, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player user = (Player) sender;

        if (this.plugin.getUserManager().getUser(user) == null)
            this.plugin.getUserManager().cacheUser(user);

        if (args.length == 0) {
            this.plugin.message(user, this.plugin.getConfig().getString("messages.invalid-arguments").replace("%command%", "help"));
            return true;
        }

        if (!this.subCommands.containsKey(args[0].toLowerCase())) {
            for (String subCmd : this.subCommands.keySet()) {
                if (subCmd.startsWith(args[0].toLowerCase())) {
                    this.plugin.message(user, this.plugin.getConfig().getString("messages.invalid-arguments").replace("%command%", subCmd));
                    return true;
                }
            }

            this.plugin.message(user, this.plugin.getConfig().getString("messages.invalid-arguments").replace("%command%", "help"));
            return true;
        }

        this.subCommands.get(args[0].toLowerCase()).execute(user, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) return null;

        Player user = (Player) sender;

        if (args.length == 0)
            return new ArrayList<>(this.subCommands.keySet());

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            for (String subCmd : this.subCommands.keySet()) {
                if (subCmd.startsWith(args[0].toLowerCase())) {
                    completions.add(subCmd);
                }
            }

            return completions;
        }

        ArrayList<String> players;
        if (args.length == 2 && !args[0].equalsIgnoreCase("transfer") && !args[0].equalsIgnoreCase("rename") && !args[0].equalsIgnoreCase("delete")) {
            players = new ArrayList<>();
            for (Player player : this.plugin.getServer().getOnlinePlayers())
                players.add(player.getName());

            return players;
        }

        ArrayList<String> ownedWarps;
        if (args[0].equalsIgnoreCase("delete")) {
            ownedWarps = new ArrayList<>();
            for (CaseInsensitiveString warp : this.plugin.getUserManager().getUser(user).getOwnedWarps())
                ownedWarps.add(warp.toString());

            return ownedWarps;
        }

        if (args[0].equalsIgnoreCase("rename")) {
            ownedWarps = new ArrayList<>();
            for (CaseInsensitiveString warp : this.plugin.getUserManager().getUser(user).getOwnedWarps())
                ownedWarps.add(warp.toString());

            return ownedWarps;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("transfer")) {
            players = new ArrayList<>();
            for (Player player : this.plugin.getServer().getOnlinePlayers())
                players.add(player.getName());

            return players;
        }

        return null;
    }
}
