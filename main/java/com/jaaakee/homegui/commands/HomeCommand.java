package com.jaaakee.homegui.commands;

import com.jaaakee.homegui.inventory.HomeInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("HomeGUI: You must be a player to use this command!");
            return false;
        }

        if (!sender.hasPermission("homegui.use")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        Player player = (Player)sender;
        player.openInventory(HomeInventory.createHomeInventory(player.getUniqueId().toString()));
        return true;
    }
}
