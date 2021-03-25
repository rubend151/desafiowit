package com.example.desafio2.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.desafio2.model.City

class MainViewModel: ViewModel() {
    //I created a mutable list to change the recycleview data automatically
    var citiesMutableList = MutableLiveData<ArrayList<City>>()
    var newCitiesList = arrayListOf<City>()

    fun addCity(city: City){
        if(!newCitiesList.contains(city)) {
            newCitiesList.add(0, city)
            citiesMutableList.postValue(newCitiesList)
        }
    }
}