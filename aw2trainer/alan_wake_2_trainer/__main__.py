"""Entry point for running the trainer as a script."""

import sys
import time
from .features import AlanWake2Trainer


def main():
    """Main CLI interface for the trainer."""
    if len(sys.argv) < 2:
        print("Usage: python -m alan_wake_2_trainer <pid>")
        print("Example: python -m alan_wake_2_trainer 1234")
        sys.exit(1)

    try:
        pid = int(sys.argv[1])
    except ValueError:
        print("Error: PID must be an integer.")
        sys.exit(1)

    try:
        with AlanWake2Trainer(pid) as trainer:
            print(f"Connected to Alan Wake 2 (PID: {pid})")
            print("Commands: health, ammo, flashlight, quit")
            while True:
                cmd = input("> ").strip().lower()
                if cmd == "health":
                    trainer.infinite_health(True)
                    print("Infinite health enabled.")
                elif cmd == "ammo":
                    trainer.infinite_ammo(True)
                    print("Infinite ammo enabled.")
                elif cmd == "flashlight":
                    trainer.infinite_flashlight(True)
                    print("Infinite flashlight enabled.")
                elif cmd == "quit":
                    break
                else:
                    print("Unknown command.")
    except RuntimeError as e:
        print(f"Error: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
