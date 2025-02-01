package com.example.orderingapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.orderingapp.model.CategoryModel
import com.example.orderingapp.model.FoodModel
import com.example.orderingapp.repository.MainRepository

class MainViewModel:ViewModel() {
    private val repository = MainRepository()

    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        return repository.loadCategory()
    }

    fun loadItems(): LiveData<MutableList<FoodModel>>{
        return repository.loadItems()
    }

}