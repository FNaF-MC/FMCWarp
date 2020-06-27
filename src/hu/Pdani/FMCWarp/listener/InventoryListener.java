package hu.Pdani.FMCWarp.listener;

import hu.Pdani.FMCWarp.FMCWarpPlugin;
import hu.Pdani.FMCWarp.FWHolder;
import hu.Pdani.FMCWarp.manager.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static hu.Pdani.FMCWarp.FMCWarpPlugin.c;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // The player that clicked the item
        ItemStack clicked = event.getCurrentItem(); // The item that was clicked
        int slot = event.getRawSlot();
        Inventory inventory = event.getInventory(); // The inventory that was clicked in
        WarpManager wm = FMCWarpPlugin.getWarpManager();
        if(inventory.getHolder() instanceof FWHolder){
            if(slot > inventory.getSize()-1){
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            if(clicked == null || clicked.getType() == Material.AIR)
                return;
            String warp = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            if(!wm.isWarp(warp))
                return;
            wm.lastpos.put(player,player.getLocation());
            Location loc = wm.getWarpLocation(warp);
            player.closeInventory();
            player.teleport(loc);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE,0),true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0),true);
            ItemStack item = new ItemStack(Material.IRON_DOOR);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(c("&cExit"));
            List<String> lore = new ArrayList<>();
            lore.add("fmcwarp_exit");
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            if(!FMCWarpPlugin.getPlugin().getConfig().getBoolean("gui.disableexit",false))
                player.getInventory().setItem(8,item);
        } else {
            if(wm.lastpos.containsKey(player)){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void itemDrop(PlayerDropItemEvent event){
        WarpManager wm = FMCWarpPlugin.getWarpManager();
        if(wm.lastpos.containsKey(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}
