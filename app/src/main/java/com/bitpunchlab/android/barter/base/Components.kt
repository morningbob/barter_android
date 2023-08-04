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
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.SellingDuration
import com.bitpunchlab.android.barter.util.parseDateTime
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.file.attribute.BasicFileAttributeView
import kotlin.reflect.KClass

@Composable
fun CustomTextField(modifier: Modifier = Modifier, label: String, textValue: String, onChange: (String) -> Unit,
                    hide: Boolean = false, ) {
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
fun CustomButton(modifier: Modifier = Modifier,
                 label: String, onClick: () -> Unit, enable: Boolean = true, ) {
    OutlinedButton(
        onClick = { onClick.invoke() },
        modifier = Modifier
            .then(modifier),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = BarterColor.lightGreen,
            disabledContentColor = BarterColor.lightGray,
            disabledBackgroundColor = BarterColor.lightGray,
            contentColor = BarterColor.green
            ),
        enabled = enable,
        shape = RoundedCornerShape(10.dp),

    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = BarterColor.green,
            textAlign = TextAlign.Center
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
fun ContentText(content: String, textSize: TextUnit = 18.sp,
                textColor: Color = BarterColor.textGreen, modifier: Modifier) {
    Text(
        text = content,
        color = textColor,
        fontSize = textSize,
        modifier = Modifier
            .then(modifier)
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
fun CancelCross(modifier: Modifier = Modifier, onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp, top = 20.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.End
    ) {
        Image(
            painter = painterResource(id = R.mipmap.cross),
            contentDescription = "cancel icon",
            modifier = Modifier
                .width(40.dp)
                .clickable {
                    onCancel()
                },
        )
    }
}


@Composable
fun <T: Any> CustomDropDown(title: String, shouldExpand: Boolean,
    onClickButton: () -> Unit,
    onClickItem: (T) -> Unit,
    onDismiss: () -> Unit, items: List<T>, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.then(modifier)
    ) {
        ChoiceButton(
            title = title,
            onClick = { onClickButton.invoke() },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = shouldExpand,
            onDismissRequest = { onDismiss.invoke() },
            modifier = Modifier
            ) {

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
fun CustomCard(modifier: Modifier = Modifier,
               content: @Composable() () -> Unit,) {
    Card(
        modifier = Modifier
            .then(modifier),
        elevation = 10.dp,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(3.dp, BarterColor.textGreen)
    ) {
        content()
    }
}

@Composable
fun ProductRow(modifier: Modifier = Modifier, contentModifier: Modifier = Modifier,
               product: ProductOffering, onClick: (ProductOffering) -> Unit,
               backgroundColor: Color) {

    CustomCard(modifier) {
        Column(
            modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(bottom = 15.dp)
                .clickable { onClick.invoke(product) }
                .then(contentModifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoadedImageOrPlaceholder(
                    imageUrls = product.images,
                    contentDes = "product's image",
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .width(100.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(start = 20.dp, end = 20.dp)

                ) {
                    Text(
                        text = product.name,
                        color = BarterColor.textGreen,
                        fontSize = 21.sp,
                        modifier = Modifier
                            .padding(top = 10.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.category_row),
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = product.category,
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.selling_duration_row),
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = "${product.duration} days",
                            fontSize = 18.sp,
                            color = BarterColor.textGreen,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.asking_products_row),
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }

            }
            Row(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                DateTimeInfo(
                    dateTimeString = product.dateCreated,
                )
            }
        }
    }
}

@Composable
fun DateTimeInfo(dateTimeString: String, modifier: Modifier = Modifier) {

    val dateTime = parseDateTime(dateTimeString)

    Column(
        modifier = Modifier
            .then(modifier)

    ) {
        if (dateTime != null) {
            Text(
                text = "${dateTime.month} ${dateTime.dayOfMonth}, ${dateTime.year}  ${dateTime.hour}:${dateTime.minute}",
                fontSize = 18.sp,
                color = Color.Blue
            )
        } else {
            Text(
                text = stringResource(R.string.date_and_time_info_not_available),
                fontSize = 18.sp,
                color = Color.Blue
            )
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
fun BasicBidScreen(productName: String, productCategory: String,
                   images: SnapshotStateList<ProductImageToDisplay>,
                   updateShouldDisplayImages: (Boolean) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.mipmap.hammer),
            contentDescription = "Bid's icon",
            modifier = Modifier
                .padding(top = 5.dp)
                .width(120.dp)
        )

        if (images.isNotEmpty()) {
            Image(
                bitmap = images[0].image!!.asImageBitmap(),
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
            text = productName,
            fontSize = 20.sp,
            color = BarterColor.textGreen,
            modifier = Modifier
                .padding(top = 30.dp)
        )
        Text(
            text = productCategory,
            fontSize = 20.sp,
            color = BarterColor.textGreen,
            modifier = Modifier
                .padding(top = 30.dp)
        )
        CustomButton(
            label = "Show All Images",
            onClick = {
                updateShouldDisplayImages(true)
            },
            modifier = Modifier
                .padding(top = 25.dp)
        )
    }
}

@Composable
fun BasicRecordScreen(modifier: Modifier = Modifier, productOfferingImages: List<ProductImageToDisplay>,
    productInExchangeImages: List<ProductImageToDisplay>, prepareImages: (List<ProductImageToDisplay>) -> Unit,
    updateShouldDisplayImages: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.product_offered_row),
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 15.dp)
        )
        if (productOfferingImages.isNotEmpty()) {
            Image(
                bitmap = productOfferingImages[0].image!!.asImageBitmap(),
                contentDescription = "first product image",
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 20.dp)
            )
            CustomButton(
                label = stringResource(R.string.view_images),
                onClick = {
                    prepareImages(productOfferingImages)
                    updateShouldDisplayImages(true)
                },
                modifier = Modifier
                    .padding(top = 20.dp)
            )
        } else {
            Text(
                text = "Image not available",
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 15.dp)
            )
        }
        Text(
            text = stringResource(R.string.product_in_exchange_row),
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 15.dp)
        )
        if (productInExchangeImages.isNotEmpty()) {
            Image(
                bitmap = productInExchangeImages[0].image!!.asImageBitmap(),
                contentDescription = "first product image",
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 20.dp)
            )
            CustomButton(
                label = stringResource(id = R.string.view_images),
                onClick = {
                    prepareImages(productInExchangeImages)
                    updateShouldDisplayImages(true)
                    //prepareImagesDisplay.invoke(viewModel, productInExchangeImages)
                    //updateShouldDisplayImages.invoke(viewModel, true)
                },
                modifier = Modifier
                    .padding(top = 20.dp)
            )
        } else {
            Text(
                text = "Image not available",
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 15.dp)
            )
        }
    }
}


@Composable
fun TitleRow(modifier: Modifier = Modifier, iconId: Int, title: String) {
    Row(
        modifier = Modifier
            .height(80.dp)
            .padding()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = title,
            modifier = Modifier
                .width(80.dp)
                .padding(end = 20.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                text = title,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = BarterColor.textGreen,
                modifier = Modifier,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun LoadedImageOrPlaceholder(modifier: Modifier = Modifier, imageUrls: List<String>, contentDes: String, ) {
    Column(
        modifier = Modifier
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (imageUrls.isNotEmpty()) {
            val bitmap = LoadImage(url = imageUrls[0])
            if (bitmap.value != null) {
                Image(
                    bitmap = bitmap.value!!.asImageBitmap(),
                    contentDescription = contentDes,
                    modifier = Modifier
                        .fillMaxWidth()
                        //.then(modifier)
                )
            } else {
                PlaceholderImage()
            }
        } else {
            PlaceholderImage()
        }
    }
}

@Composable
fun PlaceholderImage() {
    Image(
        painter = painterResource(id = R.mipmap.imageplaceholder),
        contentDescription = "placeholder image",
    )
}
/*
@Composable
fun <T> BasicRecordScree(bidWithDetails: BidWithDetails, viewModel: T, modifier: Modifier = Modifier) {

    val viewModelCollection = viewModel!!::class.members

    val viewModelProductOfferingImages = viewModel.javaClass.getDeclaredField("_productOfferingImages")
    viewModelProductOfferingImages.isAccessible = true
    val viewModelProductInExchangeImages = viewModel.javaClass.getDeclaredField("_productInExchangeImages")
    viewModelProductInExchangeImages.isAccessible = true
    val productOfferingImages by (viewModelProductOfferingImages.get(viewModel)
        as MutableStateFlow<List<ProductImage>>).collectAsState()
    val productInExchangeImages by (viewModelProductInExchangeImages.get(viewModel)
        as MutableStateFlow<List<ProductImage>>).collectAsState()
    val updateShouldDisplayImages = viewModel.javaClass.declaredMethods.first { it.name == "updateShouldDisplayImages" }
    updateShouldDisplayImages.isAccessible = true
    val updateShouldPopImages = viewModel.javaClass.declaredMethods.first { it.name == "updateShouldPopImages" }
    updateShouldPopImages.isAccessible = true
    //val updateShouldDisplayDetails = viewModel.javaClass.declaredMethods.first { it.name == "updateShouldDisplayDetails" }
    //updateShouldDisplayDetails.isAccessible = true
    val prepareImagesDisplay = viewModel.javaClass.declaredMethods.first { it.name == "prepareImagesDisplay" }
    prepareImagesDisplay.isAccessible = true

    Column(
        modifier = Modifier
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.mipmap.recorddetails),
            contentDescription = "record details icon",
            modifier = Modifier
                .width(120.dp)
        )
        Text(
            text = "Product offered:",
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 15.dp)
        )
        if (productOfferingImages.isNotEmpty()) {
            Image(
                bitmap = productOfferingImages[0].image.asImageBitmap(),
                contentDescription = "first product image",
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 20.dp)
            )
            CustomButton(
                label = "View Images",
                onClick = {
                    prepareImagesDisplay.invoke(viewModel, productOfferingImages)
                    updateShouldDisplayImages.invoke(viewModel, true)
                },
                modifier = Modifier
                    .padding(top = 20.dp)
            )
        } else {
            Text(
                text = "Image not available",
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 15.dp)
            )
        }
        Text(
            text = "Product in exchange:",
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 15.dp)
        )
        if (productInExchangeImages.isNotEmpty()) {
            Image(
                bitmap = productInExchangeImages[0].image.asImageBitmap(),
                contentDescription = "first product image",
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 20.dp)
            )
            CustomButton(
                label = "View Images",
                onClick = {
                    prepareImagesDisplay.invoke(viewModel, productInExchangeImages)
                    updateShouldDisplayImages.invoke(viewModel, true)
                },
                modifier = Modifier
                    .padding(top = 20.dp)
            )
        } else {
            Text(
                text = "Image not available",
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 15.dp)
            )
        }
    }
}

 */


