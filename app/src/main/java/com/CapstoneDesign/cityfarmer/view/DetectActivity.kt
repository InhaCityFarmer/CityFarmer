package com.CapstoneDesign.cityfarmer.view

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.CapstoneDesign.cityfarmer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.IOException

class DetectActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView

    private val takePictureResult = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val resizedBitmap = resizeBitmap(it, 800)
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(resizedBitmap)
                }
                sendBitmapToApi(resizedBitmap)
            }
        }
    }

    private val selectPictureResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                val resizedBitmap = resizeBitmap(bitmap, 800)
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(resizedBitmap)
                }
                sendBitmapToApi(resizedBitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect)

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)

        val btnTakePhoto: Button = findViewById(R.id.btnTakePhoto)
        val btnUploadPhoto: Button = findViewById(R.id.btnUploadPhoto)

        btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                takePictureResult.launch(null)
            }
        }

        btnUploadPhoto.setOnClickListener {
            selectPictureResult.launch("image/*")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                takePictureResult.launch(null)
            } else {
                Toast.makeText(this, "카메라 권한을 허용 해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxSize && height <= maxSize) return bitmap

        val ratio: Float = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (ratio > 1) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun sendImageToApi(imageData: ByteArray) {
        val apiUrl = "https://api-inference.huggingface.co/models/swueste/plant-health-image-classifier"
        val client = OkHttpClient()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = RequestBody.create("application/octet-stream".toMediaTypeOrNull(), imageData)

                val request = Request.Builder()
                    .url(apiUrl)
                    .post(requestBody)
                    .addHeader("Authorization", resources.getString(R.string.Huggingface_API))
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseData != null) {
                        val jsonResponse = JSONArray(responseData)
                        val results = StringBuilder()
                        for (i in 0 until jsonResponse.length()) {
                            val item = jsonResponse.getJSONObject(i)
                            val label = item.getString("label")
                            val score = item.getDouble("score")
                            results.append("$label, ${"%.2f".format(score * 100)}%\n")
                        }
                        resultTextView.text = results.toString()
                    } else {
                        resultTextView.text = "죄송합니다. 다시 시도해 주세요"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("API_ERROR", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    resultTextView.text = "죄송합니다. 다시 시도해 주세요"
                }
            }
        }
    }

    private fun sendBitmapToApi(bitmap: Bitmap) {
        try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            outputStream.close()
            sendImageToApi(byteArray)
        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread {
                resultTextView.text = "죄송합니다. 다시 시도해 주세요"
            }
        }
    }
}
