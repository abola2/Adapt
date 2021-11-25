package com.volmit.adapt.api.skill;

import com.volmit.adapt.api.tick.TickedObject;
import com.volmit.adapt.api.world.AdaptPlayer;
import com.volmit.adapt.api.world.AdaptRecipe;
import com.volmit.adapt.api.world.PlayerSkillLine;
import com.volmit.adapt.api.xp.XPMultiplier;
import com.volmit.adapt.content.gui.SkillsGui;
import com.volmit.adapt.content.skill.SkillAgility;
import com.volmit.adapt.content.skill.SkillArchitect;
import com.volmit.adapt.content.skill.SkillAxes;
import com.volmit.adapt.content.skill.SkillCrafting;
import com.volmit.adapt.content.skill.SkillDiscovery;
import com.volmit.adapt.content.skill.SkillEnchanting;
import com.volmit.adapt.content.skill.SkillHerbalism;
import com.volmit.adapt.content.skill.SkillHunter;
import com.volmit.adapt.content.skill.SkillPickaxes;
import com.volmit.adapt.content.skill.SkillRanged;
import com.volmit.adapt.content.skill.SkillRift;
import com.volmit.adapt.content.skill.SkillSeaborne;
import com.volmit.adapt.content.skill.SkillStealth;
import com.volmit.adapt.content.skill.SkillSwords;
import com.volmit.adapt.content.skill.SkillTaming;
import com.volmit.adapt.content.skill.SkillUnarmed;
import com.volmit.adapt.util.C;
import com.volmit.adapt.util.Form;
import com.volmit.adapt.util.KList;
import com.volmit.adapt.util.KMap;
import com.volmit.adapt.util.M;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class SkillRegistry extends TickedObject {
    private final KMap<String, Skill<?>> skills = new KMap<>();

    public SkillRegistry() throws IOException {
        super("registry", UUID.randomUUID() + "-sk", 1250);
        registerSkill(SkillAgility.class);
        registerSkill(SkillArchitect.class);
        registerSkill(SkillAxes.class);
        registerSkill(SkillCrafting.class);
        registerSkill(SkillDiscovery.class);
        registerSkill(SkillEnchanting.class);
        registerSkill(SkillHerbalism.class);
        registerSkill(SkillHunter.class);
        registerSkill(SkillPickaxes.class);
        registerSkill(SkillRanged.class);
        registerSkill(SkillRift.class);
        registerSkill(SkillSeaborne.class);
        registerSkill(SkillStealth.class);
        registerSkill(SkillSwords.class);
        registerSkill(SkillTaming.class);
        registerSkill(SkillUnarmed.class);
    }

    @EventHandler
    public void on(PlayerExpChangeEvent e) {
        if(e.getAmount() > 0) {
            getPlayer(e.getPlayer()).boostXPToRecents(getPlayer(e.getPlayer()), 0.03, 10000);
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent e) {
        if(!e.getBlockFace().equals(BlockFace.UP) && !e.getBlockFace().equals(BlockFace.DOWN) && !e.getPlayer().isSneaking() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.BOOKSHELF) && (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR) || !e.getPlayer().getInventory().getItemInMainHand().getType().isBlock()) &&
            (e.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.AIR) || !e.getPlayer().getInventory().getItemInOffHand().getType().isBlock())) {
            e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.1f, 0.72f);
            e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.35f, 0.755f);
            SkillsGui.open(e.getPlayer());
            e.getPlayer().getWorld().spawnParticle(Particle.CRIT_MAGIC, e.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5), 25, 0, 0, 0, 1.1);
            e.getPlayer().getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, e.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5), 12, 0, 0, 0, 1.1);
        }

        if(e.getPlayer().isSneaking() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.LECTERN)) {
            ItemStack it = e.getPlayer().getInventory().getItemInMainHand();

            if(it.getItemMeta() != null && !it.getItemMeta().getPersistentDataContainer().getKeys().isEmpty()) {
                e.setCancelled(true);
                playDebug(e.getPlayer());
                it.getItemMeta().getPersistentDataContainer().getKeys().forEach(k -> Bukkit.getServer().getConsoleSender().sendMessage(k + " = " + it.getItemMeta().getPersistentDataContainer().getOrDefault(k, PersistentDataType.STRING, "Not a String")));
            }
        }

        if(e.getPlayer().isSneaking() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.OBSERVER)) {
            ItemStack it = e.getPlayer().getInventory().getItemInMainHand();

            if(it.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                e.setCancelled(true);
                Bukkit.getServer().getConsoleSender().sendMessage("   ");
                e.getPlayer().setCooldown(Material.ENCHANTED_BOOK, 3);
                AdaptPlayer a = getPlayer(e.getPlayer());
                playDebug(e.getPlayer());

                String xv = a.getData().getMultiplier() - 1d > 0 ? "+" + Form.pc(a.getData().getMultiplier() - 1D) : Form.pc(a.getData().getMultiplier() - 1D);
                Bukkit.getServer().getConsoleSender().sendMessage("Global" + C.GRAY + ": " + C.GREEN + xv);

                for(XPMultiplier i : a.getData().getMultipliers()) {
                    String vv = i.getMultiplier() > 0 ? "+" + Form.pc(i.getMultiplier()) : Form.pc(i.getMultiplier());
                    Bukkit.getServer().getConsoleSender().sendMessage(C.GREEN + "* " + vv + C.GRAY + " for " + Form.duration(i.getGoodFor() - M.ms(), 0));
                }

                for(PlayerSkillLine i : a.getData().getSkillLines().v()) {
                    Skill<?> s = i.getRawSkill(a);
                    String v = i.getMultiplier() - a.getData().getMultiplier() > 0 ? "+" + Form.pc(i.getMultiplier() - a.getData().getMultiplier()) : Form.pc(i.getMultiplier() - a.getData().getMultiplier());
                    Bukkit.getServer().getConsoleSender().sendMessage("  " + s.getDisplayName() + C.GRAY + ": " + s.getColor() + v);

                    for(XPMultiplier j : i.getMultipliers()) {
                        String vv = j.getMultiplier() > 0 ? "+" + Form.pc(j.getMultiplier()) : Form.pc(j.getMultiplier());
                        Bukkit.getServer().getConsoleSender().sendMessage("  " + s.getShortName() + C.GRAY + " " + vv + " for " + Form.duration(j.getGoodFor() - M.ms(), 0));
                    }
                }
            }
        }
    }

    private void playDebug(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_BELL_RESONATE, 1f, 0.6f);
        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.1f);
        p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1.6f);
        p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1.2f);

    }

    public Skill<?> getSkill(String i) {
        return skills.get(i);
    }

    public KList<Skill<?>> getSkills() {
        return skills.v();
    }

    public void registerSkill(Class<? extends Skill<?>> skill) {
        try {
            Skill<?> sk = skill.getConstructor().newInstance();

            if(!sk.isEnabled())
            {
                return;
            }

            skills.put(sk.getName(), sk);
            registerRecipes(sk);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void unregisterRecipes(Skill<?> s) {
        s.getRecipes().forEach(AdaptRecipe::unregister);
        s.getAdaptations().forEach(i -> i.getRecipes().forEach(AdaptRecipe::unregister));
    }

    private void registerRecipes(Skill<?> s) {
        s.getRecipes().forEach(AdaptRecipe::register);
        s.getAdaptations().forEach(i -> i.getRecipes().forEach(AdaptRecipe::register));
    }

    @Override
    public void unregister() {
        for(Skill<?> i : skills.v()) {
            i.unregister();
            unregisterRecipes(i);
        }
    }

    @Override
    public void onTick() {

    }
}
