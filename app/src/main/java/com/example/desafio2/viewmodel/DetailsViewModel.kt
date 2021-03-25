package com.example.desafio2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.desafio2.model.Detail

class DetailsViewModel: ViewModel() {
    var newDetailsList = arrayListOf<Detail>()

    fun addDetail(detail: Detail){
        newDetailsList.add(detail)
    }
}