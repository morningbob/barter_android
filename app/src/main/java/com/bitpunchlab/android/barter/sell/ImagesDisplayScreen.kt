package com.bitpunchlab.android.barter.sell

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.staticProperties
import kotlin.reflect.full.valueParameters

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T> ImagesDisplayScreen(viewModel: T) {

    val viewModelCollection = viewModel!!::class.members

    var images: MutableStateFlow<List<ProductImage>?>

    val field = viewModel.javaClass.getDeclaredField("_imagesDisplay")
    field.isAccessible = true
    //Log.i("images display", "accessed field ${field.name}")
    //images = field.get(viewModel) as MutableStateFlow<List<Bitmap>?>//.collectAsState()
    images = field.get(viewModel) as MutableStateFlow<List<ProductImage>?>
    //Log.i("images display", images.value?.size.toString())

    val viewModelShouldPopImages = viewModel.javaClass.getDeclaredField("_shouldPopImages")//.get(viewModel) as MutableStateFlow<Boolean>
    viewModelShouldPopImages.isAccessible = true
    val shouldPopImages = (viewModelShouldPopImages.get(viewModel) as MutableStateFlow<Boolean>).collectAsState()

    val viewModelUpdateShouldPopImages = viewModel.javaClass.declaredMethods.first { it.name == "updateShouldPopImages" }
    viewModelUpdateShouldPopImages.isAccessible = true
    val viewModelUpdateShouldDisplayImages = viewModel.javaClass.declaredMethods.first { it.name == "updateShouldDisplayImages" }
    viewModelUpdateShouldDisplayImages.isAccessible = true
    val viewModelDeleteImages = viewModel.javaClass.declaredMethods.first { it.name == "deleteImage" }
    viewModelDeleteImages.isAccessible = true

    var shouldDisplayFullImage by remember { mutableStateOf(false) }
    var imageToShow : ProductImage? by remember { mutableStateOf(null) }

    var shouldShowConfirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = shouldPopImages.value) {
        Log.i("images launched effect", "should pop images changed ${shouldPopImages.value}")
        if (shouldPopImages.value) {
            viewModelUpdateShouldPopImages.invoke(viewModel, false)
            //navController.popBackStack()
        }
    }

    @Composable
    fun ConfirmDeleteDialog(image: ProductImage) {
        CustomDialog(
            title = "Remove Confirmation",
            message = "Are you sure to remove the image?",
            positiveText = "Delete",
            negativeText = "Cancel",
            onDismiss = { shouldShowConfirmDelete = false },
            onPositive = { Log.i("confirm delete", "confirmed") },
            onNegative = { Log.i("confirm delete", "cancelled") } )
    }

    @Composable
    fun showImage(image: ProductImage) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            Alignment.Center
        ) {
            Image(
                bitmap = image.image.asImageBitmap(),
                contentDescription = "A product image",
                modifier = Modifier
                    .width(400.dp)
            )
            Card(
                modifier = Modifier
                    //.fillMaxWidth()
                    .background(Color.Transparent),
                //.padding(30.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Gray)
                        .padding(15.dp)
                        .clickable {
                            shouldShowConfirmDelete = true
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.remove),
                        contentDescription = "remove icon",
                        modifier = Modifier
                            .width(25.dp)
                            .background(Color.Gray)
                    )
                    Text(
                        text = "Remove",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .background(Color.Gray)
                        ,
                        color = BarterColor.lightGreen
                    )
                }
                if (shouldShowConfirmDelete) {
                    ConfirmDeleteDialog(image)
                }
            }
        }

    }

    Dialog(
        onDismissRequest = {  },
        //properties = DialogProperties(decorFitsSystemWindows = true),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 25.dp, top = 25.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.cross),
                        contentDescription = "cancel icon",
                        modifier = Modifier
                            .width(50.dp)
                            .clickable {
                                viewModelUpdateShouldPopImages.invoke(viewModel, true)
                                viewModelUpdateShouldDisplayImages.invoke(viewModel, false)
                            },
                    )
                }
                if (shouldDisplayFullImage && imageToShow != null) {
                    showImage(image = imageToShow!!)
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {

                        items(images.value ?: listOf(), { image -> image.id }) { item ->
                            //item {
                            // remember updated state will renew whenever there is
                            // recomposition, so , if an item is deleted, another
                            // item's position changed, the remember will remember the
                            // new position.
                            val currentItem by rememberUpdatedState(item)
                            val dismissState = rememberDismissState(
                                confirmStateChange = {
                                    // delete the item in view model
                                    viewModelDeleteImages.invoke(viewModel, currentItem)
                                    true
                                }
                            )
                            Image(
                                bitmap = item.image.asImageBitmap(),
                                contentDescription = "product image",
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .clickable {
                                        //showImage(item)
                                        imageToShow = item
                                        shouldDisplayFullImage = true
                                    },
                            )

                        }

                    }  // end of lazy column
                }
            } // end of column
        } // end of surface
    } // end of dialog


}
/*


inline fun <reified T: Any> getValue(name: String): MutableStateFlow<Boolean> {
    return T::class.java.getDeclaredField(name).get(null) as MutableStateFlow<Boolean>
}
/*
                            SwipeToDismiss(
                                state = dismissState,
                                background = {
                                    val direction =
                                        dismissState.dismissDirection ?: return@SwipeToDismiss
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Red)
                                    )
                                },
                                dismissThresholds = { direction ->
                                    FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.25f else 0.5f)
                                },
                                directions = setOf(
                                    DismissDirection.StartToEnd,
                                    DismissDirection.EndToStart
                                ),

                                dismissContent = {
                                    Card(
                                        elevation = animateDpAsState(
                                            if (dismissState.dismissDirection != null) 4.dp else 0.dp
                                        ).value
                                    ) {

                                        // Layout here
                                    }
                                }
                            )

                             */

 */
