package com.alantrainer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

/**
 * Handles low-level memory operations for the Alan Wake 2 process.
 * Uses JNA to interface with Windows kernel32 APIs for reading/writing process memory.
 */
public class MemoryManager {

    private final Kernel32 kernel32;
    private WinNT.HANDLE processHandle;
    private final int processId;

    /**
     * Creates a MemoryManager for a given process ID.
     *
     * @param processId The Windows process ID of Alan Wake 2.
     */
    public MemoryManager(int processId) {
        this.processId = processId;
        this.kernel32 = Kernel32.INSTANCE;
        openProcess();
    }

    /**
     * Opens the target process with necessary access rights (VM_READ | VM_WRITE | VM_OPERATION).
     */
    private void openProcess() {
        int desiredAccess = WinNT.PROCESS_VM_READ | WinNT.PROCESS_VM_WRITE | WinNT.PROCESS_VM_OPERATION;
        processHandle = kernel32.OpenProcess(desiredAccess, false, processId);
        if (processHandle == null || !WinNT.HANDLE.ByReference.class.isInstance(processHandle)) {
            throw new RuntimeException("Failed to open process: " + Native.getLastError());
        }
    }

    /**
     * Reads a 4-byte integer from the specified memory address.
     *
     * @param address The memory address to read from.
     * @return The integer value at that address.
     */
    public int readInt(long address) {
        Pointer buffer = new Pointer(Native.malloc(4));
        try {
            int[] bytesRead = new int[1];
            boolean success = kernel32.ReadProcessMemory(processHandle, new Pointer(address), buffer, 4, bytesRead);
            if (!success) {
                throw new RuntimeException("ReadProcessMemory failed at 0x" + Long.toHexString(address));
            }
            return buffer.getInt(0);
        } finally {
            Native.free(Pointer.nativeValue(buffer));
        }
    }

    /**
     * Writes a 4-byte integer to the specified memory address.
     *
     * @param address The memory address to write to.
     * @param value   The integer value to write.
     */
    public void writeInt(long address, int value) {
        Pointer buffer = new Pointer(Native.malloc(4));
        try {
            buffer.setInt(0, value);
            int[] bytesWritten = new int[1];
            boolean success = kernel32.WriteProcessMemory(processHandle, new Pointer(address), buffer, 4, bytesWritten);
            if (!success) {
                throw new RuntimeException("WriteProcessMemory failed at 0x" + Long.toHexString(address));
            }
        } finally {
            Native.free(Pointer.nativeValue(buffer));
        }
    }

    /**
     * Closes the process handle to release system resources.
     */
    public void close() {
        if (processHandle != null) {
            kernel32.CloseHandle(processHandle);
            processHandle = null;
        }
    }
}
