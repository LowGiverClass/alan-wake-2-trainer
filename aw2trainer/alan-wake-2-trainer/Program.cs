using System;
using System.Diagnostics;
using System.Threading;
using AlanWake2Trainer.Memory;

namespace AlanWake2Trainer
{
    /// <summary>
    /// Entry point for the Alan Wake 2 Trainer application.
    /// Provides real-time cheat functionalities via memory manipulation.
    /// </summary>
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Alan Wake 2 Trainer v1.0");
            Console.WriteLine("Waiting for Alan Wake 2 process...");

            // Wait for the game process to start
            Process? gameProcess = null;
            while (gameProcess == null)
            {
                var processes = Process.GetProcessesByName("AlanWake2");
                if (processes.Length > 0)
                {
                    gameProcess = processes[0];
                }
                else
                {
                    Thread.Sleep(1000);
                }
            }

            Console.WriteLine($"Attached to process: {gameProcess.Id}");

            // Initialize memory manager
            var memoryManager = new GameMemoryManager(gameProcess);
            var trainer = new Trainer(memoryManager);

            // Start the trainer loop
            trainer.Run();
        }
    }
}
