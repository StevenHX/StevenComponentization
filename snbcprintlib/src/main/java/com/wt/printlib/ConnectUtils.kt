package com.wt.printlib

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.widget.Toast
import com.snbc.sdk.barcode.BarInstructionImpl.BarPrinter
import com.snbc.sdk.barcode.BarInstructionImpl.BarPrinter.BarPrinterBuilder
import com.snbc.sdk.barcode.enumeration.InstructionType
import com.snbc.sdk.connect.connectImpl.BluetoothConnect
import java.lang.Exception

class ConnectUtils {
    companion object {
        var printer: BarPrinter? = null
        var connect: BluetoothConnect? = null
        fun isConnectNull(): Boolean {
            return connect == null
        }

        fun isPrinterNull(): Boolean {
            return printer == null
        }

        fun setPrintNull() {
            printer = null
        }

        fun DiscoverDevice(): Set<BluetoothDevice> {
            val blueToothAdapter = BluetoothAdapter.getDefaultAdapter()
                ?: return mutableSetOf<BluetoothDevice>()
            if (!blueToothAdapter.isEnabled) {
                blueToothAdapter.enable()
            }
            return blueToothAdapter.bondedDevices
        }

        fun ConnectDevice(ip: String, name: String) {
            try {
                val bluetoothConnect1 = BluetoothConnect(BluetoothAdapter.getDefaultAdapter(), ip)
                bluetoothConnect1.DecodeType("GB18030")
                bluetoothConnect1.connect()
                val builder = BarPrinterBuilder()
                builder.buildDeviceConnenct(bluetoothConnect1)
                builder.buildInstruction(InstructionType.BPLC)
                val printer1 = builder.barPrinter
                printer = printer1
                connect = bluetoothConnect1
            } catch (e: Exception) {
                throw Exception()
            }
        }
    }
}