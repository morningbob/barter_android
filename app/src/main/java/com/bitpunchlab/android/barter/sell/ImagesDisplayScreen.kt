package com.bitpunchlab.android.barter.sell

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.reflect.Field
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.staticProperties
import kotlin.reflect.full.valueParameters

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T> ImagesDisplayScreen(viewModel: T) {

    val viewModelCollection = viewModel!!::class.members

    var images: MutableStateFlow<List<Bitmap>?>

    val field = viewModel.javaClass.getDeclaredField("_imagesDisplay")
    field.isAccessible = true
    Log.i("images display", "accessed field ${field.name}")
    images = field.get(viewModel) as MutableStateFlow<List<Bitmap>?>//.collectAsState()
    Log.i("images display", images.value?.size.toString())

    val viewModelShouldPopImages = viewModel.javaClass.getDeclaredField("_shouldPopImages")//.get(viewModel) as MutableStateFlow<Boolean>
    viewModelShouldPopImages.isAccessible = true
    val shouldPopImages = (viewModelShouldPopImages.get(viewModel) as MutableStateFlow<Boolean>).collectAsState()

    val viewModelUpdateShouldPopImages = viewModel.javaClass.declaredMethods.first { it.name == "updateShouldPopImages" }
    viewModelUpdateShouldPopImages.isAccessible = true
    val viewModelUpdateShouldDisplayImages = viewModel.javaClass.declaredMethods.first { it.name == "updateShouldDisplayImages" }
    viewModelUpdateShouldDisplayImages.isAccessible = true

    LaunchedEffect(key1 = shouldPopImages.value) {
        Log.i("images launched effect", "should pop images changed ${shouldPopImages.value}")
        if (shouldPopImages.value) {
            viewModelUpdateShouldPopImages.invoke(viewModel, false)
            //navController.popBackStack()
        }
    }
    Dialog(onDismissRequest = {  }) {

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
                        .padding(start = 40.dp, end = 40.dp, top = 40.dp, bottom = 40.dp),
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
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    for (each in images.value!!) {
                        item {
                            Image(
                                bitmap = each.asImageBitmap(),
                                contentDescription = "product image",
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)

                            )
                        }
                    }
                }
            }
        }
    }

}
/*
@Composable
fun <T> ImageDisplayDialog(viewModel: T) {

    Dialog(onDismissRequest = {  }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 40.dp, bottom = 40.dp),
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
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                for (each in images.value!!) {
                    item {
                        Image(
                            bitmap = each.asImageBitmap(),
                            contentDescription = "product image",
                            modifier = Modifier
                                .fillMaxWidth(0.8f)

                        )
                    }
                }
            }
        }
    }
}


inline fun <reified T: Any> getValue(name: String): MutableStateFlow<Boolean> {
    return T::class.java.getDeclaredField(name).get(null) as MutableStateFlow<Boolean>
}

 */
