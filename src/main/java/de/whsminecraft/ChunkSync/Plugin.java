package de.whsminecraft.ChunkSync;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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

        String mode = getConfig().getString("mode");
        ConfigurationSection config;
        if ("server".equals(mode)) {
            getLogger().info("Starting in server mode");
            config = getConfig().getConfigurationSection("server");

            new Thread(new ChunkServer(
                    config.getInt("port")
            )).start();
        } else if ("client".equals(mode)) {
            getLogger().info("Starting in client mode");
            config = getConfig().getConfigurationSection("client");

            ChunkClient chunkClient = new ChunkClient(
                    config.getString("host"),
                    config.getInt("port")
            );
            Material selectTool = Material.getMaterial(config.getString("select-tool"));
            if (selectTool == null) {
                getLogger().severe("No item found for setting \"client.select-tool\": \"" + config.getString("select-tool") +  "\"");
            }
            getServer().getPluginManager().registerEvents(
                    new ChunkSelector(chunkClient, selectTool),
                    this
            );
            ChunkReplacer.getInstance().start();
        } else {
            getLogger().info("Unknown \"mode\" setting \"" + mode + "\"");
        }

        getLogger().info("Plugin was successfully enabled.");
    }
}
