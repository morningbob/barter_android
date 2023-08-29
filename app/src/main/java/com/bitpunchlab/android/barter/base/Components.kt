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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.util.ImageHandler.loadImage
import com.bitpunchlab.android.barter.util.SellingDuration
import com.bitpunchlab.android.barter.util.parseDateTime
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.file.attribute.BasicFileAttributeView
import kotlin.math.min
import kotlin.reflect.KClass

@Composable
fun CustomTextField(modifier: Modifier = Modifier, label: String, textValue: String, onChange: (String) -> Unit,
                    hide: Boolean = false, ) {
    OutlinedTextField(
        label = {
                Text(
                    text = label,
                    fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                    color = BarterColor.textGreen
                )
        },
        value = textValue,
        onValueChange = { value: String -> onChange.invoke(value) },
        visualTransformation = if (!hide) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.then(modifier),
        textStyle = TextStyle(fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp),
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
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.round_corner_shape)),

    ) {
        Text(
            text = label,
            fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
            color = BarterColor.green,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TitleText(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = dimensionResource(id = R.dimen.title_font_size).value.sp,
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
        fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
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
             shape = RoundedCornerShape(dimensionResource(id = R.dimen.round_corner_shape))
         ) {
             Column(
                 modifier = Modifier
                     .background(BarterColor.lightGreen)
                     .padding(
                         vertical = dimensionResource(id = R.dimen.dialog_top_bottom_padding),
                         horizontal = dimensionResource(id = R.dimen.dialog_left_right_padding)
                     ),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Text(
                     text = title,
                     fontSize = dimensionResource(id = R.dimen.dialog_title_font_size).value.sp,
                     fontWeight = FontWeight.Bold,
                     color = BarterColor.textGreen,
                     modifier = Modifier
                         .padding()
                 )
                 Text(
                     text = message,
                     fontSize = dimensionResource(id = R.dimen.dialog_title_font_size).value.sp,
                     color = BarterColor.green,
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(top = dimensionResource(id = R.dimen.dialog_title_top_bottom_padding)),
                 )
                 Row(
                     modifier = Modifier
                         .padding(top = dimensionResource(id = R.dimen.dialog_top_bottom_padding))
                 ) {
                     DialogButton(
                         title = positiveText, onPositive)
                     if (negativeText != null && onNegative != null) {
                         DialogButton(
                             title = negativeText, onNegative,
                             modifier = Modifier
                                 .padding(start = dimensionResource(id = R.dimen.dialog_button_padding))
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
            fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomTextArea(modifier: Modifier = Modifier, textInput: String, onChange: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    val shape = RoundedCornerShape(dimensionResource(id = R.dimen.round_corner_shape))
    val borderModifier = Modifier.border(dimensionResource(id = R.dimen.border_stroke), BarterColor.lightBlue, shape)

    BasicTextField(
        value = textInput,
        singleLine = false,
        onValueChange = { onChange.invoke(it) },
        interactionSource = interactionSource,
        modifier = borderModifier
            .background(BarterColor.lightYellow2, shape)
            .verticalScroll(rememberScrollState(), enabled = true)
            .height(dimensionResource(id = R.dimen.textarea_height))
            .then(modifier),
        textStyle = TextStyle(fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp),
        ) {
            innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = textInput,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = false,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                    start = dimensionResource(id = R.dimen.textarea_content_padding),
                    end = dimensionResource(id = R.dimen.textarea_content_padding)
                ),
            )
    }
}

@Composable
fun CustomCircularProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.progress_bar_size)),
        color = BarterColor.darkGreen,
        strokeWidth = dimensionResource(id = R.dimen.progress_bar_stroke)
    )
}

@Composable
fun MenuBlock(modifier: Modifier, barHeight: Dp = 100.dp, barWidth: Dp = 15.dp,
              content: @Composable() () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimensionResource(id = R.dimen.menu_bar_left_padding))
            .then(modifier)
    ) {
        Row(
            modifier = Modifier

        ) {
            Column(
                modifier = Modifier
                    .background(BarterColor.darkGreen)
                    .width(barWidth)
                    .height(barHeight)
                    .padding(end = dimensionResource(id = R.dimen.menu_bar_right_padding))
            ) {

            }
            content()
        }
    }
}

@Composable
fun CancelCross(modifier: Modifier = Modifier, onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = dimensionResource(id = R.dimen.cancel_cross_padding),
                top = dimensionResource(id = R.dimen.cancel_cross_padding)
            )
            .then(modifier),
        horizontalArrangement = Arrangement.End
    ) {
        Image(
            painter = painterResource(id = R.mipmap.cross),
            contentDescription = "cancel icon",
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.cancel_cross_size))
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
fun ChoiceButton(modifier: Modifier = Modifier, title: String, enable: Boolean = true,
                 onClick: () -> Unit, ) {
    Button(
        onClick = { onClick.invoke() },
        modifier = Modifier.then(modifier),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = BarterColor.green
        ),
        enabled = enable

    ) {
        Text(
            text = title,
            fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
            color = Color.White,
        )
    }
}

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    borderWidth: Dp = dimensionResource(id = R.dimen.border_stroke),
    borderColor: Color = BarterColor.textGreen,
    content: @Composable() () -> Unit,
) {
    Card(
        modifier = Modifier
            .then(modifier),
        elevation = dimensionResource(id = R.dimen.card_elevation),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.round_corner_shape)),
        border = BorderStroke(borderWidth, borderColor)
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
                .padding(bottom = dimensionResource(id = R.dimen.product_row_top_bottom_padding))
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
                        .padding(start = dimensionResource(id = R.dimen.mild_start_padding))
                        .width(dimensionResource(id = R.dimen.product_row_image_size))
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(horizontal = dimensionResource(id = R.dimen.mild_start_padding))

                ) {
                    Text(
                        text = product.name,
                        color = BarterColor.textGreen,
                        fontSize = dimensionResource(id = R.dimen.product_row_title_font_size).value.sp,
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.product_row_item_padding))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.product_row_item_padding)),
                    ) {
                        Text(
                            text = stringResource(R.string.category_row),
                            color = BarterColor.textGreen,
                            fontSize = dimensionResource(id = R.dimen.product_row_title_font_size).value.sp,
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
                            fontSize = dimensionResource(id = R.dimen.product_row_title_font_size).value.sp,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.product_row_item_padding)),
                    ) {
                        Text(
                            text = stringResource(R.string.selling_duration_row),
                            color = BarterColor.textGreen,
                            fontSize = dimensionResource(id = R.dimen.product_row_title_font_size).value.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.product_row_item_padding)),
                    ) {
                        Text(
                            text = "${product.duration} days",
                            fontSize = dimensionResource(id = R.dimen.product_row_title_font_size).value.sp,
                            color = BarterColor.textGreen,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.product_row_item_padding)),
                    ) {
                        Text(
                            text = stringResource(R.string.asking_products_row),
                            color = BarterColor.textGreen,
                            fontSize = dimensionResource(id = R.dimen.product_row_title_font_size).value.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }

            }
            Row(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(top = dimensionResource(id = R.dimen.product_row_item_padding)),
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
                fontSize = dimensionResource(id = R.dimen.date_time_font_size).value.sp,
                color = Color.Blue
            )
        } else {
            Text(
                text = stringResource(R.string.date_and_time_info_not_available),
                fontSize = dimensionResource(id = R.dimen.date_time_font_size).value.sp,
                color = Color.Blue
            )
        }
    }
}

// we basically load all the required images in local database mgr
// and display the pics from local database.
// if we can't get the bitmap, we load it here
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
        TitleRow(
            iconId = R.mipmap.hammer,
            title = stringResource(R.string.bid_details),
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.basic_screen_top_padding))
        )
/*
        Image(
            painter = painterResource(id = R.mipmap.hammer),
            contentDescription = "Bid's icon",
            modifier = Modifier
                .padding(top = 5.dp)
                .width(120.dp)
        )


 */
        if (images.isNotEmpty()) {
            Image(
                bitmap = images[0].image!!.asImageBitmap(),
                contentDescription = "product's image",
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.basic_bid_screen_item_padding))
                    .width(dimensionResource(id = R.dimen.detail_image_size))
            )
        } else {
            Image(
                painter = painterResource(id = R.mipmap.imageplaceholder),
                contentDescription = "image placeholder",
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.basic_bid_screen_item_padding))
                    .width(dimensionResource(id = R.dimen.detail_image_size))
            )
        }

        Text(
            text = productName,
            fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
            color = BarterColor.textGreen,
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.basic_bid_screen_item_padding))
        )
        Text(
            text = productCategory,
            fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
            color = BarterColor.textGreen,
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.basic_bid_screen_item_padding))
        )
        CustomButton(
            label = stringResource(R.string.show_all_images),
            onClick = {
                updateShouldDisplayImages(true)
            },
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.basic_bid_screen_item_padding))
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
            fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.accept_bid_details_element_padding))
        )
        if (productOfferingImages.isNotEmpty()) {
            Image(
                bitmap = productOfferingImages[0].image!!.asImageBitmap(),
                contentDescription = "first product image",
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.detail_image_size))
                    .padding(top = dimensionResource(id = R.dimen.accept_bid_details_element_padding))
            )
            CustomButton(
                label = stringResource(R.string.view_images),
                onClick = {
                    prepareImages(productOfferingImages)
                    updateShouldDisplayImages(true)
                },
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.accept_bid_details_element_padding))
            )
        } else {
            Text(
                text = "Image not available",
                color = BarterColor.textGreen,
                fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.basic_screen_top_padding))
            )
        }
        Text(
            text = stringResource(R.string.product_in_exchange_row),
            color = Color.Black,
            fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.accept_bid_details_element_padding))
        )
        if (productInExchangeImages.isNotEmpty()) {
            Image(
                bitmap = productInExchangeImages[0].image!!.asImageBitmap(),
                contentDescription = "first product image",
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.detail_image_size))
                    .padding(top = dimensionResource(id = R.dimen.detail_image_top_padding))
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
                    .padding(top = dimensionResource(id = R.dimen.accept_bid_details_element_padding))
            )
        } else {
            Text(
                text = "Image not available",
                color = BarterColor.textGreen,
                fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.accept_bid_details_element_padding))
            )
        }
    }
}


@Composable
fun TitleRow(modifier: Modifier = Modifier, iconId: Int, title: String) {
    Row(
        modifier = Modifier
            .height(dimensionResource(id = R.dimen.title_row_height))
            .padding()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = title,
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.title_row_height))
                .padding(end = dimensionResource(id = R.dimen.detail_image_top_padding))
        )
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                text = title,
                fontSize = dimensionResource(id = R.dimen.title_row_font_size).value.sp,
                fontWeight = FontWeight.Bold,
                color = BarterColor.textGreen,
                modifier = Modifier,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun ChooseTitlesRow(modifier: Modifier = Modifier, contentDes: String, iconId: Int, titleOne: String, titleTwo: String,
                    onClickOne: () -> Unit, onClickTwo: () -> Unit, bidMode: Boolean) {

    Row(
        modifier = Modifier
            .height(dimensionResource(id = R.dimen.title_row_height))
            .padding()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = contentDes,
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.title_row_height))
                .padding(end = dimensionResource(id = R.dimen.thumbnail_right_padding))
        )
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = titleOne,
                fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                fontWeight = FontWeight.Bold,
                color = if (bidMode) BarterColor.textGreen else BarterColor.medGreen,
                modifier = Modifier
                    .clickable { onClickOne.invoke() },
                textAlign = TextAlign.Start
            )
            Text(
                text = titleTwo,
                fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                fontWeight = FontWeight.Bold,
                color = if (bidMode) BarterColor.medGreen else BarterColor.textGreen,
                modifier = Modifier
                    .clickable { onClickTwo.invoke() }
                    .padding(start = dimensionResource(id = R.dimen.choose_title_row_right_padding)),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun LoadedImageOrPlaceholder(modifier: Modifier = Modifier, imageUrls: List<String>, contentDes: String,
        loadImageViewModel: LoadImageViewModel = remember {
            LoadImageViewModel()
        }) {

    Column(
        modifier = Modifier
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var image by remember {
            mutableStateOf<Bitmap?>(null)
        }
        if (imageUrls.isNotEmpty()) {
            LaunchedEffect(key1 = imageUrls[0]) {
                image = loadImageViewModel.loadImageDatabase(imageUrls[0])
            }
            if (image != null) {
                Log.i("load image or placeholder", "loaded bitmap from storage")
                Image(
                    bitmap = image!!.asImageBitmap(),
                    contentDescription = contentDes,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else { // imageUrls is not empty && image = null
                Log.i("load image or placeholder", "didn't load bitmap yet")
                var bitmap by remember {
                    mutableStateOf<Bitmap?>(null)
                }
                LaunchedEffect(key1 = imageUrls[0]) {
                    Log.i("load image or placeholder", "loading bitmap from cloud")
                    bitmap = CoroutineScope(Dispatchers.IO).async {
                        loadImage(url = imageUrls[0])
                    }.await()
                    if (bitmap != null) {
                        ImageHandler.saveImageExternalStorage(imageUrls[0], bitmap!!)
                        Log.i("load image or placeholder", "saved image from cloud")
                    }
                }

                // here, I check the bitmap again, since I can't return composable inside
                // launch effect
                if (bitmap != null) {
                    Log.i("load image or placeholder", "loaded bitmap from cloud")
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = contentDes,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                } else {
                    PlaceholderImage()
                }
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

