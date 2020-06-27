package hu.Pdani.FMCWarp.listener;

import hu.Pdani.FMCWarp.FMCWarpPlugin;
import hu.Pdani.FMCWarp.manager.GuiManager;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static hu.Pdani.FMCWarp.FMCWarpPlugin.c;

public class NPCListener implements Listener {
    @EventHandler
    public void npcRemove(NPCRemoveEvent event){
        int id = event.getNPC().getId();
        GuiManager gm = FMCWarpPlugin.getGuiManager();
        if(gm.isGuiNpc(id)) {
            try {
                gm.delGuiNpc(id);
                FMCWarpPlugin.getPlugin().getLogger().info("Interactable NPC ("+id+") removed!");
            } catch (Exception ignored) {}
        }
    }
}
