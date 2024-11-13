package com.example.restaurantes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_inicio.BotonGoogle
import kotlinx.android.synthetic.main.activity_inicio.contraseña
import kotlinx.android.synthetic.main.activity_inicio.entrar
import kotlinx.android.synthetic.main.activity_inicio.textoRegistrate
import kotlinx.android.synthetic.main.activity_inicio.usuario
import www.sanju.motiontoast.MotionToast

class InicioSesion : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        val inicio = Intent(this, MainActivity:: class.java)
        startActivity(inicio)
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ColorTexto()
        IniciarSesion()
        textoRegistrate.setOnClickListener {
            val inicio = Intent(this, Registro:: class.java)
            startActivity(inicio)
            finish()
        }

    }

    private fun ColorTexto () {
        val text1 = "No tienes cuenta "
        val text2 = "Regístrate"

        val spannable = SpannableString("$text1$text2")

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#646464")),
            0, text1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#EF9106")),
            text1.length, spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textoRegistrate.text = spannable
    }
    private val GOOGLE = 100
    private fun IniciarSesion() {
        entrar.setOnClickListener {
            if (Verificar()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario.text.toString(),contraseña.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        val inicio = Intent(this, PantallaPrincipal:: class.java).apply {
                            putExtra("email", usuario.text.toString())
                        }
                        startActivity(inicio)
                        MotionToast.createToast(this,"Operacion Exitosa", "Inicio exitoso",MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                        finish()
                    }else{
                        MotionToast.createToast(this,"Operacion Fallida", "Verificar credenciales",MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                    }
                }
            }
        }
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, googleConf)
        BotonGoogle.setOnClickListener {
            googleClient.signOut()
            googleSignIn.launch(googleClient.signInIntent)
        }
    }
    private lateinit var googleClient: GoogleSignInClient

    private val googleSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val email = account.email
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("Usuarios").document(email.toString()).get().addOnCompleteListener { documentTask ->
                    if (documentTask.isSuccessful) {
                        val document = documentTask.result
                        if (document != null && document.exists()) {
                            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val inicio = Intent(this, PantallaPrincipal::class.java).apply {
                                        putExtra("email", email)
                                    }
                                    startActivity(inicio)
                                    finish()
                                    MotionToast.createToast(this, "Operación Exitosa", "Inicio exitoso", MotionToast.TOAST_SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
                                } else {
                                    MotionToast.createToast(this, "Operación Fallida", "Algo salió mal", MotionToast.TOAST_ERROR,
                                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
                                }
                            }
                        } else {
                            val registroIntent = Intent(this, Registro::class.java).apply {
                                putExtra("email", email)
                                putExtra("nombre", account.displayName)
                            }
                            finish()
                            startActivity(registroIntent)
                        }
                    } else {
                        MotionToast.createToast(this, "Operación Fallida", "Error al consultar Firestore", MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
                    }
                }
            }
        }catch (e: ApiException){
            MotionToast.createToast(this,"Operacion Fallida", "Algo salio mal",MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
        }
    }
    private fun Verificar (): Boolean {
        var verificado = true;
        if (usuario.text.toString().isEmpty()){
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "Ingresar correo", MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        }
        if (contraseña.text.toString().isEmpty()){
            verificado = false
            MotionToast.createToast(this, "Operación Fallida", "Ingresar contraseña", MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
        }
        return verificado
    }
}
