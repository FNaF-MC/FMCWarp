package hu.Pdani.FMCWarp.listener;

import hu.Pdani.FMCWarp.FMCWarpPlugin;
import hu.Pdani.FMCWarp.manager.GuiManager;
import hu.Pdani.FMCWarp.manager.WarpManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.List;

public class BlockListener implements Listener {
    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        GuiManager gm = FMCWarpPlugin.getGuiManager();
        WarpManager wm = FMCWarpPlugin.getWarpManager();
        Player player = event.getPlayer();
        if(wm.lastpos.containsKey(player)){
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        if(gm.isGuiBlock(block.getLocation())){
            event.setCancelled(true);
            //return;
        }
        /*final BlockFace[] directions = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
        for (BlockFace blockFace : directions) {
            Block rel = block.getRelative(blockFace);
            if(gm.isGuiBlock(rel.getLocation())){
                event.setCancelled(true);
            }
        }*/
    }
    @EventHandler
    public void blockPlace(BlockPlaceEvent event){
        WarpManager wm = FMCWarpPlugin.getWarpManager();
        Player player = event.getPlayer();
        if(wm.lastpos.containsKey(player)){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void pistonExtendBlock(BlockPistonExtendEvent event){
        GuiManager gm = FMCWarpPlugin.getGuiManager();
        List<Block> blocks = event.getBlocks();
        for(Block b : blocks){
            if(gm.isGuiBlock(b.getLocation())){
                event.setCancelled(true);
                break;
            }
        }
    }
    @EventHandler
    public void pistonRetractBlock(BlockPistonRetractEvent event){
        GuiManager gm = FMCWarpPlugin.getGuiManager();
        List<Block> blocks = event.getBlocks();
        for(Block b : blocks){
            if(gm.isGuiBlock(b.getLocation())){
                event.setCancelled(true);
                break;
            }
        }
    }
}
