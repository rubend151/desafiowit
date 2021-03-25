package com.example.desafio2.view

import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.desafio2.Global.Drawables
import com.example.desafio2.Global.Intents
import com.example.desafio2.Global.Metrics
import com.example.desafio2.R
import com.example.desafio2.adapters.DetailAdapter
import com.example.desafio2.databinding.DetailsBinding
import com.example.desafio2.model.Detail
import com.example.desafio2.viewmodel.DetailsViewModel

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: DetailsBinding
    private lateinit var viewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        binding = DetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue_2))
        }

        viewModel = DetailsViewModel()

        //Retreive all weather details from the city
        val bundle = intent.extras
        for (key in bundle!!.keySet()) {
            val value = bundle[key]
            val detail = Detail(value.toString() + getMetric(key), key, getDrawable(key))
            viewModel.addDetail(detail)
        }

        binding.gridView.adapter = DetailAdapter(this@DetailsActivity, viewModel.newDetailsList)
    }

    private fun getDrawable(value: String): Int{
       return when (value) {
            Intents.TEMP -> Drawables.TEMP
            Intents.TEMP_MAX -> Drawables.TEMP_MAX
            Intents.TEMP_MIN -> Drawables.TEMP_MIN
            Intents.WIND  -> Drawables.WIND
            Intents.HUMIDITY  -> Drawables.HUMIDITY
            else -> {
                Drawables.TEMP
            }
        }
    }

    private fun getMetric(value: String): String{
        return when (value) {
            Intents.TEMP -> Metrics.CELSIUS
            Intents.TEMP_MAX -> Metrics.CELSIUS
            Intents.TEMP_MIN -> Metrics.CELSIUS
            Intents.WIND  -> Metrics.KMH
            Intents.HUMIDITY  -> Metrics.PERCENTAGE
            else -> {
                return "-"
            }
        }
    }
}