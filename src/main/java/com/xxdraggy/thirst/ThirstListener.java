package com.xxdraggy.thirst;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ThirstListener implements Listener {
    public void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ThirstManager.getCounting().getScore(player.getName()).setScore(1);

        if(!ThirstManager.isPlayerRegistered(player)) {
            ThirstManager.registerPlayer(player);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        ThirstManager.resetThirst(event.getPlayer());

        ThirstManager.getCounting().getScore(event.getPlayer().getName()).setScore(1);
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        ThirstManager.getCounting().getScore(event.getPlayer().getName()).setScore(0);
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        PotionMeta potion = (PotionMeta) item.getItemMeta();

        if (!item.getType().equals(Material.POTION)) return;
        if (!potion.getBasePotionData().getType().equals(PotionType.WATER)) return;

        ThirstManager.waterDrank(event.getPlayer(), potion.getAttributeModifiers(Attribute.GENERIC_LUCK) != null);
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        ItemStack input = event.getSource();
        PotionMeta potion = (PotionMeta) input.getItemMeta();

        if (!input.getType().equals(Material.POTION)) return;

        if (!potion.getBasePotionData().getType().equals(PotionType.WATER)) event.setCancelled(true);
        if (potion.getAttributeModifiers(Attribute.GENERIC_LUCK) != null) event.setCancelled(true);
    }
}
