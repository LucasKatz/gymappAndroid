package com.example.clubdeportivodam

import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class PerfilSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_socio)

        val tvNombre = findViewById<TextView>(R.id.tvNombreSocio)
        val tvDni = findViewById<TextView>(R.id.tvDniSocio)
        val tvCategoria = findViewById<TextView>(R.id.tvCategoriaSocio)
        val tvActividad = findViewById<TextView>(R.id.tvActividadSocio)
        val tvVencimiento = findViewById<TextView>(R.id.tvVencimientoSocio)
        val btnVolver = findViewById<ImageButton>(R.id.btnBack)

        val btnDescargarPdf = findViewById<Button>(R.id.btnDescargarPdf)

        val vistaCarnet = findViewById<View>(R.id.contenedorCarnet)

        val dniRecibido = intent.getStringExtra("DNI_SOCIO")

        if (!dniRecibido.isNullOrEmpty()) {
            val admin = AdminSQLiteOpenHelper(this)
            val db = admin.readableDatabase

            val cursor = db.rawQuery("SELECT * FROM socios WHERE dni = ?", arrayOf(dniRecibido))

            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                val vencimientoRaw = cursor.getString(cursor.getColumnIndexOrThrow("vencimiento"))
                val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))

                // --- LÓGICA DE FORMATEO DE FECHA ---
                val fechaFormateada = formatearFecha(vencimientoRaw)

                // Asignar datos
                tvNombre.text = nombre
                tvDni.text = "DNI: $dniRecibido"
                tvCategoria.text = categoria.uppercase()
                tvVencimiento.text = fechaFormateada
                tvActividad.text = "Estado: $estado"

                // Estética de la categoría
                if (categoria.contains("Socio", ignoreCase = true)) {
                    tvCategoria.setBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"))
                    tvCategoria.setTextColor(android.graphics.Color.parseColor("#1B4F72"))
                } else {
                    tvCategoria.setBackgroundColor(android.graphics.Color.parseColor("#FBE9E7"))
                    tvCategoria.setTextColor(android.graphics.Color.parseColor("#D84315"))
                }

                // Configuración del botón para exportar a PDF utilizando el nombre del socio para el archivo
                btnDescargarPdf.setOnClickListener {
                    if (vistaCarnet != null) {
                        generarYGuardarPDF(vistaCarnet, "Carnet_${nombre.replace(" ", "_")}")
                    } else {
                        Toast.makeText(this, "Error: No se encontró la vista del carnet", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
            db.close()
        }

        btnVolver.setOnClickListener { finish() }
    }

    // Convierte el formato de fecha a un formato legible
    private fun formatearFecha(fechaRaw: String): String {
        return try {
            if (fechaRaw.contains("-")) {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
                val date = parser.parse(fechaRaw)
                date?.let { formatter.format(it) } ?: fechaRaw
            } else {
                val milis = fechaRaw.toLong()
                val formatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
                formatter.format(java.util.Date(milis))
            }
        } catch (e: Exception) {
            fechaRaw
        }
    }

    // Lógica técnica para procesar la UI gráficamente, plasmarla en el motor de PDF y guardarla en Descargas
    private fun generarYGuardarPDF(vista: View, nombreArchivo: String) {
        // 1. Instanciamos el documento PDF
        val pdfDocument = PdfDocument()

        // 2. Definimos el tamaño de la página basados en las dimensiones físicas actuales de tu vista XML
        val pageInfo = PdfDocument.PageInfo.Builder(vista.width, vista.height, 1).create()
        val pagina = pdfDocument.startPage(pageInfo)

        // 3. Obtenemos el lienzo gráfico (Canvas) de la página del PDF y pintamos la vista del carnet sobre él
        val canvas: Canvas = pagina.canvas
        vista.draw(canvas)

        // 4. Concluimos la escritura de la página
        pdfDocument.finishPage(pagina)

        // 5. Manejo de almacenamiento compatible con todas las versiones de Android (Scoping Storage)
        var outputStream: OutputStream? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Lógica obligatoria para Android 10 (API 29) en adelante mediante MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$nombreArchivo.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    outputStream = contentResolver.openOutputStream(uri)
                }
            } else {
                // Lógica para versiones antiguas de Android (Requiere permisos de escritura en el manifest)
                val directorioDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val archivoFinal = File(directorioDescargas, "$nombreArchivo.pdf")
                outputStream = FileOutputStream(archivoFinal)
            }

            // 6. Escribimos los bytes del documento al almacenamiento físico
            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
                // REQUERIMIENTO: Lanzamos el Toast solicitado por pantalla
                Toast.makeText(this, "Carnet Descargado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error al inicializar el archivo de salida", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al exportar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            // 7. Liberamos los recursos de memoria del Stream y del generador de PDF
            try {
                outputStream?.close()
            } catch (e: Exception) { e.printStackTrace() }
            pdfDocument.close()
        }
    }
}