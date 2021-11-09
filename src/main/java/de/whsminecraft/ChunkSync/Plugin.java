package de.whsminecraft.ChunkSync;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Plugin extends JavaPlugin {
    private static Plugin instance;

    public static Plugin getInstance() {
        return instance;
    }

    public Plugin() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Commands cmds = new Commands();

        String mode = getConfig().getString("mode");
        if ("server".equals(mode)) {
            getLogger().info("Starting in server mode");
            new Thread(new ChunkServer(1337)).start();
        } else if ("client".equals(mode)) {
            getLogger().info("Starting in client mode");
            getServer().getPluginManager().registerEvents(new ChunkSelector(), this);
        } else {
            getLogger().info("Unknown \"mode\" setting \"" + mode + "\"");
        }

        getCommand("chunksync").setExecutor(cmds);


        getLogger().info("Plugin was successfully enabled.");

    }
}
