"""Module for reading and writing process memory (Windows-only)."""

import ctypes
from ctypes import wintypes
from typing import Optional, List, Tuple

# Windows API constants
PROCESS_VM_READ = 0x0010
PROCESS_VM_WRITE = 0x0020
PROCESS_VM_OPERATION = 0x0008
PROCESS_QUERY_INFORMATION = 0x0400

# Load kernel32.dll
kernel32 = ctypes.WinDLL('kernel32', use_last_error=True)


def open_process(pid: int) -> Optional[int]:
    """Open a process with required permissions.

    Args:
        pid: Process ID of the target game.

    Returns:
        Handle to the process, or None on failure.
    """
    handle = kernel32.OpenProcess(
        PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION | PROCESS_QUERY_INFORMATION,
        False,
        pid
    )
    if not handle:
        return None
    return handle


def close_process(handle: int) -> None:
    """Close an open process handle."""
    kernel32.CloseHandle(handle)


def read_memory(handle: int, address: int, size: int) -> Optional[bytes]:
    """Read memory from a process.

    Args:
        handle: Process handle.
        address: Base address to read from.
        size: Number of bytes to read.

    Returns:
        Bytes read, or None if failed.
    """
    buffer = ctypes.create_string_buffer(size)
    bytes_read = wintypes.SIZE_T(0)
    success = kernel32.ReadProcessMemory(
        handle,
        ctypes.c_void_p(address),
        buffer,
        size,
        ctypes.byref(bytes_read)
    )
    if not success:
        return None
    return buffer.raw


def write_memory(handle: int, address: int, data: bytes) -> bool:
    """Write data to process memory.

    Args:
        handle: Process handle.
        address: Target address.
        data: Bytes to write.

    Returns:
        True if successful, False otherwise.
    """
    bytes_written = wintypes.SIZE_T(0)
    success = kernel32.WriteProcessMemory(
        handle,
        ctypes.c_void_p(address),
        data,
        len(data),
        ctypes.byref(bytes_written)
    )
    return bool(success)


def find_pattern(handle: int, start_address: int, end_address: int, pattern: bytes, mask: str) -> Optional[int]:
    """Scan memory for a byte pattern (AOB scan).

    Args:
        handle: Process handle.
        start_address: Start of scan range.
        end_address: End of scan range.
        pattern: Byte pattern to find.
        mask: String mask ('x' for match, '?' for wildcard).

    Returns:
        Address of first match, or None.
    """
    chunk_size = 4096
    current = start_address
    while current < end_address:
        chunk = read_memory(handle, current, min(chunk_size, end_address - current))
        if chunk is None:
            current += chunk_size
            continue
        for i in range(len(chunk) - len(pattern) + 1):
            match = True
            for j in range(len(pattern)):
                if mask[j] == 'x' and chunk[i + j] != pattern[j]:
                    match = False
                    break
            if match:
                return current + i
        current += chunk_size
    return None
