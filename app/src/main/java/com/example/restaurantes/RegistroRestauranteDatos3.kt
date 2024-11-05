package com.example.restaurantes
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos3.Adelante3


class RegistroRestauranteDatos3 : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val db = FirebaseFirestore.getInstance()
    private lateinit var usuario: Usuario


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registro_restaurante_datos3, container, false)
    }
    lateinit var storageReference : StorageReference
    private var marcador: Marker? = null
    private var ubicacionSeleccionada: LatLng? = null
    private var marcadorActual: Marker? = null
    private var ubicacionActual: LatLng? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.Mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Adelante3.setOnClickListener {
            // Asegúrate de que al menos una ubicación esté disponible
            val ubicacion: LatLng? = ubicacionSeleccionada ?: ubicacionActual

            if (ubicacion != null) {
                (activity as RegistroRestaurante).agregarUbicacion(ubicacion)

                arguments?.let {
                    usuario = it.getParcelable("usuario")!!
                }

                val restaurante = (activity as RegistroRestaurante).restaurante
                val horariosMap = convertirHorariosAHashMap(restaurante.horarioAtencion)

                Adelante3.isEnabled = false
                storageReference = FirebaseStorage.getInstance().getReference("Restaurante/${restaurante.nombreRestaurante}")

                storageReference.putFile(restaurante.logo).addOnSuccessListener { snapshot ->
                    val uriTask: Task<Uri> = snapshot.getStorage().getDownloadUrl()
                    uriTask.addOnSuccessListener { uri ->
                        db.collection("Usuarios").document(usuario.correo.toString()).set(
                            hashMapOf(
                                "Nombre" to usuario.nombre,
                                "Contraseña" to usuario.contraseña,
                                "FechaNacimiento" to usuario.fechaNacimiento,
                                "TieneRestaurante" to true,
                                "Restaurante" to restaurante.nombreRestaurante
                            )
                        )
                        val categoriasMap: MutableMap<String, Any> = HashMap()
                        for (categoria in restaurante.categoria) {
                            categoriasMap.putAll(categoria.toMap())
                        }
                        db.collection("Restaurante").document(restaurante.nombreRestaurante).set(
                            hashMapOf(
                                "nombreRestaurante" to restaurante.nombreRestaurante,
                                "celularReferencia" to restaurante.celularreferencia,
                                "logo" to restaurante.logo.toString(),
                                "ubicacion" to restaurante.ubicacion,
                                "horarioAtencion" to horariosMap,
                                "categoria" to categoriasMap,
                                "servicios" to restaurante.servicios
                            )
                        )
                        (activity as RegistroRestaurante).mostrar()
                        val inicio = Intent(this.context, PantallaPrincipal::class.java).apply {
                            putExtra("email", usuario.correo.toString())
                        }
                        startActivity(inicio)
                        requireActivity().finish()
                    }
                }
            } else {
                // Manejo de caso cuando no hay ubicación seleccionada ni actual
                Toast.makeText(requireContext(), "Por favor selecciona una ubicación", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun convertirHorariosAHashMap(listaHorarios: ArrayList<Horario>): List<HashMap<String, String>> {
        return listaHorarios.map { horario ->
            hashMapOf(
                "dia" to horario.Dia,
                "abrir" to horario.Abrir,
                "cerrar" to horario.Cerrar
            )
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
                ubicacionActual = LatLng(location.latitude, location.longitude)

                // Si ya hay un marcador de ubicación actual, lo eliminamos
                if (marcadorActual != null) {
                    marcadorActual!!.remove()
                }

                // Agregar el marcador de la ubicación actual
                marcadorActual = mMap.addMarker(MarkerOptions().position(ubicacionActual!!).title("Ubicación Actual"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual!!, 15f)) // Zoom nivel 15
            }
        }
    }

    override fun onMapClick(latLng: LatLng) {
        // Si ya hay un marcador de ubicación seleccionada, lo movemos
        if (marcador != null) {
            marcador!!.position = latLng
        } else {
            // Si no hay marcador, creamos uno nuevo
            marcador = mMap.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
        }

        // Guardar la nueva ubicación
        ubicacionSeleccionada = latLng

        // Eliminar el marcador de la ubicación actual
        if (marcadorActual != null) {
            marcadorActual!!.remove()
            marcadorActual = null
        }
    }

    override fun onMapLongClick(p0: LatLng) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                obtenerUbicacionActual()
            }
        }
    }
}
