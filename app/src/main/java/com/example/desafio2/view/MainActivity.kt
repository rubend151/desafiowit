package com.example.desafio2.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.desafio2.Global.Api
import com.example.desafio2.R
import com.example.desafio2.adapters.CityAdapter
import com.example.desafio2.databinding.ActivityMainBinding
import com.example.desafio2.model.City
import com.example.desafio2.viewmodel.MainViewModel
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var viewManager = LinearLayoutManager(this)
    private lateinit var viewModel: MainViewModel
    private var locationHandler : LocationManager? = null
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue_2))
        }

        viewModel = MainViewModel()

        initializeAdapter()

        //Add the default cities (Lisboa, Madrid, Paris, Berlim, Copenhaga, Roma, Londres, Dublin, Praga, and Viena)
        addFullData()

        initializePermissions()
    }

    private fun initializePermissions() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !==
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        } else {
            setLocationListener()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        setLocationListener()
                        Toast.makeText(this, "Permissão aceite!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission negada!", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun initializeAdapter(){
        binding.recycler.layoutManager = viewManager
        observeData()
    }

    private fun observeData(){
        viewModel.citiesMutableList.observe(this, Observer{
            binding.recycler.adapter = CityAdapter(it, this, client)
        })
    }

    private fun setLocationListener() {
        //Get the user current location
        locationHandler = getSystemService(LOCATION_SERVICE) as LocationManager?
        try {
            locationHandler?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (ex: SecurityException) {
            Log.d("error", "Security exception")
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val request = Request.Builder()
                .cacheControl(
                    CacheControl.Builder()
                        .maxAge(120, TimeUnit.SECONDS)
                        .build())
                .url("https://api.openweathermap.org/data/2.5/weather?lat=" + location.latitude + "&lon=" + location.longitude +"&appid=" + Api.KEY + "&units=metric")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("error", "I wasn't possible to retreive the city data")
                }
                override fun onResponse(call: Call, response: Response) {
                    try{
                        val jobject = JSONObject(response.body()!!.string())
                        viewModel.addCity(City(label= jobject.get("name").toString()))
                    } catch (e: JSONException) {
                        Log.d("error", "I wasn't possible to parse the data")
                    }
                }
            })
        }
    }

    private fun addFullData(){
        val arr = arrayOf("Lisboa", "Porto", "Paris", "Berlim", "Copenhaga", "Roma", "Londres", "Dublin", "Praga", "Viena");
        for (item in arr) {
            viewModel.addCity(City(label=item))
        }
        binding.recycler.adapter?.notifyDataSetChanged()
    }
}