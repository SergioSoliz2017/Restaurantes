package com.example.restaurantes

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_inicio.textoRegistrate

import www.sanju.motiontoast.MotionToast

class Inicio : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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
}
