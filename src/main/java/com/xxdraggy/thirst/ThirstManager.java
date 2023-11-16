package com.xxdraggy.thirst;

import com.xxdraggy.datamanager.DataManager;
import com.xxdraggy.datamanager.YMLFile;
import com.xxdraggy.utils.Creator;
import com.xxdraggy.utils.builders.text.TextBuilder;
import com.xxdraggy.utils.data.attribute.AttributeOperation;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class ThirstManager {
    private static YMLFile config = DataManager.getYmlFile("config");
    private static Plugin plugin;

    public static Objective getThirst() {
        return Bukkit.getScoreboardManager().getMainScoreboard().getObjective("thirst_thirst_counter");
    }
    public static Objective getJoined() {
        return Bukkit.getScoreboardManager().getMainScoreboard().getObjective("thirst_joined_counter");
    }
    public static Objective getAir() {
        return Bukkit.getScoreboardManager().getMainScoreboard().getObjective("thirst_air_counter");
    }
    public static Objective getCounting() {
        return Bukkit.getScoreboardManager().getMainScoreboard().getObjective("thirst_counting_counter");
    }

    public static Void resetThirst(Player player) {
        ThirstManager.getThirst().getScore(player.getName()).setScore(20);

        return null;
    }
    public static Void waterDrank(Player player, boolean pure) {
        Score score = ThirstManager.getThirst().getScore(player.getName());

        if (!pure) {
            score.setScore(score.getScore() + config.getInt("drink"));

            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, config.getInt("hungerDuration") * 20, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, config.getInt("nauseaDuration") * 20, 1));
        }
        else {
            score.setScore(score.getScore() + config.getInt("drinkPure"));
        }

        if (score.getScore() > 20) {
            score.setScore(20);
        }

        return null;
    }
    public static Void registerPlayer(Player player) {
        ThirstManager.getThirst().getScore(player.getName()).setScore(20);

        ThirstManager.getJoined().getScore(player.getName()).setScore(1);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            if (!offlinePlayer.isOnline()) return;

            Player p = Bukkit.getPlayer(offlinePlayer.getUniqueId());
            if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
            if (ThirstManager.getCounting().getScore(p.getName()).getScore() == 0) return;

            int score = ThirstManager.getThirst().getScore(player.getName()).getScore();
            int result = score - config.getInt("thirst");

            if (result < 0) {
                result = 0;
            }

            ThirstManager.getThirst().getScore(player.getName()).setScore(result);
        }, config.getInt("thirstPeriod") * 20L, config.getInt("thirstPeriod") * 20L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            if (!offlinePlayer.isOnline()) return;

            Player p = Bukkit.getPlayer(offlinePlayer.getUniqueId());
            if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
            if (ThirstManager.getCounting().getScore(p.getName()).getScore() == 0) return;

            if (ThirstManager.getThirst().getScore(player.getName()).getScore() == 0) {
                p.damage(config.getDouble("damage"));
            }
        }, config.getInt("damagePeriod") * 20L, config.getInt("damagePeriod") * 20L);

        return null;
    }
    public static boolean isPlayerRegistered(Player player) {
        return ThirstManager.getJoined().getScore(player.getName()).getScore() == 1;
    }

    public static Void register() {
        if (ThirstManager.getThirst() == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(
                    "thirst_thirst_counter",
                    Criteria.DUMMY,
                    Component.text("thirst")
            );
        }
        if (ThirstManager.getJoined() == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(
                    "thirst_joined_counter",
                    Criteria.DUMMY,
                    Component.text("joined")
            );
        }
        if (ThirstManager.getAir() == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(
                    "thirst_air_counter",
                    Criteria.AIR,
                    Component.text("air")
            );
        }
        if (ThirstManager.getCounting() == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(
                    "thirst_counting_counter",
                    Criteria.DUMMY,
                    Component.text("counting")
            );
        }

        return null;
    }
    public static Void init(Plugin plugin) {
        ThirstManager.plugin = plugin;

        Bukkit.addRecipe(new FurnaceRecipe(
                new NamespacedKey(plugin, "furnace_recipe"),
                ThirstManager.getPureWater(),
                Material.POTION,
                10,
                config.getInt("cookingTime") * 20
        ));

        return null;
    }

    public static ItemStack getPureWater() {
        ItemStack potion = Creator.item()
                .setMaterial(Material.POTION)
                .addAttribute(Attribute.GENERIC_LUCK, 1, AttributeOperation.AddNumber)
                .setName(
                        Creator.text("Pure Water")
                                .bold()
                                .toString()
                )
                .build();

        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
        potionMeta.setColor(TextBuilder.hexColor("87DDFF").getBukkitObject());
        potionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        potion.setItemMeta(potionMeta);

        return potion;
    }
}
