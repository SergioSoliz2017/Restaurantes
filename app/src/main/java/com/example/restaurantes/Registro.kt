package com.example.restaurantes

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_registro.EsRestaurante
import kotlinx.android.synthetic.main.activity_registro.ImagenFotoPerfil
import kotlinx.android.synthetic.main.activity_registro.NombreRegistro
import kotlinx.android.synthetic.main.activity_registro.Registrar
import kotlinx.android.synthetic.main.activity_registro.SubirFotoPerfil
import kotlinx.android.synthetic.main.activity_registro.contraseñaRegistro
import kotlinx.android.synthetic.main.activity_registro.fechaRegistro
import kotlinx.android.synthetic.main.activity_registro.usuarioRegistro
import www.sanju.motiontoast.MotionToast
import java.util.Calendar

class Registro : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var nombre: String
    val db = FirebaseFirestore.getInstance()
    override fun onBackPressed() {
        super.onBackPressed()
        val inicio = Intent(this, MainActivity:: class.java)
        startActivity(inicio)
        finish()
    }
    lateinit var storageReference : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        email = intent.getStringExtra("email") ?: ""
        nombre = intent.getStringExtra("nombre") ?: ""
        usuarioRegistro.setText(email)
        NombreRegistro.setText(nombre)
        fecha()
        SubirFotoPerfil.setOnClickListener {
            abrirGaleria()
        }
        Registrar.setOnClickListener {
            if (Verificar()){
                if (EsRestaurante.isChecked){
                    val user = Usuario(
                        NombreRegistro.text.toString(),
                        usuarioRegistro.text.toString(),
                        contraseñaRegistro.text.toString(),
                        fechaRegistro.text.toString(),
                        true,
                        "",
                        uri.toString(),
                        uri
                    )
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuarioRegistro.text.toString(),contraseñaRegistro.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            storageReference = FirebaseStorage.getInstance().getReference("Usuario/${usuarioRegistro.text.toString()}")
                            storageReference.putFile(uri!!).addOnSuccessListener { snapshot ->
                                val uriTask: Task<Uri> = snapshot.getStorage().getDownloadUrl()
                                uriTask.addOnSuccessListener { uri ->
                                    val inicio =
                                        Intent(this, RegistroRestaurante::class.java).apply {
                                            putExtra("usuario", user)
                                        }
                                    finish()
                                    startActivity(inicio)
                                }
                            }
                        }else{
                            MotionToast.createToast(this,"Operacion Fallida", "Algo salio mal",MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                        }
                    }
                }else{
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuarioRegistro.text.toString(),contraseñaRegistro.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            storageReference = FirebaseStorage.getInstance().getReference("Usuario/${usuarioRegistro.text.toString()}")
                            storageReference.putFile(uri!!).addOnSuccessListener { snapshot ->
                                val uriTask: Task<Uri> = snapshot.getStorage().getDownloadUrl()
                                uriTask.addOnSuccessListener { uri ->
                                    db.collection("Usuarios")
                                        .document(usuarioRegistro.text.toString()).set(
                                            hashMapOf(
                                                "Nombre" to NombreRegistro.text.toString(),
                                                "Contraseña" to contraseñaRegistro.text.toString(),
                                                "FechaNacimiento" to fechaRegistro.text.toString(),
                                                "TieneRestaurante" to false,
                                                "FotoPerfil" to uri.toString()
                                            )
                                        )
                                    val inicio = Intent(this, PantallaPrincipal::class.java).apply {
                                        putExtra("email", usuarioRegistro.text.toString())
                                    }
                                    startActivity(inicio)
                                    MotionToast.createToast(
                                        this,
                                        "Operacion Exitosa",
                                        "Registro exitoso",
                                        MotionToast.TOAST_SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        null
                                    )
                                    finish()
                                }
                            }
                        }else{
                            MotionToast.createToast(this,"Operacion Fallida", "Algo salio mal",MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                        }
                    }

                }
            }
        }
    }
    private val PICK_IMAGE_REQUEST = 1
    private var uri: Uri? = null
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data
            if (uri != null) {
                this?.let {
                    Glide.with(it)
                        .load(uri)
                        .circleCrop()
                        .into(ImagenFotoPerfil)
                }
            }
        }
    }
    private fun fecha () {
        fechaRegistro.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    fechaRegistro.setText(selectedDate)
                }, year, month, day)
            datePickerDialog.show()
        }
    }
    private fun Verificar(): Boolean {
        var verificado = true

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val correo = usuarioRegistro.text.toString()
        if (correo.isEmpty()) {
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "Ingresar correo", MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        } else if (!correo.matches(emailPattern.toRegex())) {
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "Correo inválido", MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        }

        val contraseña = contraseñaRegistro.text.toString()
        if (contraseña.isEmpty()) {
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "Ingresar contraseña", MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        } else if (contraseña.length < 6) {
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "La contraseña debe tener al menos 6 caracteres", MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        }

        val nombre = NombreRegistro.text.toString()
        if (nombre.isEmpty()) {
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "Ingresar nombre", MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        } else if (nombre.length < 3) {
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "El nombre debe tener más de 3 caracteres", MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        }

        val fecha = fechaRegistro.text.toString()
        if (fecha.isEmpty()) {
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "Ingresar fecha de nacimiento", MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        }

        return verificado
    }
}
