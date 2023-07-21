package com.bitpunchlab.android.barter.base

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ImagesDisplayDialog(images: StateFlow<SnapshotStateList<ProductImage>>, onDismiss: () -> Unit,
    deleteStatus: Int? = null,
    updateDeleteStatus: ((Int) -> Unit)? = null, deleteImage: ((ProductImage) -> Unit)? = null) {
    
    var imageToShow by remember { mutableStateOf<ProductImage?>(null) }

    @Composable
    fun showImage(image: ProductImage, deleteStatus: Int?) {
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
            deleteStatus?.let {
                Card(
                    modifier = Modifier
                        .background(Color.Transparent),
                    shape = RoundedCornerShape(20.dp),
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.Gray)
                            .padding(15.dp)
                            .clickable {
                                updateDeleteStatus?.let {
                                    it(1)
                                    // 1 means need to confirm
                                }
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
                                .background(Color.Gray),
                            color = BarterColor.lightGreen
                        )
                    }
                }
            }
            if (deleteStatus != null && deleteImage != null) {
                if (deleteStatus != 0) {
                    ConfirmDeleteDialog(updateDeleteStatus!!, image, deleteImage
                    ) { imageToShow = null }
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
                            .width(40.dp)
                            .clickable {
                                onDismiss.invoke()
                            },
                    )
                }

                if (imageToShow != null) {
                    showImage(image = imageToShow!!, deleteStatus)
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
                        verticalArrangement = Arrangement.spacedBy(30.dp),
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {

                        items(images.value, { image -> image.id }) { item ->
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
                                        imageToShow = item
                                    },
                            )

                        }

                    }  // end of lazy column
                }

            } // end of column
        } // end of surface
    } // end of dialog
}

// 1 : confirm delete
// 2 : deletion success
// 3 : deletion failed
@Composable
fun ConfirmDeleteDialog(updateDeleteStatus: (Int) -> Unit, imageToBeDeleted: ProductImage,
    deleteImage: (ProductImage) -> Unit, removeShow: () -> Unit) {
    CustomDialog(
        title = "Remove Confirmation",
        message = "Are you sure to remove the image?",
        positiveText = "Delete",
        negativeText = "Cancel",
        onDismiss = { updateDeleteStatus(0) },
        onPositive = {
            deleteImage(imageToBeDeleted)
            updateDeleteStatus(0)
            //onDismiss.invoke()
            removeShow.invoke()
         },
        onNegative = { updateDeleteStatus(0) }
    )
}