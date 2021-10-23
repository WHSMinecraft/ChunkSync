package de.whsminecraft.ChunkSync;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equals("chunksync")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "This command is only available to players");
                return true;
            }

            Player sender = (Player) commandSender;
            Chunk standingIn = sender.getLocation().getChunk();
            Plugin.getInstance().getLogger().info("Standing in chunk (" + standingIn.getX() + ", " + standingIn.getZ() + ")");

            ChunkSnapshot originChunk = sender.getLocation().getChunk().getChunkSnapshot();

            ChunkReplacer cr = new ChunkReplacer();
            cr.replace(standingIn, originChunk);
            Plugin.getInstance().getLogger().info("Replaced chunk");
            return true;
        }
        return false;
    }
}
