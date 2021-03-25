package com.example.desafio2.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio2.Global.Api
import com.example.desafio2.Global.Intents
import com.example.desafio2.R
import com.example.desafio2.model.City
import com.example.desafio2.view.DetailsActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class CityAdapter(
    private val arrayList: ArrayList<City>,
    val context: Context,
    val client: OkHttpClient
): RecyclerView.Adapter<CityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityAdapter.ViewHolder {
        var root = LayoutInflater.from(parent.context).inflate(R.layout.city_view,parent,false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: CityAdapter.ViewHolder, position: Int) {
        holder.bind(arrayList.get(position))
    }

    override fun getItemCount(): Int {
        if(arrayList.size==0){
            Toast.makeText(context,"A lista encontra-se vazia!",Toast.LENGTH_LONG).show()
        }
        return arrayList.size
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(city: City){
            view.findViewById<TextView>(R.id.label).text = city.label
            view.setOnClickListener {
                val request = Request.Builder()
                    //I used a cache control to prevent the data from being re-generated everytime the user checks the city data
                    .cacheControl(CacheControl.Builder()
                        .maxAge(120, TimeUnit.SECONDS)
                        .build())
                    .url("https://api.openweathermap.org/data/2.5/weather?q=" + city.label + "&appid=" + Api.KEY +"&units=metric")
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("error", "I wasn't possible to retreive the weather data")
                    }
                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val jobject = JSONObject(response.body()!!.string())
                            val intent = Intent(context, DetailsActivity::class.java)
                            //I sent each element via an intent, the keys can be easily changed by accessing the Intents class at the Global package
                            intent.putExtra(
                                Intents.TEMP,
                                jobject.getJSONObject("main").get("temp").toString()
                            )
                            intent.putExtra(
                                Intents.TEMP_MAX,
                                jobject.getJSONObject("main").get("temp_max").toString()
                            )
                            intent.putExtra(
                                Intents.TEMP_MIN,
                                jobject.getJSONObject("main").get("temp_min").toString()
                            )
                            intent.putExtra(
                                Intents.WIND,
                                jobject.getJSONObject("wind").get("speed").toString()
                            )
                            intent.putExtra(
                                Intents.HUMIDITY,
                                jobject.getJSONObject("main").get("humidity").toString()
                            )
                            context.startActivity(intent)
                        }  catch (e: JSONException) {
                            Log.d("error", "I wasn't possible to parse the data")
                        }
                    }
                })
            }
        }
    }
}