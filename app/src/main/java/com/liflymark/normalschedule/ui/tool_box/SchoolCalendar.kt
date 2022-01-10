package com.liflymark.normalschedule.ui.tool_box

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SingleDayCircle(){
    Surface(
        shape = RoundedCornerShape(10)
    ) {
        // Just a fake data... a Pair of Int and String
        val tableData = (1..100).mapIndexed { index, item ->
            index to "Item $index"
        }
        // Each cell of a column must have the same weight.
        val column1Weight = .3f // 30%
        val column2Weight = .7f // 70%
        // The LazyColumn will be our table. Notice the use of the weights below
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(16.dp)) {
            // Here is the header
            item {
                Column(Modifier.background(Color.Gray)) {

                }
            }
            // Here are all the lines of your table.
            items(tableData) {
                val (id, text) = it
                Row(Modifier.fillMaxWidth()) {

                }
            }
        }
    }
}

@Composable
fun RowScope.RowCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        modifier = Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun ColumnScope.ColumnCell(
    text: String,
    weight: Float
){
    Text(
        text = text,
        modifier = Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarPreview(){
    SingleDayCircle()
}