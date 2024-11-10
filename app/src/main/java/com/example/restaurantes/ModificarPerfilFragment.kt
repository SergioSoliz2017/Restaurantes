package com.example.restaurantes

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ModificarPerfilFragment : Fragment() {

    private lateinit var imgUsr: RoundedImageView
    private lateinit var editNombre: TextView
    private lateinit var editCorreo: TextView
    private lateinit var editNumero: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnEditarImagen: Button
    private var imageUri: Uri? = null

    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val CAMERA_REQUEST_CODE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_modificar_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgUsr = view.findViewById(R.id.imgUsr)
        editNombre = view.findViewById(R.id.editNombre)
        editCorreo = view.findViewById(R.id.editCorreo)
        editNumero = view.findViewById(R.id.editNumero)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnCancelar = view.findViewById(R.id.btnCancelar)
        btnEditarImagen = view.findViewById(R.id.btnEditarImagen)

        val actividad = activity as? PantallaPrincipal
        val usuario = actividad?.usuario
        if (usuario != null) {
            obtenerDatosUsuarioDesdeDB(usuario)
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

    private fun obtenerDatosUsuarioDesdeDB(usuario: Usuario) {
        val db = FirebaseFirestore.getInstance()
        val correoUsuario = usuario.correo

        db.collection("Usuarios").document(correoUsuario)
            .get()
            .addOnSuccessListener { documento ->
                val nombreUsr = documento.data?.get("Nombre").toString()
                val correoUsr = documento.data?.get("Correo").toString()
                val numeroUsr = documento.data?.get("Número").toString()

                editNombre.text = nombreUsr
                editCorreo.text = correoUsr
                editNumero.text = numeroUsr

                cargarImagenPerfil(nombreUsr, imgUsr)
            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
    }

    private fun cargarImagenPerfil(nombreUsr: String, imageView: ImageView) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imagenReference = storageReference.child("Usuarios/$nombreUsr/perfil.jpg")
        val contexto = imageView.context

        imagenReference.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .into(imageView)
        }.addOnFailureListener { exception ->
            Toast.makeText(contexto, "Error al cargar imagen: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun guardarCambios() {
        val nombreActualizado = editNombre.text.toString()
        val correoActualizado = editCorreo.text.toString()
        val numeroActualizado = editNumero.text.toString()

        val actividad = activity as? PantallaPrincipal
        val usuario = actividad?.usuario

        if (usuario != null) {
            val db = FirebaseFirestore.getInstance()
            val usuarioDocRef = db.collection("Usuarios").document(usuario.correo)

            val updates = mutableMapOf<String, Any>(
                "Nombre" to nombreActualizado,
                "Correo" to correoActualizado,
                "Número" to numeroActualizado
            )

            // Subir imagen si está seleccionada
            imageUri?.let { uri ->
                val storageReference = FirebaseStorage.getInstance().reference
                    .child("Usuarios/${usuario.correo}/perfil.jpg")
                storageReference.putFile(uri)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                            updates["ImagenURL"] = downloadUri.toString()
                            usuarioDocRef.update(updates)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(context, "Error al actualizar perfil: ${exception.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Error al subir imagen: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            } ?: usuarioDocRef.update(updates) // Si no hay imagen, actualiza solo el texto
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