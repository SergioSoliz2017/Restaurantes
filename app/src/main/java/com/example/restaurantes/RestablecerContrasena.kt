package com.example.restaurantes
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth


class RestablecerContrasena : AppCompatActivity() {

    private lateinit var mEditTextEmail: EditText
    private lateinit var mButtonResetPass: Button
    private lateinit var correo: String
    private lateinit var mAuth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_contrasena)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        mEditTextEmail = findViewById(R.id.textEmail)
        mButtonResetPass = findViewById(R.id.btnResetPass)
        mAuth = FirebaseAuth.getInstance()

        mButtonResetPass.setOnClickListener {
            correo = mEditTextEmail.text.toString()
            if (correo.isNotEmpty()) {
                resetPassword()
            } else {
                Toast.makeText(this, "Debe ingresar el email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(){
        mAuth.setLanguageCode("es")
        mAuth.sendPasswordResetEmail(correo).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "El email fue enviado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ocurrio algun error al enviar el correo", Toast.LENGTH_SHORT).show()
            }
        }
    }


}