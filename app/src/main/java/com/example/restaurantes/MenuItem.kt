package com.example.restaurantes

import android.os.Parcel
import android.os.Parcelable

data class MenuItem(
    val identificador: String? = null,
    val calificacion: Double? = null,
    val nomPlato: String? = null,
    val nomRestaurante: String? = null,
    val precio: Double? = null,
    val restauranteId: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(identificador)
        parcel.writeValue(calificacion)
        parcel.writeString(nomPlato)
        parcel.writeString(nomRestaurante)
        parcel.writeValue(precio)
        parcel.writeString(restauranteId)
    }

    companion object CREATOR : Parcelable.Creator<MenuItem> {
        override fun createFromParcel(parcel: Parcel): MenuItem {
            return MenuItem(parcel)
        }

        override fun newArray(size: Int): Array<MenuItem?> {
            return arrayOfNulls(size)
        }
    }
}