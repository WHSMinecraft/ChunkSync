package de.whsminecraft.ChunkSync;

import org.bukkit.plugin.java.JavaPlugin;

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
            ChunkReplacer.getInstance().start();
        } else {
            getLogger().info("Unknown \"mode\" setting \"" + mode + "\"");
        }

        getCommand("chunksync").setExecutor(cmds);


        getLogger().info("Plugin was successfully enabled.");

    }
}
