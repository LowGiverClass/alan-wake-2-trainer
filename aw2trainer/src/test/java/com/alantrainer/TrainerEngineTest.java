package com.alantrainer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrainerEngine using a mock MemoryManager.
 * These tests verify that the trainer logic works correctly without a real process.
 */
class TrainerEngineTest {

    private TrainerEngine trainer;
    private MockMemoryManager mockMemory;

    @BeforeEach
    void setUp() {
        mockMemory = new MockMemoryManager();
        trainer = new TrainerEngine(mockMemory);
    }

    @AfterEach
    void tearDown() {
        trainer.shutdown();
    }

    @Test
    void testSetAndGetHealth() {
        trainer.setHealth(75);
        assertEquals(75, trainer.getHealth(), "Health should be 75 after setting.");
    }

    @Test
    void testSetAndGetAmmo() {
        trainer.setAmmo(999);
        assertEquals(999, trainer.getAmmo(), "Ammo should be 999 after setting.");
    }

    @Test
    void testSetAndGetFlashlightEnergy() {
        trainer.setFlashlightEnergy(50);
        assertEquals(50, trainer.getFlashlightEnergy(), "Flashlight energy should be 50 after setting.");
    }

    @Test
    void testHealthClampedToZero() {
        trainer.setHealth(-10);
        assertEquals(0, trainer.getHealth(), "Health should be clamped to 0.");
    }

    @Test
    void testAmmoClampedToZero() {
        trainer.setAmmo(-5);
        assertEquals(0, trainer.getAmmo(), "Ammo should be clamped to 0.");
    }

    /**
     * A simple mock that stores values in a map to simulate memory.
     */
    private static class MockMemoryManager extends MemoryManager {
        private final java.util.HashMap<Long, Integer> memory = new java.util.HashMap<>();

        // Dummy constructor to bypass real process opening
        public MockMemoryManager() {
            super(0); // Will not actually open process
        }

        @Override
        public int readInt(long address) {
            return memory.getOrDefault(address, 0);
        }

        @Override
        public void writeInt(long address, int value) {
            memory.put(address, value);
        }

        @Override
        public void close() {
            // No-op for mock
        }
    }
}
