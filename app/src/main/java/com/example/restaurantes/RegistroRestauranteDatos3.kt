package com.example.restaurantes
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos3.Adelante3
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos3.Atras3
import www.sanju.motiontoast.MotionToast

class RegistroRestauranteDatos3 : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val db = FirebaseFirestore.getInstance()
    private lateinit var usuario: Usuario


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registro_restaurante_datos3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.Mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Adelante3.setOnClickListener {
            val ubicacion = "me invento"
            (activity as RegistroRestaurante).agregarUbicacion(ubicacion)

            arguments?.let {
                usuario = it.getParcelable("usuario")!!
            }

            val restaurante = (activity as RegistroRestaurante).restaurante
            db.collection("Usuarios").document(usuario.correo.toString()).set(
                hashMapOf(
                    "Nombre" to usuario?.nombre.toString(),
                    "Contraseña" to usuario?.contraseña.toString(),
                    "FechaNacimiento" to usuario?.fechaNacimiento.toString(),
                    "TieneRestaurante" to true,
                )
            ).addOnSuccessListener {
                db.collection("Restaurante").document(restaurante.nombreRestaurante).set(
                    hashMapOf(
                        "nombreRestaurante" to restaurante.nombreRestaurante,
                        "celularReferencia" to restaurante.celularreferencia,
                        "logo" to restaurante.logo,
                        "ubicacion" to restaurante.ubicacion
                    )
                ).addOnSuccessListener {
                    val inicio = Intent(this.context, InicioSesion::class.java)
                    startActivity(inicio)
                    MotionToast.createToast(
                        requireActivity(),
                        "Operación Exitosa",
                        "Registro exitoso",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        null
                    )
                    requireActivity().finish()
                }.addOnFailureListener {
                    MotionToast.createToast(
                        requireActivity(),
                        "Error",
                        "Error al registrar el restaurante",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        null
                    )
                }
            }.addOnFailureListener {
                MotionToast.createToast(
                    requireActivity(),
                    "Error",
                    "Error al registrar el usuario",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    null
                )
            }
        }


        Atras3.setOnClickListener {
            (activity as RegistroRestaurante).cambiarFragmento(2)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        this.mMap.setOnMapClickListener(this)
        this.mMap.setOnMapLongClickListener(this)

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        obtenerUbicacionActual()
    }

    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val ubicacionActual = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(ubicacionActual).title("Ubicación Actual"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 15f)) // Zoom nivel 15
            }
        }
    }

    override fun onMapClick(p0: LatLng) {
    }

    override fun onMapLongClick(p0: LatLng) {

    }

    // Manejo de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                obtenerUbicacionActual()
            }
        }
    }
}
