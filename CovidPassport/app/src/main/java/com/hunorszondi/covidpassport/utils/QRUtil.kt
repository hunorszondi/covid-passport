package com.hunorszondi.covidpassport.utils

import android.graphics.Bitmap
import android.graphics.Point
import android.view.WindowManager
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.content.ContextCompat
import com.google.zxing.WriterException

class QRUtil {
    fun generateQRCode(signature: String): Bitmap? {
        val manager = ContextCompat.getSystemService(ResourceUtil.instance.getContext(), WindowManager::class.java)

        // initializing a variable for default display.
        val display = manager!!.defaultDisplay

        // creating a variable for point which
        // is to be displayed in QR Code.

        // creating a variable for point which
        // is to be displayed in QR Code.
        val point = Point()
        display.getSize(point)

        // getting width and
        // height of a point

        // getting width and
        // height of a point
        val width: Int = point.x
        val height: Int = point.y

        // generating dimension from width and height.

        // generating dimension from width and height.
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        val qrgEncoder = QRGEncoder(signature, null, QRGContents.Type.TEXT, dimen)
        return try {
            qrgEncoder.encodeAsBitmap()
        } catch (e: WriterException) {
            null
        }
    }
}