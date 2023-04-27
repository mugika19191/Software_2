package com.example.final_proyect

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import org.json.JSONArray
import java.io.ByteArrayOutputStream


class Perfil_Activity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var perfil:ImageButton
    var REQUEST_IMAGE_CAPTURE=1
    var indiceGlobal:Int=0
    var posx:Double=0.0
    var posy:Double=0.0
    var latitude:Double=0.0
    var longitude:Double=0.0
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val ss:String = intent.getStringExtra("NAME").toString()
        prepare_user(ss)
        var perfil = findViewById<ImageButton>(R.id.edit)
        var subir = findViewById<Button>(R.id.subir)
        var up = findViewById<ImageButton>(R.id.up)
        var down = findViewById<ImageButton>(R.id.down)
        var mapaB = findViewById<Button>(R.id.mapB)
        updateInterface()
        updateFotoPerfil()
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
/*
        val fragment = secondFragment()
        val bundle = Bundle()
        bundle.putString("name",intent.getStringExtra("NAME").toString())
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment,fragment).commit()
*/
        //val botmenu = findViewById<BottomNavigationView>(R.id.navigation)
        //val controller = findNavController(R.id.fragment)
        //botmenu.setupWithNavController(controller)






        /*val bundle = Bundle(intent.getStringExtra("NAME").toString(), ++)
        bundle.putString()
        val trans = supportFragmentManager.beginTransaction()
        val frag = secondFragment()
        trans.replace(R.id.fragment, frag )
        trans.addToBackStack(null)
        trans.commit()
        var text =
*/
        subir.setOnClickListener {
            sacar_foto()
        }

        perfil.setOnClickListener {
            select_image()
       }
        up.setOnClickListener {
            sumarIndice()
        }
        down.setOnClickListener {
            restarIndice()
        }

        mapaB.setOnClickListener{
            setContentView(R.layout.maps)
            createFragment()
        }

    }
    private fun createFragment(){
        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap:GoogleMap) {
        map=googleMap
        createMarker()
    }
    private fun createMarker(){
        val coordinates = LatLng (posx, posy)
        val marker: MarkerOptions = MarkerOptions() .position (coordinates).title("Mi playa favorita!")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom (coordinates, 18f),
            4000,
            null
        )
    }
    fun prepare_user(name:String){
        val nombre = findViewById<TextView>(R.id.nombrePerfil)
        nombre.setText(name)
       /* ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.MANAGE_MEDIA),1)
        val room = Room.databaseBuilder(this, DBPruebas::class.java, "Usuario").build()
        lifecycleScope.launch {

            var user=room.daoUsuario().getByName(name)
/*
            if (user.img!=""){

                image.setImageURI(user.img.toUri())
            }
            */
        }*/
    }
    fun updateUserImg(uriIMG : String){
        val room = Room.databaseBuilder(this, DBPruebas::class.java, "Usuario").build()
        val name = findViewById<TextView>(R.id.nombrePerfil)

        lifecycleScope.launch {

            var user=room.daoUsuario().getByName(name.text.toString())
            val newUser= Usuario(user.name,user.pass,uriIMG)
            room.daoUsuario().update(newUser)
        }
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri->
        if (uri!=null){
            //hemos elegido imagen
            var foto = findViewById<ImageView>(R.id.profile_image)
            foto.setImageURI(uri)
            val drawable = foto.drawable
            val bitmap = drawable.toBitmap()
            subirFotoPerfil(encodeImage(bitmap))
            //updateUserImg(uri.toString())
        }
        else{
            // no hay imagen
        }
    }
    val startPhoto=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result: ActivityResult->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            val imagebitmap=intent?.extras?.get("data") as Bitmap
            val text = encodeImage(imagebitmap)
            subirFoto(text)
        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

   fun select_image(){
       pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    fun sacar_foto(){
        startPhoto.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }
    fun subirFoto(text:String?){
        val url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/subir_foto.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                updateInterface()
            },
            {error ->

            })
        {
            //Press Ctr + O to find getParams
            override fun getParams(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap.put("usuario",findViewById<TextView>(R.id.nombrePerfil).text.toString())
                hashMap.put("foto", text.toString())
                fetchLocation()
                hashMap.put("posx",latitude.toString())
                hashMap.put("posy",longitude.toString())
                return hashMap
            }
        }
        queue.add(stringRequest)
    }
    fun updateInterface(){
        val url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/get_fotos.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->

                //val jsonObject = JSONObject(response.toString())

                //val Jarray = jsonObject.getJSONArray(jsonObject.getString("nombre"))

                var jArray = JSONArray(response)

                if (jArray.length()!=0){
                    if(indiceGlobal>=jArray.length()){
                        indiceGlobal=0
                    }
                    else if(indiceGlobal<0){
                        indiceGlobal=jArray.length()-1
                    }
                    var jsonObj = jArray.getJSONObject(indiceGlobal)
                    //for (i in 0..jArray.length()-1) {
                    findViewById<TextView>(R.id.foto_counter).text = "Numero de fotos: " + (jArray.length())
                    decodeIMG(jsonObj.getString("foto"))
                    posx=jsonObj.getString("posx").toDouble()
                    posy=jsonObj.getString("posy").toDouble()
                }
                //}
            },
            {error ->

            })
        {
            //Press Ctr + O to find getParams
            override fun getParams(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap.put("usuario",findViewById<TextView>(R.id.nombrePerfil).text.toString())
                return hashMap
            }
        }
        queue.add(stringRequest)

    }

    private fun decodeIMG(string: String) {
        val imageBytes = Base64.decode(string, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        findViewById<ImageView>(R.id.image_cat).setImageBitmap(image)

    }
    private fun decodeIMGProfile(string: String) {
        val imageBytes = Base64.decode(string, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        findViewById<ImageView>(R.id.profile_image).setImageBitmap(image)

    }

    private fun sumarIndice(){
        indiceGlobal++
        updateInterface()
    }

    private fun restarIndice(){
        indiceGlobal--
        updateInterface()
    }
    private fun subirFotoPerfil(text:String?){
        val url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/subir_foto_perfil.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->

            },
            {error ->

            })
        {
            //Press Ctr + O to find getParams
            override fun getParams(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap.put("usuario",findViewById<TextView>(R.id.nombrePerfil).text.toString())
                hashMap.put("foto",text.toString())
                return hashMap
            }
        }
        queue.add(stringRequest)

    }
    fun updateFotoPerfil(){
        val url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/imugica037/WEB/get_foto_perfil.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->

                //val jsonObject = JSONObject(response.toString())

                //val Jarray = jsonObject.getJSONArray(jsonObject.getString("nombre"))

                var jArray = JSONArray(response)

                if (jArray.length()!=0){
                    var jsonObj = jArray.getJSONObject(0)
                    //for (i in 0..jArray.length()-1) {
                    decodeIMGProfile(jsonObj.getString("foto"))
                }
                //}
            },
            {error ->

            })
        {
            //Press Ctr + O to find getParams
            override fun getParams(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap.put("usuario",findViewById<TextView>(R.id.nombrePerfil).text.toString())
                return hashMap
            }
        }
        queue.add(stringRequest)

    }
    private fun fetchLocation () {
        val task : Task<Location> = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions( this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }
       task.addOnSuccessListener {
           if (it!=null){
               latitude=it.latitude
               longitude=it.longitude
           }
       }
    }
}
