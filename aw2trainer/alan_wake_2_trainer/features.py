"""Game-specific features for Alan Wake 2 trainer."""

from typing import Optional
from .memory_manager import open_process, close_process, read_memory, write_memory

# Example offsets for Alan Wake 2 (hypothetical, for demonstration)
# In a real trainer, these would be found via AOB scans or reverse engineering
OFFSETS = {
    "health": 0x00A1B2C0,
    "ammo": 0x00A1B2C4,
    "flashlight_battery": 0x00A1B2C8,
    "experience": 0x00A1B2CC,
}


class AlanWake2Trainer:
    """Main trainer class for Alan Wake 2."""

    def __init__(self, pid: int):
        """Initialize trainer with target process ID.

        Args:
            pid: Process ID of Alan Wake 2.
        """
        self.pid = pid
        self.handle = open_process(pid)
        if self.handle is None:
            raise RuntimeError(f"Failed to open process with PID {pid}")

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.handle:
            close_process(self.handle)

    def close(self):
        """Close the process handle."""
        if self.handle:
            close_process(self.handle)
            self.handle = None

    def _read_float(self, offset: int) -> Optional[float]:
        """Read a 4-byte float from the base address + offset."""
        data = read_memory(self.handle, offset, 4)
        if data is None:
            return None
        import struct
        return struct.unpack('<f', data)[0]

    def _write_float(self, offset: int, value: float) -> bool:
        """Write a 4-byte float to the base address + offset."""
        import struct
        data = struct.pack('<f', value)
        return write_memory(self.handle, offset, data)

    def get_health(self) -> Optional[float]:
        """Get current health value."""
        return self._read_float(OFFSETS["health"])

    def set_health(self, value: float) -> bool:
        """Set health to a specific value."""
        return self._write_float(OFFSETS["health"], value)

    def infinite_health(self, enable: bool = True) -> None:
        """Toggle infinite health (sets health to 100 every frame).

        Note: In a real trainer this would hook a game loop or timer.
        """
        if enable:
            self.set_health(100.0)
        # In practice, you'd run a background thread continuously setting health.

    def get_ammo(self) -> Optional[int]:
        """Get current ammo count (4-byte integer)."""
        data = read_memory(self.handle, OFFSETS["ammo"], 4)
        if data is None:
            return None
        import struct
        return struct.unpack('<I', data)[0]

    def set_ammo(self, count: int) -> bool:
        """Set ammo to a specific count."""
        import struct
        data = struct.pack('<I', count)
        return write_memory(self.handle, OFFSETS["ammo"], data)

    def infinite_ammo(self, enable: bool = True) -> None:
        """Toggle infinite ammo."""
        if enable:
            self.set_ammo(999)

    def get_flashlight_battery(self) -> Optional[float]:
        """Get flashlight battery percentage."""
        return self._read_float(OFFSETS["flashlight_battery"])

    def set_flashlight_battery(self, value: float) -> bool:
        """Set flashlight battery."""
        return self._write_float(OFFSETS["flashlight_battery"], value)

    def infinite_flashlight(self, enable: bool = True) -> None:
        """Toggle infinite flashlight battery."""
        if enable:
            self.set_flashlight_battery(100.0)
