package com.alantrainer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core trainer engine that manages memory addresses and provides high-level cheats
 * for Alan Wake 2: health, ammo, and flashlight energy.
 */
public class TrainerEngine {

    private static final Logger LOGGER = Logger.getLogger(TrainerEngine.class.getName());

    // Known static memory offsets for Alan Wake 2 (example values, not real)
    // In a real project, these would be discovered via pointer scanning.
    private static final long HEALTH_OFFSET = 0x00A3F1B0;
    private static final long AMMO_OFFSET = 0x00B4C2D0;
    private static final long FLASHLIGHT_OFFSET = 0x00C5E3F0;

    private final MemoryManager memoryManager;
    private final Map<String, Long> addressCache;

    /**
     * Initializes the trainer engine with a MemoryManager instance.
     *
     * @param memoryManager An open MemoryManager for the Alan Wake 2 process.
     */
    public TrainerEngine(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        this.addressCache = new HashMap<>();
        cacheAddresses();
    }

    /**
     * Pre-caches memory addresses for faster access.
     */
    private void cacheAddresses() {
        addressCache.put("health", HEALTH_OFFSET);
        addressCache.put("ammo", AMMO_OFFSET);
        addressCache.put("flashlight", FLASHLIGHT_OFFSET);
        LOGGER.info("Addresses cached successfully.");
    }

    /**
     * Sets the player's health to the specified value.
     *
     * @param health New health value (typical max: 100).
     */
    public void setHealth(int health) {
        long addr = addressCache.get("health");
        memoryManager.writeInt(addr, Math.max(0, health));
        LOGGER.log(Level.INFO, "Health set to {0}", health);
    }

    /**
     * Gets the current player health.
     *
     * @return Current health value.
     */
    public int getHealth() {
        long addr = addressCache.get("health");
        return memoryManager.readInt(addr);
    }

    /**
     * Sets the player's ammo count.
     *
     * @param ammo New ammo count (typical max: 999).
     */
    public void setAmmo(int ammo) {
        long addr = addressCache.get("ammo");
        memoryManager.writeInt(addr, Math.max(0, ammo));
        LOGGER.log(Level.INFO, "Ammo set to {0}", ammo);
    }

    /**
     * Gets the current ammo count.
     *
     * @return Current ammo value.
     */
    public int getAmmo() {
        long addr = addressCache.get("ammo");
        return memoryManager.readInt(addr);
    }

    /**
     * Sets the flashlight energy level.
     *
     * @param energy New flashlight energy (typical max: 100).
     */
    public void setFlashlightEnergy(int energy) {
        long addr = addressCache.get("flashlight");
        memoryManager.writeInt(addr, Math.max(0, energy));
        LOGGER.log(Level.INFO, "Flashlight energy set to {0}", energy);
    }

    /**
     * Gets the current flashlight energy.
     *
     * @return Current flashlight energy value.
     */
    public int getFlashlightEnergy() {
        long addr = addressCache.get("flashlight");
        return memoryManager.readInt(addr);
    }

    /**
     * Releases the memory manager resources.
     */
    public void shutdown() {
        memoryManager.close();
        LOGGER.info("Trainer engine shut down.");
    }
}
