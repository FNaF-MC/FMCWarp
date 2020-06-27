package hu.Pdani.FMCWarp.manager;

import hu.Pdani.FMCWarp.FMCWarpPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class WarpManager {
    private HashMap<String, Location> warplist = new HashMap<>();
    public HashMap<Player, Location> lastpos = new HashMap<>();
    private File warpFile;
    private FileConfiguration warps;
    public WarpManager() {
        reloadWarps();
        warps.options().header(" Do not modify this file manually !!!");
        warps.options().copyHeader(true);
    }

    public void saveWarps(){
        warps.options().header(" Do not modify this file manually !!!");
        warps.options().copyHeader(true);
        try {
            warps.save(warpFile);
        } catch (IOException e) {
            FMCWarpPlugin.getPlugin().getLogger().severe("Error saving warps.yml file: "+e.getMessage());
        }
    }

    public void loadWarps(){
        warpFile = new File(FMCWarpPlugin.getPlugin().getDataFolder(),"warps.yml");
        warps = YamlConfiguration.loadConfiguration(warpFile);
    }

    public void reloadWarps(){
        FMCWarpPlugin plugin = FMCWarpPlugin.getPlugin();
        loadWarps();
        warplist.clear();
        ConfigurationSection def = warps.getRoot();
        if(def == null) {
            FMCWarpPlugin.getPlugin().getLogger().warning("def is null");
            return;
        }
        for(String key : def.getKeys(false)){
            double x,y,z;
            float yaw,pitch;
            String sw = warps.getString(key+".world");
            World world = plugin.getServer().getWorld(sw);
            if(world == null){
                plugin.getLogger().warning("World '"+sw+"' does not exists!");
                continue;
            }
            x = warps.getDouble(key+".x");
            y = warps.getDouble(key+".y");
            z = warps.getDouble(key+".z");
            yaw = warps.getLong(key+".yaw");
            pitch = warps.getLong(key+".pitch");
            Location loc = new Location(world,x,y,z,yaw,pitch);
            warplist.put(key,loc);
        }
    }

    public void addWarp(String name, Location loc) {
        String key = name.toLowerCase();
        if(isWarp(key)){
            return;
        }
        warplist.put(key,loc);
        warps.set(key+".world",loc.getWorld().getName());
        warps.set(key+".x",loc.getX());
        warps.set(key+".y",loc.getY());
        warps.set(key+".z",loc.getZ());
        warps.set(key+".yaw",loc.getYaw());
        warps.set(key+".pitch",loc.getPitch());
        saveWarps();
    }

    public void delWarp(String name) {
        String key = name.toLowerCase();
        if(!isWarp(key)){
            return;
        }
        warplist.remove(key);
        warps.set(key,null);
        saveWarps();
    }

    public boolean isWarp(String name){
        return warplist.containsKey(name.toLowerCase());
    }

    public HashMap<String, Location> getWarplist() {
        return warplist;
    }

    public Location getWarpLocation(String warp) {
        return warplist.get(warp);
    }
}
