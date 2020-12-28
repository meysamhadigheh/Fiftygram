package edu.harvard.cs50.fiftygram

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import java.io.OutputStream
import java.lang.reflect.Method

import java.io.File
import java.io.FileOutputStream



object ShareHelper {

    fun saveBitmap(bm: Bitmap, fileName: String, context: Context): Uri? {


        val fOut: OutputStream?
        val uri: Uri?

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/fiftyGramApp"
            val dir = File(dirPath)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dirPath, fileName)
            fOut = FileOutputStream(file)
            uri = Uri.fromFile(file)


        } else {

            val resolver: ContentResolver = context.contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/fiftyGramApp")
            uri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fOut = uri?.let { resolver.openOutputStream(it) }

        }

        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut?.flush()
            fOut?.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }



        return uri
    }

    fun shareImage(uri: Uri?, activity: Activity, message: String?) {


        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            startActivity(activity, Intent.createChooser(intent, "Share Screenshot"), null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }





}