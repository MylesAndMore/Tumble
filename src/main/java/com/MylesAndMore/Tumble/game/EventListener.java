package com.MylesAndMore.Tumble.game;

import java.util.Objects;

import com.MylesAndMore.Tumble.plugin.GameState;
import com.MylesAndMore.Tumble.plugin.GameType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.MylesAndMore.Tumble.Main.configManager;
import static com.MylesAndMore.Tumble.Main.plugin;

/**
 * Tumble event listener for all plugin and game-related events.
 */
public class EventListener implements Listener {

    World gameWorld;
    Game game;
    public EventListener(Game game) {
        this.game = game;
        this.gameWorld = game.arena.gameSpawn.getWorld();
    }
    
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        // Hide/show join message accordingly
        if (configManager.HideLeaveJoin) {
            event.setJoinMessage(null);
        }
        if (event.getPlayer().getWorld() == gameWorld) {
            // Send the player back to the lobby if they try to join in the middle of a game
            event.getPlayer().teleport(Objects.requireNonNull(game.arena.lobby));
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        // Hide/show leave message accordingly
        if (configManager.HideLeaveJoin) {
            event.setQuitMessage(null);
        }
        if (event.getPlayer().getWorld() == gameWorld) {
            event.getPlayer().teleport(game.arena.lobby);
            game.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        if (game.gamePlayers.contains(event.getEntity())) {
            game.playerDeath(event.getEntity());
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> event.getEntity().spigot().respawn(), 10);
        }
    }

    @EventHandler
    public void PlayerItemDamageEvent(PlayerItemDamageEvent event) {
        // Remove item damage within games
        if (event.getPlayer().getWorld() == gameWorld) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (game.roundType != GameType.SNOWBALLS) { return; }
        if (event.getEntity().getWorld() == gameWorld
                && event.getEntity() instanceof Snowball
                && event.getEntity().getShooter() instanceof Player player) {

            // Prevent projectiles (snowballs) from being thrown before the game starts
            if (Objects.equals(game.gameState, GameState.STARTING)) {
                event.setCancelled(true);
            }
            else {
                // Give players a snowball when they've used one (infinite snowballs)
                Bukkit.getServer().getScheduler().runTask(plugin, () -> player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1)));
            }
        }
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event) {
        if (event.getHitBlock() == null || game.roundType != GameType.SNOWBALLS) { return; }
        // Removes blocks that snowballs thrown by players have hit in the game world
        if (event.getHitBlock().getWorld() == gameWorld) {
            if (event.getEntity() instanceof Snowball) {
                if (event.getEntity().getShooter() instanceof Player p) {
                    if (event.getHitBlock() != null) {
                        if (event.getHitBlock().getLocation().distanceSquared(Objects.requireNonNull(game.arena.gameSpawn)) < 579) {
                            p.playEffect(
                                event.getHitBlock().getLocation(),
                                Effect.STEP_SOUND,
                                event.getHitBlock().getType());
                            event.getHitBlock().setType(Material.AIR);
                        }
                    }
                    else if (event.getHitEntity() != null) {
                        if (event.getHitEntity() instanceof Player hitPlayer) {
                            // Also cancel any knockback
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> hitPlayer.setVelocity(new Vector()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        // Don't allow items to drop in the game world
        if (event.getPlayer().getWorld() == gameWorld) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        // Cancel movement if the game is starting (so players can't move before the game starts)
        if (Objects.equals(game.gameState, GameState.STARTING)
                && event.getPlayer().getWorld().equals(gameWorld)
                && !equalPosition(event.getFrom(),event.getTo())) {
            event.setCancelled(true);
        }
        // kill player if they are below a Y level
        if (event.getPlayer().getWorld().equals(gameWorld) && game.arena.killAtY != null) {
            if (event.getPlayer().getLocation().getY() <= game.arena.killAtY) {
                event.getPlayer().setHealth(0);
            }
        }
    }

    @EventHandler
    public void BlockDropItemEvent(BlockDropItemEvent event) {
        // If a block was going to drop an item (ex. snow dropping snowballs) in the game world, cancel it
        if (event.getBlock().getWorld() == gameWorld) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
//        if (game.roundType != GameType.SHOVELS) {return;}
        // Remove blocks when clicked in the game world (all gamemodes require this functionality)
        if (event.getAction() == Action.LEFT_CLICK_BLOCK
                && Objects.requireNonNull(event.getClickedBlock()).getWorld() == gameWorld) {
            event.getPlayer().playEffect(
                    event.getClickedBlock().getLocation(),
                    Effect.STEP_SOUND,
                    event.getClickedBlock().getType()
            );
            event.getClickedBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        // This just doesn't allow blocks to break in the gameWorld; the PlayerInteractEvent will take care of everything
        // This prevents any weird client-server desync
        if (event.getBlock().getWorld() == gameWorld) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void FoodLevelChangeEvent(FoodLevelChangeEvent event) {
        // INFINITE FOOD (YAY!!!!)
        if (event.getEntity().getWorld() == gameWorld) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        // Check to see if a player got damaged by another entity (player, snowball, etc) in the gameWorld, if so, cancel it
        if (event.getEntity().getWorld() == gameWorld) {
            if (event.getEntity() instanceof Player) {
                if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                        || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                        || event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void InventoryDragEvent(InventoryDragEvent event) {
        if (event.getWhoClicked().getWorld() == gameWorld) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerRespwanEvent(PlayerRespawnEvent event) {
        // Make sure players respawn in the correct location
        if (game.gamePlayers.contains(event.getPlayer())) {
            event.setRespawnLocation(game.arena.gameSpawn);
        }
    }

    // TODO: stop tile drops for pistons, stop player from getting stuck in the waiting area after they leave

    public static boolean equalPosition(Location l1, @Nullable Location l2) {
        if (l2 == null) {
            return true;
        }
        return  (l1.getX() == l2.getX()) &&
                (l1.getY() == l2.getY()) &&
                (l1.getZ() == l2.getZ());
    }

}