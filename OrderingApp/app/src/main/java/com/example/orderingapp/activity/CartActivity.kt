package com.example.orderingapp.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.orderingapp.R
import com.example.orderingapp.helper.ChangeNumberItemsListener
import com.example.orderingapp.helper.ManagementCart
import com.example.orderingapp.helper.TinyDB
import com.example.orderingapp.model.FoodModel

class CartActivity : BasicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CartScreen(
                ManagementCart(this),
                onBackClick = {
                    finish()
                }

            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CartScreen(
    managementCart: ManagementCart = ManagementCart(LocalContext.current),
    onBackClick: () -> Unit = {}
) {
    val cartItems = remember { mutableStateOf(managementCart.getListCart()) }
    val tax = remember { mutableStateOf(0.0) }
    CalculatorCart(managementCart, tax)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(top = 36.dp)
                .fillMaxWidth(),
            text = "My Cart",
            color = colorResource(R.color.orange),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
            )
        if (cartItems.value.isEmpty()){
            Text(
                text = "Cart Is Empty",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            CartList(cartItems = cartItems.value, managementCart){
                cartItems.value = managementCart.getListCart()
                CalculatorCart(managementCart, tax)
            }
            CartSummary (
                itemTotal = managementCart.getTotalFee(),
                tax = tax.value,
                delivery = 10.0
            )
        }
    }
}

@Composable
fun CartSummary(itemTotal: Double, tax: Double, delivery: Double) {
    val total = itemTotal + tax + delivery
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        ){
            Text(
                text = "Item Total:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black)
            )
            Text(text = "$${"%.2f".format(itemTotal)}")
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        ){
            Text(
                text = "Tax:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black)
            )
            Text(text = "$$tax")
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        ){
            Text(
                text = "Delivery:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black)
            )
            Text(text = "$$delivery")
        }
        Box (Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colorResource(R.color.black))
            .padding(vertical = 16.dp)
        )

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        ){
            Text(
                text = "Total:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black)
            )
            Text(text = "$${"%.2f".format(total)}")
        }
        Button(
            onClick = {},
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = colorResource(R.color.orange)
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Check Out",
                fontSize = 22.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun CartList(
    cartItems: ArrayList<FoodModel>,
    managementCart: ManagementCart,
    onItemChange: () -> Unit
) {
    val context = LocalContext.current
    val tinyDB = remember { TinyDB(context) } // Tạo tinyDB ở đây

    LazyColumn(Modifier.padding(top = 16.dp)) {
        items(cartItems) { item ->
            CartItem(
                cartItems = cartItems,
                item = item,
                managementCart = managementCart,
                tinyDB = tinyDB,  // Truyền tinyDB xuống CartItem
                onItemChange = onItemChange
            )
        }
    }
}
@Composable
fun CartItem(cartItems: ArrayList<FoodModel>, item: FoodModel, managementCart: ManagementCart,   tinyDB: TinyDB, onItemChange: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        val (pic, titleText, feeEachItem, totalEachItem, quantity, deleteBtn) = createRefs()

        AsyncImage(
            model = (item.picUrl),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .background(
                    colorResource(R.color.grey),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(8.dp)
                .constrainAs(pic) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Text(
            text = item.title,
            modifier = Modifier
                .constrainAs(titleText) {
                    start.linkTo(pic.end)
                    top.linkTo(pic.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )

        Text(
            text = "$${item.price}", color = colorResource(R.color.orange),
            modifier = Modifier
                .constrainAs(feeEachItem) {
                    start.linkTo(titleText.start)
                    top.linkTo(titleText.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
        )

        Text(
            text = "$${"%.2f".format(item.numberInCart * item.price)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(totalEachItem) {
                    start.linkTo(titleText.start)
                    bottom.linkTo(pic.bottom)
                }
                .padding(start = 8.dp)
        )

        ConstraintLayout(
            modifier = Modifier
                .width(100.dp)
                .constrainAs(quantity) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .background(
                    colorResource(R.color.grey),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            val (plusCartBtn, minusCartBtn, numberItemTxt) = createRefs()

            Text(
                text = item.numberInCart.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(numberItemTxt) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(28.dp)
                    .background(
                        colorResource(R.color.orange),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .constrainAs(plusCartBtn) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        managementCart.plusItem(
                            cartItems,
                            cartItems.indexOf(item),
                            object : ChangeNumberItemsListener {
                                override fun onChanged() {
                                    onItemChange()
                                }
                            }
                        )
                    }
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(28.dp)
                    .background(
                        colorResource(R.color.orange),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .constrainAs(minusCartBtn) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        managementCart.minusItem(
                            cartItems,
                            cartItems.indexOf(item),
                            object : ChangeNumberItemsListener {
                                override fun onChanged() {
                                    onItemChange()
                                }
                            }
                        )
                    }
            ) {
                Text(
                    text = "-",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Nút delete
        Text(
            text = "Delete",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .constrainAs(deleteBtn) {
                    start.linkTo(quantity.start)
                    end.linkTo(quantity.end)
                    bottom.linkTo(quantity.top, margin = 10.dp)
                }
                .clickable {
                    managementCart.removeItem(
                        cartItems,
                        cartItems.indexOf(item),
                        tinyDB,
                        object : ChangeNumberItemsListener {
                            override fun onChanged() {
                                onItemChange()
                            }
                        }
                    )
                }
        )
    }
}


fun CalculatorCart(managementCart: ManagementCart, tax: MutableState<Double>) {
    val percentTax = 0.02
    tax.value = Math.round((managementCart.getTotalFee() * percentTax) * 100) / 100.0
}