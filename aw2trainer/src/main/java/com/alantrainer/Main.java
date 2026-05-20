package com.alantrainer;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for the Alan Wake 2 Trainer.
 * Provides a simple console interface to apply cheats.
 * Usage: java -jar alan-wake-2-trainer.jar <processId>
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar alan-wake-2-trainer.jar <processId>");
            System.exit(1);
        }

        int processId;
        try {
            processId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid process ID: " + args[0]);
            return;
        }

        LOGGER.log(Level.INFO, "Attaching to process {0}...", processId);
        MemoryManager memoryManager = new MemoryManager(processId);
        TrainerEngine trainer = new TrainerEngine(memoryManager);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Alan Wake 2 Trainer active.");
            System.out.println("Commands: health <value>, ammo <value>, flashlight <value>, exit");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                String[] parts = input.split("\\s+");
                if (parts.length < 2) {
                    System.out.println("Invalid command. Use: <command> <value>");
                    continue;
                }

                try {
                    int value = Integer.parseInt(parts[1]);
                    switch (parts[0].toLowerCase()) {
                        case "health":
                            trainer.setHealth(value);
                            System.out.println("Health set to " + value);
                            break;
                        case "ammo":
                            trainer.setAmmo(value);
                            System.out.println("Ammo set to " + value);
                            break;
                        case "flashlight":
                            trainer.setFlashlightEnergy(value);
                            System.out.println("Flashlight energy set to " + value);
                            break;
                        default:
                            System.out.println("Unknown command: " + parts[0]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number: " + parts[1]);
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "Memory operation failed: " + e.getMessage());
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } finally {
            trainer.shutdown();
            System.out.println("Trainer terminated.");
        }
    }
}
