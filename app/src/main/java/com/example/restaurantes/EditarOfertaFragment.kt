package com.example.restaurantes

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditarOfertaFragment : Fragment() {
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
    private var ofertaId: String? = null

    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val CAMERA_REQUEST_CODE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.editar_oferta_fragment, container, false)
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

        val oferta = arguments?.getParcelable("oferta") as? Oferta
        if (oferta != null) {
            val id = oferta.id
            obtenerDatosOfertaDesdeDB(id)
        } else {
            Toast.makeText(context, "Error al cargar la información", Toast.LENGTH_SHORT).show()
        }

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

    private fun obtenerDatosOfertaDesdeDB(ofertaId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Ofertas").document(ofertaId)
            .get()
            .addOnSuccessListener { documento ->
                val imageOfertaUrl = documento.data?.get("imagen").toString()
                println("url img: ${imageOfertaUrl}")

                cargarImagenOferta(imageOfertaUrl, imgUsr)
                val nombreOf = documento.data?.get("titulo").toString()
                val descripcionOf = documento.data?.get("descripcion").toString()
                val precioOf = documento.data?.get("precio").toString()
                val fechaIniOf = documento.data?.get("fechaInicio").toString()
                val fechaFinOf = documento.data?.get("fechaFin").toString()

                editNombre.setText(nombreOf)
                editDescripcion.setText(descripcionOf)
                editPrecioOferta.setText(precioOf)
                editFechaIni.setText(fechaIniOf)
                editFechaFin.setText(fechaFinOf)

            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
    }

    private fun cargarImagenOferta(imageOfertaUrl: String, imageView: ImageView) {
        if (imageOfertaUrl.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.banner).into(imageView)
        } else {
            try {
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageOfertaUrl)

                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    Picasso.get()
                        .load(uri.toString())
                        .into(imageView)
                }.addOnFailureListener { exception ->
                    Toast.makeText(imageView.context, "Error al cargar imagen: ${exception.message}", Toast.LENGTH_LONG).show()
                    Picasso.get().load(R.drawable.banner).into(imageView)
                }
            } catch (e: IllegalArgumentException) {
                Log.e("ImagenOferta", "URL de imagen es inválida: $imageOfertaUrl")
                Picasso.get().load(R.drawable.banner).into(imageView)
            }
        }
    }

    private fun guardarCambios() {
        val nombrePlato = editNombre.text.toString()
        val descripcion = editDescripcion.text.toString()
        val precioOferta = editPrecioOferta.text.toString().toDoubleOrNull()
        val fechaInicio = editFechaIni.text.toString()
        val fechaFin = editFechaFin.text.toString()

        val oferta = arguments?.getParcelable("oferta") as? Oferta

        if (oferta != null) {
            val id = oferta.id
            val db = FirebaseFirestore.getInstance()
            val ofertaDocRef = db.collection("Ofertas").document(id)

            val updates = mutableMapOf<String, Any>(
                "titulo" to nombrePlato,
                "descripcion" to descripcion,
                "precio" to (precioOferta ?: 0.0),
                "fechaInicio" to fechaInicio,
                "fechaFin" to fechaFin
            )

            imageUri?.let { uri ->
                val storageReference = FirebaseStorage.getInstance().reference.child("Ofertas/$id")

                storageReference.delete().addOnSuccessListener {
                    storageReference.putFile(uri)
                        .addOnSuccessListener {
                            storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                                updates["imagen"] = downloadUri.toString()
                                ofertaDocRef.update(updates)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Oferta actualizada correctamente", Toast.LENGTH_SHORT).show()
                                        requireActivity().onBackPressed()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(context, "Error al actualizar oferta: ${exception.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Error al subir imagen: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }.addOnFailureListener { exception ->
                    storageReference.putFile(uri)
                        .addOnSuccessListener {
                            storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                                updates["imagen"] = downloadUri.toString()
                                ofertaDocRef.update(updates)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Oferta actualizada correctamente", Toast.LENGTH_SHORT).show()
                                        requireActivity().onBackPressed()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(context, "Error al actualizar oferta: ${exception.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Error al subir imagen: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
            } ?: run {
                ofertaDocRef.update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Oferta actualizada correctamente", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Error al actualizar oferta: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }
        } else {
            Toast.makeText(context, "ID de oferta no encontrado", Toast.LENGTH_SHORT).show()
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
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    imageUri = data?.data
                    imgUsr.setImageURI(imageUri)
                }
                CAMERA_REQUEST_CODE -> {
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
}