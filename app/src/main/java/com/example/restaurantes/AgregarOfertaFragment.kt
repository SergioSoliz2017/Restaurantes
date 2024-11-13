package com.example.restaurantes

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import java.io.ByteArrayOutputStream
import java.util.Calendar

class AgregarOfertaFragment : Fragment() {

    private lateinit var imgUsr: RoundedImageView
    private lateinit var editNombre: EditText
    private lateinit var editDescripcion: EditText
    private lateinit var editPrecioOferta: EditText
    private lateinit var editFechaIni: EditText
    private lateinit var editFechaFin: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnEditarImagen: ImageButton
    private var imageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val CAMERA_REQUEST_CODE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agregar_oferta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgUsr = view.findViewById(R.id.imgUsr)
        editNombre = view.findViewById(R.id.editNombre)
        editDescripcion = view.findViewById(R.id.editDescripcion)
        editPrecioOferta = view.findViewById(R.id.editPrecioOferta)
        editFechaIni = view.findViewById(R.id.editFechaIni)
        editFechaFin = view.findViewById(R.id.editFechaFin)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnCancelar = view.findViewById(R.id.btnCancelar)
        btnEditarImagen = view.findViewById(R.id.btnEditarImagen)

        fecha()

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        btnCancelar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnEditarImagen.setOnClickListener {
            editarImagen()
        }
    }

    private fun guardarCambios() {
        val nombre = editNombre.text.toString()
        val descripcion = editDescripcion.text.toString()
        val precio = editPrecioOferta.text.toString().toDoubleOrNull()

        if (precio == null) {
            Toast.makeText(context, "El precio debe ser un número válido.", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaIni = editFechaIni.text.toString()
        val fechaFin = editFechaFin.text.toString()

        val actividad = activity as? PantallaPrincipal
        val usuario = actividad?.usuario

        if (usuario != null) {
            val db = FirebaseFirestore.getInstance()
            val ofertasRef = db.collection("Ofertas")

            val restauranteId = usuario.nombreRestaurante

            val ofertaData = mutableMapOf<String, Any>(
                "titulo" to nombre,
                "descripcion" to descripcion,
                "precio" to precio,
                "fechaInicio" to fechaIni,
                "fechaFin" to fechaFin,
                "restauranteId" to restauranteId
            )

            imageUri?.let { uri ->
                val storageReference = FirebaseStorage.getInstance().reference
                    .child("Ofertas/${usuario.correo}/${System.currentTimeMillis()}.jpg")

                storageReference.putFile(uri)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                            ofertaData["imagen"] = downloadUri.toString()

                            ofertasRef.add(ofertaData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Oferta creada correctamente", Toast.LENGTH_SHORT).show()
                                    requireActivity().onBackPressed()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(context, "Error al crear oferta: ${exception.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Error al subir imagen: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            } ?: run {
                ofertaData["imagen"] = ""

                ofertasRef.add(ofertaData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Oferta creada correctamente", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Error al crear oferta: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }
        } else {
            Toast.makeText(context, "Error: usuario no disponible", Toast.LENGTH_SHORT).show()
        }
    }


    private fun editarImagen() {
        val options = arrayOf("Galería", "Cámara")
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar imagen")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> abrirGaleria()
                    1 -> abrirCamara()
                }
            }
            .show()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, ModificarPerfilFragment.PICK_IMAGE_REQUEST)
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, ModificarPerfilFragment.CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ModificarPerfilFragment.PICK_IMAGE_REQUEST -> {
                    imageUri = data?.data
                    imgUsr.setImageURI(imageUri)
                }
                ModificarPerfilFragment.CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    imgUsr.setImageBitmap(bitmap)
                    imageUri = getImageUriFromBitmap(requireContext(), bitmap)
                }
            }
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "TempImage", null)
        return Uri.parse(path)
    }

    private fun fecha() {
        editFechaIni.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    editFechaIni.setText(selectedDate)
                }, year, month, day
            )
            datePickerDialog.show()
        }

        editFechaFin.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    editFechaFin.setText(selectedDate)
                }, year, month, day
            )
            datePickerDialog.show()
        }
    }
}