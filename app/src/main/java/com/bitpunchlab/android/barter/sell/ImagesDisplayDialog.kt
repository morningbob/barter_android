package com.bitpunchlab.android.barter.sell

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ImagesDisplayDialog(images: StateFlow<List<ProductImage>>, shouldDismiss: Boolean, ) {

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
                            .width(40.dp)
                            .clickable {
                                //viewModelUpdateShouldPopImages.invoke(viewModel, true)
                                //viewModelUpdateShouldDisplayImages.invoke(viewModel, false)
                            },
                    )
                }

                //if (shouldDisplayFullImage && imageToShow != null) {
                //    showImage(image = imageToShow!!)
                //} else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {

                        items(images.value, { image -> image.id }) { item ->
                            //item {
                            // remember updated state will renew whenever there is
                            // recomposition, so , if an item is deleted, another
                            // item's position changed, the remember will remember the
                            // new position.
                            val currentItem by rememberUpdatedState(item)
                            /*
                            val dismissState = rememberDismissState(
                                confirmStateChange = {
                                    // delete the item in view model
                                    viewModelDeleteImages.invoke(viewModel, currentItem)
                                    true
                                }
                            )

                             */
                            Image(
                                bitmap = item.image.asImageBitmap(),
                                contentDescription = "product image",
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .clickable {
                                        //showImage(item)
                                        //imageToShow = item
                                        //shouldDisplayFullImage = true
                                    },
                            )

                        }

                    }  // end of lazy column
                //}


            } // end of column
        } // end of surface
    } // end of dialog
}