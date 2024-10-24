package com.example.restaurantes

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_inicio.usuario
import kotlinx.android.synthetic.main.activity_registro.EsRestaurante
import kotlinx.android.synthetic.main.activity_registro.NombreRegistro
import kotlinx.android.synthetic.main.activity_registro.Registrar
import kotlinx.android.synthetic.main.activity_registro.contraseñaRegistro
import kotlinx.android.synthetic.main.activity_registro.fechaRegistro
import kotlinx.android.synthetic.main.activity_registro.usuarioRegistro
import www.sanju.motiontoast.MotionToast
import java.util.Calendar

class Registro : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var nombre: String
    val db = FirebaseFirestore.getInstance()

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
        Registrar.setOnClickListener {
            if (Verificar()){
                if (EsRestaurante.isChecked){
                    val user = Usuario(
                        NombreRegistro.text.toString(),
                        usuarioRegistro.text.toString(),
                        contraseñaRegistro.text.toString(),
                        fechaRegistro.text.toString(),
                        true
                    )
                    //hablar si se va modificar (aca cabiar consultar si el correo ya existe y seguna  eso verificar y esto poner al final)
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuarioRegistro.text.toString(),contraseñaRegistro.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            val inicio = Intent(this, RegistroRestaurante::class.java).apply {
                                putExtra("usuario", user)
                            }
                            finish()
                            startActivity(inicio)
                        }else{
                            MotionToast.createToast(this,"Operacion Fallida", "Algo salio mal",MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                        }
                    }
                }else{
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuarioRegistro.text.toString(),contraseñaRegistro.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            db.collection("Usuarios").document(usuarioRegistro.text.toString()).set(
                                hashMapOf(
                                    "Nombre" to NombreRegistro.text.toString(),
                                    "Contraseña" to contraseñaRegistro.text.toString(),
                                    "FechaNacimiento" to fechaRegistro.text.toString(),
                                    "TieneRestaurante" to false,
                                )
                            )
                            val inicio = Intent(this, PantallaPrincipal:: class.java)
                            startActivity(inicio)
                            MotionToast.createToast(this,"Operacion Exitosa", "Registro exitoso",MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                            finish()
                        }else{
                            MotionToast.createToast(this,"Operacion Fallida", "Algo salio mal",MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                        }
                    }

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
