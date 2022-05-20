package com.wt.printlib

import com.snbc.sdk.barcode.IBarInstruction.ILabelEdit
import com.snbc.sdk.barcode.enumeration.*

class PrintUtils {
    companion object {
        /**
         * 打印pps 标签
         */
        fun printPPSLabel(
            shipmentid: String,
            palletno: String,
            cartons: String,
            pallettype: String,
            pallets: String,
            level: String,
            specification: String
        ) {
            val printer = ConnectUtils.printer
            val labelEdit: ILabelEdit? = printer?.labelEdit()
            val xaxis = 75
            labelEdit.let {
                it?.setColumn(1, 0)//设置打印列数及横向间距 i:打印列数 i1:标签间距，点位:点
                it?.setLabelSize(650, 423) //设置标签大小 i:标签宽度，单位:点 i1:标签高度，单位:点
                it?.printText(
                    xaxis + 80, 0,
                    "4",
                    "Pick Pallet Label",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )
                //i:横坐标 i1:纵坐标 rotation:打印旋转角度 s:条码内容
                // s1:安全级别('H'表示极高可靠性级别, 'Q'表示高可靠性级别, 'M'表示标准级别, 'L'表示高密度级别)
                // i2:模块宽度 i3:条码模式(1为原始模式，2为增强模式)
                it?.printBarcodeQR(
                    xaxis + 40,
                    50,
                    Rotation.Rotation0,
                    shipmentid,
                    QRLevel.QR_LEVEL_M.level,
                    5,
                    QRMode.QR_MODE_ENHANCED.mode
                )
                it?.printText(
                    xaxis + 160, 60,
                    "5",
                    "Pack level: $level",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )

                it?.printText(
                    xaxis + 160, 110,
                    "5",
                    pallettype,
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )

                it?.printText(
                    xaxis + 20, 155,
                    "5",
                    shipmentid,
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )

                it?.printText(
                    xaxis, 190,
                    "5",
                    "Total Cartons: $cartons",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )
                it?.printText(
                    xaxis, 230,
                    "5",
                    "Total Pallets: $pallets",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )

                it?.printText(
                    xaxis + 220, 265,
                    "5",
                    palletno,
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )

                it?.printText(
                    xaxis, 290,
                    "5",
                    "Pallet specification: $specification",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )

                it?.printBarcodeQR(
                    xaxis + 280,
                    160,
                    Rotation.Rotation0,
                    palletno,
                    QRLevel.QR_LEVEL_M.level,
                    5,
                    QRMode.QR_MODE_ENHANCED.mode
                )
            }
            printer?.labelControl()?.print(1, 1)
        }

        /**
         * pps check 打印标签
         */
        fun printPPSCheckLabel(shipment_id: String, pallet_no: String, pack_type:String, security: String) {
            val printer = ConnectUtils.printer
            val labelEdit: ILabelEdit? = printer?.labelEdit()
            val xaxis = 75
            labelEdit.let {
                it?.setColumn(1, 0)//设置打印列数及横向间距 i:打印列数 i1:标签间距，点位:点
                it?.setLabelSize(650, 423) //设置标签大小 i:标签宽度，单位:点 i1:标签高度，单位:点
                it?.printText(
                    xaxis + 60, 0,
                    "4",
                    "Check Pass Label",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )
                it?.printBarcode1D(
                    xaxis,
                    60,
                    BarCodeType.Code128,
                    Rotation.Rotation0,
                    shipment_id.toByteArray(),
                    50,
                    HRIPosition.None,
                    1,
                    3
                )
                it?.printText(
                    xaxis, 120,
                    "5",
                    "Shipment ID  $shipment_id",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )

                it?.printBarcode1D(
                    xaxis,
                    180,
                    BarCodeType.Code128,
                    Rotation.Rotation0,
                    pallet_no.toByteArray(),
                    50,
                    HRIPosition.None,
                    1,
                    3
                )
                it?.printText(
                    xaxis, 240,
                    "5",
                    "Pallet No  $pallet_no",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )
                it?.printText(
                    xaxis, 280,
                    "5",
                    "Pack type: $pack_type          $security",
                    Rotation.Rotation0,
                    0,
                    0,
                    0
                )
            }
            printer?.labelControl()?.print(1, 1)
        }

        /**
         * 打印wms 入库打印 栈板号
         */
        fun printWmsInput(palletNo: String) {
            val printer = ConnectUtils.printer
            val labelEdit: ILabelEdit? = printer?.labelEdit()
            labelEdit.let {
                it?.setColumn(2, 0)
                it?.setLabelSize(576, 280)
                it?.printText(
                    100, 155,
                    "2",
                    "PALLET NO",
                    Rotation.Rotation0,
                    1,
                    1,
                    0
                )
                it?.printBarcode1D(
                    100,
                    200,
                    BarCodeType.Code128,
                    Rotation.Rotation0,
                    palletNo.toByteArray(),
                    50,
                    HRIPosition.AlignCenter,
                    1,
                    3
                )
            }
            printer?.labelControl()?.print(1, 1)
        }

        /**
         * 打印wms 拆合
         * 栈板号  WT料号  客户料号  拆分数量
         */
        fun printWmsMiamiChai(
            palletNo: String,
            materialNo: String,
            custMaterialNo: String,
            qty: Int
        ) {
            val printer = ConnectUtils.printer
            val labelEdit: ILabelEdit? = printer?.labelEdit()
            labelEdit.let {
                it?.setColumn(1, 0)
                it?.setLabelSize(576, 420)
                it?.printText(
                    100, 0,
                    "3",
                    "PALLET NO",
                    Rotation.Rotation0,
                    1,
                    1,
                    0
                )
                it?.printBarcode1D(
                    100,
                    30,
                    BarCodeType.Code128,
                    Rotation.Rotation0,
                    palletNo.toByteArray(),
                    45,
                    HRIPosition.AlignCenter,
                    1,
                    3
                )

                it?.setColumn(1, 0)
                it?.printText(
                    100, 110,
                    "3",
                    "materialNo",
                    Rotation.Rotation0,
                    1,
                    1,
                    0
                )
                it?.printBarcode1D(
                    100,
                    140,
                    BarCodeType.Code128,
                    Rotation.Rotation0,
                    materialNo.toByteArray(),
                    45,
                    HRIPosition.AlignCenter,
                    1,
                    3
                )
                it?.setColumn(1, 0)
                it?.printText(
                    100, 220,
                    "3",
                    "custMaterialNo",
                    Rotation.Rotation0,
                    1,
                    1,
                    0
                )
                it?.printBarcode1D(
                    100,
                    250,
                    BarCodeType.Code128,
                    Rotation.Rotation0,
                    custMaterialNo.toByteArray(),
                    45,
                    HRIPosition.AlignCenter,
                    1,
                    3
                )
                it?.printText(
                    450, 0,
                    "3",
                    "qty $qty",
                    Rotation.Rotation0,
                    1,
                    1,
                    0
                )
            }
            printer?.labelControl()?.print(1, 1)
        }

    }
}