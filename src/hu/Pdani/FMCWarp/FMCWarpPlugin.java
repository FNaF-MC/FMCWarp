package hu.Pdani.FMCWarp;

import hu.Pdani.FMCWarp.listener.*;
import hu.Pdani.FMCWarp.manager.GuiManager;
import hu.Pdani.FMCWarp.manager.WarpManager;
import net.citizensnpcs.Citizens;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class FMCWarpPlugin extends JavaPlugin {
    private static FMCWarpPlugin plugin;
    private static WarpManager wm;
    private static GuiManager gm;
    private static boolean citizens = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;
        checkCitizens();
        wm = new WarpManager();
        gm = new GuiManager();
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new NPCListener(), this);
        getCommand("fmcwarp").setExecutor(new CommandListener());
        getLogger().info("The plugin is now enabled!");
    }

    @Override
    public void onDisable() {
        if(!wm.lastpos.isEmpty()){
            for(Player player : wm.lastpos.keySet()){
                player.getInventory().setItem(8,null);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                player.teleport(wm.lastpos.remove(player));
            }
        }
        getLogger().info("The plugin is now disabled!");
        wm.saveWarps();
        gm.saveInteracts();
    }

    private void checkCitizens(){
        Plugin pl = plugin.getServer().getPluginManager().getPlugin("Citizens");
        if (pl == null) {
            return;
        }
        citizens = (pl instanceof Citizens);
    }

    public static boolean hasCitizens() {
        return citizens;
    }

    public static FMCWarpPlugin getPlugin() {
        return plugin;
    }

    public static WarpManager getWarpManager() {
        return wm;
    }

    public static GuiManager getGuiManager() {
        return gm;
    }

    public static String c(String msg){
        return ChatColor.translateAlternateColorCodes('&',msg);
    }
}
