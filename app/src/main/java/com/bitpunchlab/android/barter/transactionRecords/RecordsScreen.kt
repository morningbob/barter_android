package com.bitpunchlab.android.barter.transactionRecords

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.LoadImage
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun RecordsScreen(navController: NavHostController,
    recordsViewModel: RecordsViewModel = remember { RecordsViewModel() } ) {
    
    val acceptedRecords by recordsViewModel.acceptedRecords.collectAsState()
    val productOfferedImage by recordsViewModel.productOfferedImage.collectAsState()


    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.mipmap.records),
                contentDescription = "Transaction Records icon",
                modifier = Modifier
                    .width(120.dp)
                    .padding(top = 40.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .padding(top = 30.dp, start = 50.dp, end = 50.dp)
            ) {
                items(acceptedRecords,  { accepted -> accepted.acceptId }) { record ->
                    RecordRow(record = record)
                }
            }
        }
    }
}

@Composable
fun RecordRow(record: AcceptBid) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),

    ) {


        if (record.acceptProductInConcern.images.isNotEmpty()) {
            val bitmap = LoadImage(url = record.acceptProductInConcern.images[0])
            if (bitmap.value != null) {
                Image(
                    bitmap = bitmap.value!!.asImageBitmap(),
                    contentDescription = "the product's image",
                    modifier = Modifier
                        .width(80.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.mipmap.imageplaceholder),
                    contentDescription = "placeholder image",
                    modifier = Modifier
                        .width(80.dp)
                )
            }
        } else {
            Image(
                painter = painterResource(id = R.mipmap.imageplaceholder),
                contentDescription = "placeholder image",
                modifier = Modifier
                    .width(80.dp)
            )
        }
        Column(
            modifier = Modifier,
                //.padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "The product for bidding: ",
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 0.dp)
            )
            Text(
                text = record.acceptBid.bidProduct!!.productName,
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            Text(
                text = "The product exchanged: ",
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            Text(
                text = record.acceptProductInConcern.name,
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            Text(
                text = "Date",
                color = BarterColor.textGreen,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
    }
}