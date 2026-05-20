using System;
using System.Collections.Generic;
using System.Threading;
using AlanWake2Trainer.Memory;

namespace AlanWake2Trainer
{
    /// <summary>
    /// Core trainer logic providing cheats for Alan Wake 2.
    /// Features: Infinite health, infinite flashlight battery, and infinite ammo.
    /// </summary>
    public class Trainer
    {
        private readonly GameMemoryManager _memory;
        private bool _isRunning;
        private readonly Dictionary<string, bool> _cheatStates;

        // Memory addresses (example offsets - would need actual scanning)
        private readonly IntPtr _healthBase = (IntPtr)0x12345678;
        private readonly IntPtr _flashlightBase = (IntPtr)0x9ABCDEF0;
        private readonly IntPtr _ammoBase = (IntPtr)0x11223344;

        private readonly int[] _healthOffsets = { 0x10, 0x20, 0x30 };
        private readonly int[] _flashlightOffsets = { 0x40, 0x50 };
        private readonly int[] _ammoOffsets = { 0x60, 0x70, 0x80 };

        public Trainer(GameMemoryManager memory)
        {
            _memory = memory ?? throw new ArgumentNullException(nameof(memory));
            _cheatStates = new Dictionary<string, bool>
            {
                { "infinite_health", false },
                { "infinite_flashlight", false },
                { "infinite_ammo", false }
            };
        }

        /// <summary>
        /// Starts the trainer loop, listening for keyboard input and applying cheats.
        /// </summary>
        public void Run()
        {
            _isRunning = true;
            Console.WriteLine("Trainer active. Press F1 for infinite health, F2 for infinite flashlight, F3 for infinite ammo. Press Escape to exit.");

            while (_isRunning)
            {
                if (Console.KeyAvailable)
                {
                    var key = Console.ReadKey(true).Key;
                    switch (key)
                    {
                        case ConsoleKey.F1:
                            ToggleCheat("infinite_health");
                            break;
                        case ConsoleKey.F2:
                            ToggleCheat("infinite_flashlight");
                            break;
                        case ConsoleKey.F3:
                            ToggleCheat("infinite_ammo");
                            break;
                        case ConsoleKey.Escape:
                            _isRunning = false;
                            Console.WriteLine("Exiting trainer...");
                            break;
                    }
                }

                // Apply active cheats
                if (_cheatStates["infinite_health"])
                {
                    IntPtr healthAddr = _memory.ResolvePointer(_healthBase, _healthOffsets);
                    _memory.WriteFloat(healthAddr, 100.0f);
                }
                if (_cheatStates["infinite_flashlight"])
                {
                    IntPtr flashlightAddr = _memory.ResolvePointer(_flashlightBase, _flashlightOffsets);
                    _memory.WriteFloat(flashlightAddr, 100.0f);
                }
                if (_cheatStates["infinite_ammo"])
                {
                    IntPtr ammoAddr = _memory.ResolvePointer(_ammoBase, _ammoOffsets);
                    _memory.WriteInt(ammoAddr, 999);
                }

                Thread.Sleep(50); // Prevent CPU hogging
            }
        }

        /// <summary>
        /// Toggles a cheat state and prints feedback to the console.
        /// </summary>
        private void ToggleCheat(string cheatName)
        {
            _cheatStates[cheatName] = !_cheatStates[cheatName];
            string status = _cheatStates[cheatName] ? "enabled" : "disabled";
            Console.WriteLine($"{cheatName.Replace('_', ' ')} {status}");
        }
    }
}
