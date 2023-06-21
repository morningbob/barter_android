package com.bitpunchlab.android.barter.base

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.models.ProductBidding
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productBiddingList.ProductBiddingInfo
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.SellingDuration
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.nio.file.attribute.BasicFileAttributeView
import kotlin.reflect.KClass

@Composable
fun CustomTextField(label: String, textValue: String, onChange: (String) -> Unit,
                    hide: Boolean = false, modifier: Modifier = Modifier) {
    OutlinedTextField(
        label = {
                Text(
                    text = label,
                    color = BarterColor.textGreen
                )
        },
        value = textValue,
        onValueChange = { value: String -> onChange.invoke(value) },
        visualTransformation = if (!hide) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.then(modifier),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = BarterColor.green,
            unfocusedBorderColor = BarterColor.green,
            focusedLabelColor = BarterColor.textGreen,
            unfocusedLabelColor = BarterColor.textGreen,
            cursorColor = BarterColor.green,
            backgroundColor = BarterColor.lightYellow,
            textColor = BarterColor.textGreen
        )
    )
}

@Composable
fun CustomButton(label: String, onClick: () -> Unit, enable: Boolean = true,
                 modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = { onClick.invoke() },
        modifier = Modifier
            //.border(BorderStroke(2.dp, BarterColor.green), RoundedCornerShape(15.dp))
            .then(modifier),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = BarterColor.lightGreen,
            disabledContentColor = BarterColor.orange,
            disabledBackgroundColor = BarterColor.orange,
            contentColor = BarterColor.green
            ),
        enabled = enable,
        shape = RoundedCornerShape(10.dp),

    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = BarterColor.green
        )
    }
}

@Composable
fun TitleText(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 35.sp,
        color = BarterColor.textGreen,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.then(modifier)
    )
}

@Composable
fun ErrorText(error: String, modifier: Modifier = Modifier) {
    Text(
        text = error,
        fontSize = 18.sp,
        color = BarterColor.errorRed,
        modifier = Modifier.then(modifier)
    )
}

@Composable
fun CustomDialog(
    title: String, message: String, positiveText: String, negativeText: String? = null,
    onDismiss: () -> Unit, onPositive: () -> Unit, onNegative: (() -> Unit)? = null) {
    Dialog(
        onDismissRequest = { onDismiss.invoke() }
    ) {
         Card(
             shape = RoundedCornerShape(12.dp)
         ) {
             Column(
                 modifier = Modifier
                     .background(BarterColor.lightGreen)
                     .padding(top = 30.dp, bottom = 30.dp, start = 50.dp, end = 50.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Text(
                     text = title,
                     fontSize = 25.sp,
                     fontWeight = FontWeight.Bold,
                     color = BarterColor.textGreen,
                     modifier = Modifier
                         .padding()
                 )
                 Text(
                     text = message,
                     fontSize = 20.sp,
                     color = BarterColor.green,
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(top = 30.dp),
                 )
                 Row(
                     modifier = Modifier
                         .padding(top = 30.dp)
                 ) {
                     DialogButton(
                         title = positiveText, onPositive)
                     if (negativeText != null && onNegative != null) {
                         DialogButton(
                             title = negativeText, onNegative,
                             modifier = Modifier
                                 .padding(start = 25.dp)
                         )
                     }
                 }

             }
         }
    }
}

@Composable
fun DialogButton(title: String,
                 onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = { onClick.invoke() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = BarterColor.buttonBrown
        ),
        modifier = Modifier.then(modifier)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            //fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun CustomCircularProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(80.dp),
        color = BarterColor.green,
        strokeWidth = 10.dp
    )
}

@Composable
fun <T: Any> CustomDropDown(title: String, shouldExpand: Boolean,
    onClickButton: () -> Unit,
    onClickItem: (T) -> Unit,
    onDismiss: () -> Unit, items: List<T>, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.then(modifier)
    ) {
        //var expand = shouldExpand
        ChoiceButton(
            title = title,
            onClick = { onClickButton.invoke() }
        )

        DropdownMenu(
            expanded = shouldExpand,
            onDismissRequest = { onDismiss.invoke() }) {

            items.map { item ->
                val nameField = item.javaClass.getDeclaredField("label")
                nameField.isAccessible = true
                DropdownMenuItem(onClick = { onClickItem(item) }) {
                    Text(
                        text = nameField.get(item)!!.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun ChoiceButton(title: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = { onClick.invoke() },
        modifier = Modifier.then(modifier),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = BarterColor.green
        )

    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.White,
        )
    }
}

@Composable
fun ProductRowDisplay(product: ProductOffering, onClick: (ProductOffering) -> Unit,
            modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .then(modifier),
            //.clickable { onClick.invoke(product) },
            //.background(BarterColor.lightBlue),
        elevation = 10.dp,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(3.dp, BarterColor.textGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarterColor.lightBlue)
                .padding(start = 20.dp, end = 20.dp)
                .clickable { onClick.invoke(product) }
        ) {
            if (product.images.isNotEmpty()) {
                val imageState = LoadImage(url = product.images.first())
                if (imageState.value != null) {
                    //Log.i("product row", "images is not empty")
                    //Log.i("product row", "images 0 ${product.images[0]}")
                    Image(
                        bitmap = imageState.value!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .width(280.dp)
                            .padding(top = 30.dp)
                    )
                }

            } else {
                Log.i("product row", "images is empty")
            }
            Text(
                text = product.name,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            ) {
                Text(
                    text = "Category: ",
                    color = BarterColor.textGreen,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = product.category,
                    color = BarterColor.textGreen,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            ) {
                Text(
                    text = "Selling Duration: ",
                    color = BarterColor.textGreen,
                    //modifier = Modifier.then(modifier)

                    textAlign = TextAlign.Start
                )
                Text(
                    text = "${product.duration} days",
                    color = BarterColor.textGreen,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
            ) {
                Text(
                    text = "Asking Products: ",
                    color = BarterColor.textGreen,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "${product.askingProducts.askingList[0].name}",
                    color = BarterColor.textGreen,
                )
            }
        }
    }
}

@Composable
fun LoadImage(url: String): MutableState<Bitmap?> {
    val bitmapState: MutableState<Bitmap?> = remember {
        mutableStateOf(null)
    }

    Glide.with(LocalContext.current)
        .asBitmap()
        .placeholder(R.mipmap.imageplaceholder)
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

        })
    return bitmapState
}

@Composable
fun <T : Any> BasicBidScreen(product: ProductBidding?, images: List<ProductImage>, viewModel: T) {
    //val product by ProductBiddingInfo.product.collectAsState()
    //val images by viewModel.imagesDisplay.collectAsState()

    val viewModelMembers = viewModel::class.members
    val viewModelUpdateShouldDisplayImages = viewModelMembers.first { it.name == "updateShouldDisplayImages" }


            Image(
                painter = painterResource(id = R.mipmap.hammer),
                contentDescription = "Bid's icon",
                modifier = Modifier
                    .padding(top = 40.dp)
                    .width(120.dp)
            )
            if (images.isNotEmpty()) {
                Image(
                    bitmap = images[0].image.asImageBitmap(),
                    contentDescription = "product's image",
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .width(200.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.mipmap.imageplaceholder),
                    contentDescription = "image placeholder",
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .width(200.dp)
                )
            }
            Text(
                text = product?.productName ?: "Not Available",
                fontSize = 20.sp,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding(top = 30.dp)
            )
            Text(
                text = product?.productCategory ?: "Not Available",
                fontSize = 20.sp,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding(top = 30.dp)
            )
            CustomButton(
                label = "Show All Images",
                onClick = { viewModelUpdateShouldDisplayImages.call(viewModel, true) },
                modifier = Modifier
                    .padding(top = 25.dp)
            )

}


/*
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://firebasestorage.googleapis.com/v0/b/barter-a84a2.appspot.com/o/images%2F86f01956-2baf-4e9d-9c3f-b08b0a127bb6_0.jpg?alt=media&token=db25a9f0-e674-4be5-b952-73863b9c891c")
                        .setHeader("User-Agent", "Mozilla/5.0")
                        .build()),
                contentDescription = "product's image",
                modifier = Modifier
                    .width(200.dp)
            )

             */