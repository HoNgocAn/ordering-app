package com.example.orderingapp.helper;

import android.content.Context
import android.widget.Toast

import com.example.orderingapp.model.FoodModel


class ManagementCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItem(item: FoodModel) {
        var listFood = getListCart()
        val existAlready = listFood.any { it.title == item.title }
        val index = listFood.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listFood[index].numberInCart = item.numberInCart
        } else {
            listFood.add(item)
        }
        tinyDB.putListObject("CartList", listFood)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<FoodModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun minusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listFood[position].numberInCart > 1) {
            listFood[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listFood)
        listener.onChanged()
    }

    fun plusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        listFood[position].numberInCart++
        tinyDB.putListObject("CartList", listFood)
        listener.onChanged()
    }

    fun removeItem(cartItems: ArrayList<FoodModel>, index: Int, tinyDB: TinyDB, listener: ChangeNumberItemsListener) {
        if (index >= 0 && index < cartItems.size) {
            cartItems.removeAt(index)
            tinyDB.putListObject("CartList", cartItems) // Lưu lại danh sách sau khi xóa
            listener.onChanged()
        }
    }
    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.price * item.numberInCart
        }
        return fee
    }
}