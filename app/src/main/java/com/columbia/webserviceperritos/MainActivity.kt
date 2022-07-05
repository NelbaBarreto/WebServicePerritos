package com.columbia.webserviceperritos

import Imagen
import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import com.columbia.webserviceperritos.R
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var tvDatos: TextView
    private lateinit var bBuscar: Button
    private lateinit var ivFoto: ImageView
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDatos = findViewById(R.id.tvDatos)
        ivFoto = findViewById(R.id.ivFoto)
        videoView = findViewById(R.id.videoView)
        bBuscar = findViewById(R.id.bBuscar)
        bBuscar.setOnClickListener {
            Restful(this).execute()
        }
    }

    // tercer param es lo que retorna el do in background
    inner class Restful(private var contexto: Context) : AsyncTask<Void, Void,
            Imagen>() {
        private lateinit var progreso : ProgressDialog

        override fun onPreExecute() {
            progreso = ProgressDialog(contexto)
            progreso.setMessage("Buscando foto random...")
            progreso.show()
        }

        override fun doInBackground(vararg params: Void?): Imagen {
            var url = "https://random.dog/woof.json"
            var connection: HttpURLConnection? = null
            var imagen = Imagen()

            try {
                val request = URL(url)
                connection = request.openConnection() as HttpURLConnection
                val datos: InputStream = BufferedInputStream(connection.inputStream)
                val scanner = Scanner(datos)
                var respuesta: String? = ""
                while (scanner.hasNextLine()) {
                    respuesta += scanner.nextLine()
                }

                try {
                    // Convertir la respuesta a JSON para obtener los valores
                    val respuestaJSON = JSONObject(respuesta)
                    imagen.tamanio = respuestaJSON.getInt("fileSizeBytes") / 1024
                    imagen.url = respuestaJSON.getString("url")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }

            if (imagen.url.endsWith("jpeg")
                || imagen.url.endsWith("jpg")
                || imagen.url.endsWith("png")
                || imagen.url.endsWith("gif")) {
                try {
                    val inputStream = URL(imagen.url).openStream()
                    imagen.foto = BitmapFactory.decodeStream(inputStream)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    return imagen
                }
            }
            return imagen
        }

        override fun onPostExecute(imagen: Imagen?) {
            super.onPostExecute(imagen)
            progreso.dismiss()
            tvDatos.text = Html.fromHtml(
                "<b>Url:</b>" + imagen!!.url + "<b>Tamaño:</b>" + imagen.tamanio.toString() + "kb"
            )

            if (imagen.foto != null) {
                ivFoto.visibility = View.VISIBLE
                videoView.visibility = View.GONE
                ivFoto.setImageBitmap(imagen.foto)

            } else {
                if(!imagen.url.equals("")) {
                    ivFoto.visibility = View.GONE
                    videoView.visibility = View.VISIBLE

                    val uri: Uri = Uri.parse(imagen.url)
                    videoView.setVideoURI(uri)

                    val mediaController = MediaController(contexto)
                    mediaController.setAnchorView(videoView)
                    mediaController.setMediaPlayer(videoView)
                    videoView.start()
                }
            }
        }
    }
}