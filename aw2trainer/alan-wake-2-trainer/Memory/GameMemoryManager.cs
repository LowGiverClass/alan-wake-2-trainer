using System;
using System.Diagnostics;
using System.Runtime.InteropServices;

namespace AlanWake2Trainer.Memory
{
    /// <summary>
    /// Manages direct memory read/write operations for the Alan Wake 2 process.
    /// Uses Win32 API for external memory manipulation.
    /// </summary>
    public class GameMemoryManager : IDisposable
    {
        private readonly Process _process;
        private readonly IntPtr _processHandle;
        private bool _disposed;

        // Win32 API imports
        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern IntPtr OpenProcess(uint dwDesiredAccess, bool bInheritHandle, int dwProcessId);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool ReadProcessMemory(IntPtr hProcess, IntPtr lpBaseAddress, byte[] lpBuffer, int dwSize, out int lpNumberOfBytesRead);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool WriteProcessMemory(IntPtr hProcess, IntPtr lpBaseAddress, byte[] lpBuffer, int dwSize, out int lpNumberOfBytesWritten);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool CloseHandle(IntPtr hObject);

        private const uint PROCESS_ALL_ACCESS = 0x1F0FFF;

        public GameMemoryManager(Process process)
        {
            _process = process ?? throw new ArgumentNullException(nameof(process));
            _processHandle = OpenProcess(PROCESS_ALL_ACCESS, false, _process.Id);
            if (_processHandle == IntPtr.Zero)
            {
                throw new InvalidOperationException("Failed to open process handle.");
            }
        }

        /// <summary>
        /// Reads a float value from the specified memory address.
        /// </summary>
        public float ReadFloat(IntPtr address)
        {
            byte[] buffer = new byte[4];
            if (ReadProcessMemory(_processHandle, address, buffer, 4, out _))
            {
                return BitConverter.ToSingle(buffer, 0);
            }
            return 0f;
        }

        /// <summary>
        /// Writes a float value to the specified memory address.
        /// </summary>
        public void WriteFloat(IntPtr address, float value)
        {
            byte[] buffer = BitConverter.GetBytes(value);
            WriteProcessMemory(_processHandle, address, buffer, 4, out _);
        }

        /// <summary>
        /// Reads an integer value from the specified memory address.
        /// </summary>
        public int ReadInt(IntPtr address)
        {
            byte[] buffer = new byte[4];
            if (ReadProcessMemory(_processHandle, address, buffer, 4, out _))
            {
                return BitConverter.ToInt32(buffer, 0);
            }
            return 0;
        }

        /// <summary>
        /// Writes an integer value to the specified memory address.
        /// </summary>
        public void WriteInt(IntPtr address, int value)
        {
            byte[] buffer = BitConverter.GetBytes(value);
            WriteProcessMemory(_processHandle, address, buffer, 4, out _);
        }

        /// <summary>
        /// Resolves a multi-level pointer to the final address.
        /// </summary>
        public IntPtr ResolvePointer(IntPtr baseAddress, int[] offsets)
        {
            IntPtr currentAddress = baseAddress;
            foreach (int offset in offsets)
            {
                currentAddress = (IntPtr)(ReadInt(currentAddress) + offset);
            }
            return currentAddress;
        }

        public void Dispose()
        {
            if (!_disposed)
            {
                if (_processHandle != IntPtr.Zero)
                {
                    CloseHandle(_processHandle);
                }
                _disposed = true;
            }
        }
    }
}
