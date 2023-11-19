package app.kotlin.calculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.kotlin.calculator.ui.theme.HighlightedTypo
import app.kotlin.calculator.ui.theme.NormalTypo
import app.kotlin.calculator.ui.theme.borderAndTextColor
import app.kotlin.calculator.ui.theme.fontScale
import app.kotlin.calculator.ui.theme.gradient1
import app.kotlin.calculator.ui.theme.gradient2


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    isCompleted: Boolean
) {
    //(Screen width - button original size * buttonScale *4) / 5 (3 gutters and 2 margins)
    val offSet: Float =
        (LocalConfiguration.current.screenWidthDp - ((76.dp).buttonScale().value) * 4) / 5

    //Height of display = screenHeight - numpad height
    val numpadHeight: Int = LocalConfiguration.current.screenWidthDp * 5 / 4
    val screenHeight: Int = LocalConfiguration.current.screenHeightDp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .height(screenHeight.dp - numpadHeight.dp)
            .padding(start = offSet.dp, end = offSet.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            gradient1,
                            gradient2
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(width = 2.dp, color = borderAndTextColor),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            content = {}
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.End
        ) {
            //Expression
            item {
                Text(
                    text = expression,
                    style = if (isCompleted)
                        NormalTypo.copy(fontSize = NormalTypo.fontSize.fontScale())
                    else HighlightedTypo.copy(fontSize = HighlightedTypo.fontSize.fontScale())
                )
            }

            //The result of expression

            item {
                LazyRow(contentPadding = PaddingValues(start = 10.dp)) {
                    item {
                        Text(
                            text = result,
                            style = if (isCompleted)
                                HighlightedTypo.copy(fontSize = HighlightedTypo.fontSize.fontScale())
                            else NormalTypo.copy(fontSize = NormalTypo.fontSize.fontScale()),
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
