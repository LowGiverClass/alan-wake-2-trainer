"""Unit tests for the trainer features (mocked memory)."""

import unittest
from unittest.mock import patch, MagicMock
from alan_wake_2_trainer.features import AlanWake2Trainer, OFFSETS


class TestAlanWake2Trainer(unittest.TestCase):
    """Test suite for AlanWake2Trainer class."""

    @patch('alan_wake_2_trainer.features.open_process')
    def test_initialization_success(self, mock_open_process):
        """Test successful trainer initialization."""
        mock_open_process.return_value = 12345
        trainer = AlanWake2Trainer(1234)
        self.assertEqual(trainer.pid, 1234)
        self.assertEqual(trainer.handle, 12345)
        trainer.close()

    @patch('alan_wake_2_trainer.features.open_process')
    def test_initialization_failure(self, mock_open_process):
        """Test trainer initialization when process cannot be opened."""
        mock_open_process.return_value = None
        with self.assertRaises(RuntimeError):
            AlanWake2Trainer(1234)

    @patch('alan_wake_2_trainer.features.open_process')
    @patch('alan_wake_2_trainer.features.read_memory')
    def test_get_health(self, mock_read_memory, mock_open_process):
        """Test reading health value."""
        mock_open_process.return_value = 12345
        mock_read_memory.return_value = b'\x00\x00\xc8\x42'  # 100.0 as float
        trainer = AlanWake2Trainer(1234)
        health = trainer.get_health()
        self.assertAlmostEqual(health, 100.0, places=4)
        trainer.close()

    @patch('alan_wake_2_trainer.features.open_process')
    @patch('alan_wake_2_trainer.features.write_memory')
    def test_set_health(self, mock_write_memory, mock_open_process):
        """Test setting health value."""
        mock_open_process.return_value = 12345
        mock_write_memory.return_value = True
        trainer = AlanWake2Trainer(1234)
        result = trainer.set_health(50.0)
        self.assertTrue(result)
        mock_write_memory.assert_called_once_with(
            12345,
            OFFSETS["health"],
            b'\x00\x00\x48\x42'  # 50.0 as float
        )
        trainer.close()

    @patch('alan_wake_2_trainer.features.open_process')
    @patch('alan_wake_2_trainer.features.read_memory')
    def test_get_ammo(self, mock_read_memory, mock_open_process):
        """Test reading ammo value."""
        mock_open_process.return_value = 12345
        mock_read_memory.return_value = b'\xe7\x03\x00\x00'  # 999 as int
        trainer = AlanWake2Trainer(1234)
        ammo = trainer.get_ammo()
        self.assertEqual(ammo, 999)
        trainer.close()

    @patch('alan_wake_2_trainer.features.open_process')
    @patch('alan_wake_2_trainer.features.write_memory')
    def test_infinite_health(self, mock_write_memory, mock_open_process):
        """Test infinite health toggle sets health to 100."""
        mock_open_process.return_value = 12345
        mock_write_memory.return_value = True
        trainer = AlanWake2Trainer(1234)
        trainer.infinite_health(True)
        mock_write_memory.assert_called_once_with(
            12345,
            OFFSETS["health"],
            b'\x00\x00\xc8\x42'
        )
        trainer.close()


if __name__ == '__main__':
    unittest.main()
