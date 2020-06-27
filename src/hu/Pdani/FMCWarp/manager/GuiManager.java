package hu.Pdani.FMCWarp.manager;

import hu.Pdani.FMCWarp.FMCWarpPlugin;
import hu.Pdani.FMCWarp.FWHolder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static hu.Pdani.FMCWarp.FMCWarpPlugin.c;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuiManager {
    public List<Player> setuplist = new ArrayList<>();
    private HashMap<UUID,Location> blocklist = new HashMap<>();
    private HashMap<UUID,Integer> entitylist = new HashMap<>();
    private File guiFile;
    private FileConfiguration guis;
    public GuiManager() {
        reloadInteracts();
        guis.options().header(" Do not modify this file manually !!!");
        guis.options().copyHeader(true);
    }

    public void saveInteracts(){
        guis.options().header(" Do not modify this file manually !!!");
        guis.options().copyHeader(true);
        try {
            guis.save(guiFile);
        } catch (IOException e) {
            FMCWarpPlugin.getPlugin().getLogger().severe("Error saving interacts.yml file: "+e.getMessage());
        }
    }

    public void loadInteracts(){
        guiFile = new File(FMCWarpPlugin.getPlugin().getDataFolder(),"interacts.yml");
        guis = YamlConfiguration.loadConfiguration(guiFile);
    }

    public void reloadInteracts(){
        FMCWarpPlugin plugin = FMCWarpPlugin.getPlugin();
        loadInteracts();
        blocklist.clear();
        entitylist.clear();
        ConfigurationSection blocks = guis.getConfigurationSection("blocks");
        ConfigurationSection entities = guis.getConfigurationSection("entities");
        if(blocks == null || entities == null)
            return;
        for(String key : blocks.getKeys(false)){
            double x,y,z;
            String sw = guis.getString("blocks."+key+".world");
            World world = plugin.getServer().getWorld(sw);
            if(world == null){
                plugin.getLogger().warning("World '"+sw+"' does not exists!");
                continue;
            }
            x = guis.getDouble("blocks."+key+".x");
            y = guis.getDouble("blocks."+key+".y");
            z = guis.getDouble("blocks."+key+".z");
            Location loc = new Location(world,x,y,z);
            blocklist.put(UUID.fromString(key),loc);
        }
        for(String key : entities.getKeys(false)){
            int id = guis.getInt("entities."+key+".npc");
            entitylist.put(UUID.fromString(key),id);
        }
    }

    public void addGuiBlock(Location loc) throws Exception {
        if(isGuiBlock(loc))
            throw new Exception("This block is already registered!");
        UUID uuid = UUID.randomUUID();
        blocklist.put(uuid,loc);
        guis.set("blocks."+uuid.toString()+".world",loc.getWorld().getName());
        guis.set("blocks."+uuid.toString()+".x",loc.getX());
        guis.set("blocks."+uuid.toString()+".y",loc.getY());
        guis.set("blocks."+uuid.toString()+".z",loc.getZ());
        saveInteracts();
    }

    public void delGuiBlock(Location loc) throws Exception {
        if(!isGuiBlock(loc))
            throw new Exception("This block is NOT registered!");
        boolean found = false;
        for(UUID k : blocklist.keySet()){
            if(blocklist.get(k).equals(loc)){
                found = true;
                FMCWarpPlugin.getPlugin().getLogger().info("Found: "+k);
                blocklist.remove(k);
                guis.set("blocks."+k.toString(),null);
                saveInteracts();
                break;
            }
        }
        if(!found)
            throw new Exception("Block not found!");
    }

    public void addGuiNpc(int id) throws Exception {
        if(isGuiNpc(id))
            throw new Exception("This NPC is already registered!");
        UUID uuid = UUID.randomUUID();
        entitylist.put(uuid,id);
        guis.set("entities."+uuid.toString()+".npc",id);
        saveInteracts();
    }

    public void delGuiNpc(int id) throws Exception {
        if(!isGuiNpc(id))
            throw new Exception("This NPC is NOT registered!");
        for(UUID k : entitylist.keySet()){
            if(entitylist.get(k) == id){
                entitylist.remove(k);
                guis.set("entities."+k.toString(),null);
                saveInteracts();
                break;
            }
        }
    }

    public boolean isGuiBlock(Location loc) {
        for(UUID k : blocklist.keySet()) {
            if (blocklist.get(k).equals(loc)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGuiNpc(int id){
        return entitylist.containsValue(id);
    }

    public void openGui(Player target){
        WarpManager wm = FMCWarpPlugin.getWarpManager();
        if(wm.lastpos.containsKey(target))
            return;
        FMCWarpPlugin plugin = FMCWarpPlugin.getPlugin();
        int slots = (int) Math.ceil(wm.getWarplist().size()/9.0);
        if(slots == 0){
            slots = 1;
        }
        int p = 0;
        Inventory myInv = plugin.getServer().createInventory(new FWHolder(), slots*9, c(plugin.getConfig().getString("gui.title")));
        for(String k : wm.getWarplist().keySet()){
            Location v = wm.getWarpLocation(k);
            Material type = Material.SIGN;
            Block down = v.getBlock().getRelative(BlockFace.DOWN);
            if(down != null && down.getType() != Material.AIR){
                type = down.getType();
            }
            ItemStack item = new ItemStack(type);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(c("&a"+k));
            item.setItemMeta(meta);
            myInv.setItem(p,item);
            p++;
        }
        target.openInventory(myInv);
    }
}
