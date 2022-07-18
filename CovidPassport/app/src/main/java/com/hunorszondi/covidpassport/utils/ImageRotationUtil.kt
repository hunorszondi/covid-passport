package com.hunorszondi.covidpassport.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.IOException
import android.provider.MediaStore
import android.content.Context
import android.net.Uri

/**
 * Used to rotate an image
 */
class ImageRotationUtil {

    /**
     * Rotate an image if required.
     *
     * @param context Any kind of context
     * @param img The image bitmap
     * @param uri Image URI
     * @param fromGallery Is the coming uri from gallery or not
     * @return The resulted Bitmap after manipulation
     */
    @Throws(IOException::class)
    fun rotateImageIfRequired(context: Context, img: Bitmap, uri: Uri, fromGallery: Boolean): Bitmap {

        val ei = if(fromGallery) {
            ExifInterface(getRealPathFromURI(uri, context))
        } else {
            ExifInterface(uri.path)
        }

        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
            else -> img
        }
    }

    /**
     * Does rotates the picture
     *
     * @param img image to rotate
     * @param degree degree rotate to
     * @return rotated image
     */
    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    /**
     * Extracts the generally accepted format of a file path from Uri
     *
     * @param contentURI to extract from
     * @param context Context
     *
     * @return real path
     */
    private fun getRealPathFromURI(contentURI: Uri, context: Context): String {
        val result: String
        val cursor = context.contentResolver
            .query(contentURI, null, null, null, null)
        result = if (cursor == null) {
            contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
        cursor?.close()
        return result
    }
}