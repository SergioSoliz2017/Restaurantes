package com.example.restaurantes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_inicio.BotonGoogle
import kotlinx.android.synthetic.main.activity_inicio.contraseña
import kotlinx.android.synthetic.main.activity_inicio.entrar
import kotlinx.android.synthetic.main.activity_inicio.textoRegistrate
import kotlinx.android.synthetic.main.activity_inicio.usuario
import www.sanju.motiontoast.MotionToast

class InicioSesion : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ColorTexto()
        IniciarSesion()


    }

    private fun ColorTexto () {
        val text1 = "No tienes cuenta "
        val text2 = "Regístrate"

        // Combina los textos
        val spannable = SpannableString("$text1$text2")

        // Aplica color al primer texto (negro)
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#646464")),
            0, text1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Aplica color al segundo texto (naranja)
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#EF9106")),
            text1.length, spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Asigna el texto al TextView
        textoRegistrate.text = spannable
    }
    private val GOOGLE = 100
    private fun IniciarSesion() {
        entrar.setOnClickListener {
            if (Verificar()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario.text.toString(),contraseña.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        val inicio = Intent(this, PantallaPrincipal:: class.java)
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
        BotonGoogle.setOnClickListener {
            val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this,googleConfig)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == GOOGLE){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            Log.d("TAG", "Entraaa")
                            val inicio = Intent(this, PantallaPrincipal:: class.java)
                            startActivity(inicio)
                            MotionToast.createToast(this,"Operacion Exitosa", "Inicio exitoso",MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                            finish()
                        }else{
                            MotionToast.createToast(this,"Operacion Fallida", "Algo salio mal",MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
                        }
                    }
                }
            }catch (e: ApiException){
                MotionToast.createToast(this,"Operacion Fallida", "Algo salio mal",MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
            }

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