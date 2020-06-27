package hu.Pdani.FMCWarp.listener;

import hu.Pdani.FMCWarp.FMCWarpPlugin;
import hu.Pdani.FMCWarp.manager.GuiManager;
import hu.Pdani.FMCWarp.manager.WarpManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import static hu.Pdani.FMCWarp.FMCWarpPlugin.c;

public class PlayerListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        WarpManager wm = FMCWarpPlugin.getWarpManager();
        if(!wm.lastpos.containsKey(event.getPlayer())){
            return;
        }
        Location f = event.getFrom();
        Location t = event.getTo();
        if(f.getBlockX() != t.getBlockX() || f.getBlockZ() != t.getBlockZ() || f.getBlockY() != t.getBlockY()){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void blockInteract(PlayerInteractEvent event){
        if(event.getHand() == null || event.getHand() == EquipmentSlot.OFF_HAND)
            return;
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            if(block == null || block.getType() == Material.AIR)
                return;
            GuiManager gm = FMCWarpPlugin.getGuiManager();
            if(gm.setuplist.contains(player)){
                if(!player.hasPermission("fmcwarp.admin")) {
                    gm.setuplist.remove(player);
                    return;
                }
                String sb = block.getX() + "," +
                        block.getY() + "," +
                        block.getZ();
                try {
                    gm.addGuiBlock(block.getLocation());
                    player.sendMessage(c("&aNew interactable block registered! ("+sb+")"));
                } catch (Exception e) {
                    player.sendMessage(c("&c"+e.getMessage()));
                }
                gm.setuplist.remove(player);
                return;
            }
            ItemStack inHand = event.getItem();
            if(gm.isGuiBlock(block.getLocation())){
                if(!player.hasPermission("fmcwarp.use"))
                    return;
                if(inHand != null && inHand.getType() != Material.AIR)
                    event.setCancelled(true);
                gm.openGui(player);
            }
            if(inHand != null && inHand.getType() == Material.IRON_DOOR){
                if(inHand.getItemMeta().hasLore()) {
                    String line = inHand.getItemMeta().getLore().get(0);
                    if(line.equals("fmcwarp_exit")){
                        WarpManager wm = FMCWarpPlugin.getWarpManager();
                        event.setCancelled(true);
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(),null);
                        if(!wm.lastpos.containsKey(player))
                            return;
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        player.teleport(wm.lastpos.remove(player));
                    }
                }
            }
        } else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            if(block == null || block.getType() == Material.AIR)
                return;
            GuiManager gm = FMCWarpPlugin.getGuiManager();
            if(gm.setuplist.contains(player)){
                if(!player.hasPermission("fmcwarp.admin")) {
                    gm.setuplist.remove(player);
                    return;
                }
                String sb = block.getX() + "," +
                        block.getY() + "," +
                        block.getZ();
                try {
                    gm.delGuiBlock(block.getLocation());
                    player.sendMessage(c("&aInteractable block removed! ("+sb+")"));
                } catch (Exception e) {
                    player.sendMessage(c("&c"+e.getMessage()));
                }
                event.setCancelled(true);
                gm.setuplist.remove(player);
            }
        } else if(event.getAction() == Action.RIGHT_CLICK_AIR){
            ItemStack inHand = event.getItem();
            if(inHand != null && inHand.getType() == Material.IRON_DOOR){
                if(inHand.getItemMeta().hasLore()) {
                    String line = inHand.getItemMeta().getLore().get(0);
                    if(line.equals("fmcwarp_exit")){
                        WarpManager wm = FMCWarpPlugin.getWarpManager();
                        event.setCancelled(true);
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(),null);
                        if(!wm.lastpos.containsKey(player))
                            return;
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        player.teleport(wm.lastpos.remove(player));
                    }
                }
            }
        }
    }
    @EventHandler
    public void entityRightClick(PlayerInteractEntityEvent event){
        if(event.getHand() == null || event.getHand() == EquipmentSlot.OFF_HAND)
            return;
        if(!FMCWarpPlugin.hasCitizens()){
            return;
        }
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        NPC target = CitizensAPI.getNPCRegistry().getNPC(entity);
        if(target != null){
            GuiManager gm = FMCWarpPlugin.getGuiManager();
            if(gm.setuplist.contains(player)){
                if(!player.hasPermission("fmcwarp.admin")) {
                    gm.setuplist.remove(player);
                    return;
                }
                try {
                    gm.addGuiNpc(target.getId());
                    player.sendMessage(c("&aNew interactable NPC ("+target.getId()+") registered!"));
                } catch (Exception e) {
                    player.sendMessage(c("&c"+e.getMessage()));
                }
                gm.setuplist.remove(player);
                return;
            }
            if(gm.isGuiNpc(target.getId())){
                if(!player.hasPermission("fmcwarp.use"))
                    return;
                gm.openGui(player);
            }
        }
    }
    @EventHandler
    public void npcLeftClick(EntityDamageByEntityEvent event){
        if(!FMCWarpPlugin.hasCitizens()){
            return;
        }
        if(event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();
            Entity entity = event.getEntity();
            NPC target = CitizensAPI.getNPCRegistry().getNPC(entity);
            if(target != null){
                event.setCancelled(true);
                GuiManager gm = FMCWarpPlugin.getGuiManager();
                if(gm.setuplist.contains(player)){
                    if(!player.hasPermission("fmcwarp.admin")) {
                        gm.setuplist.remove(player);
                        return;
                    }
                    try {
                        gm.delGuiNpc(target.getId());
                        player.sendMessage(c("&aInteractable NPC ("+target.getId()+") removed!"));
                    } catch (Exception e) {
                        player.sendMessage(c("&c"+e.getMessage()));
                    }
                    gm.setuplist.remove(player);
                }
            }
        }
    }
}
