package pl.wavecode.crates;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class WaveCrates extends JavaPlugin {

    private CrateManager crates;

    @Override
    public void onEnable() {
        crates = new CrateManager(this);
        crates.load();

        CrateCommand command = new CrateCommand(this);
        PluginCommand pluginCommand = getCommand("wskrzynka");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        getLogger().info("Wczytano skrzynek: " + crates.all().size());
    }

    @Override
    public void onDisable() {
        AnimationPlayer.finishAll();
        if (crates != null) {
            crates.save();
        }
    }

    public CrateManager crates() {
        return crates;
    }
}
