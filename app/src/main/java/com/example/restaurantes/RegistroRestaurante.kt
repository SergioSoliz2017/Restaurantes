package com.example.restaurantes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_inicio.textoRegistrate
import kotlinx.android.synthetic.main.activity_registro.EsRestaurante
import kotlinx.android.synthetic.main.activity_registro.Registrar

class RegistroRestaurante : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_restaurante)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)



    }


}
