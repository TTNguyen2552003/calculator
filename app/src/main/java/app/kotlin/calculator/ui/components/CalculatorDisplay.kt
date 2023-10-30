package app.kotlin.calculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import app.kotlin.calculator.ui.theme.HighlightedTypo
import app.kotlin.calculator.ui.theme.NormalTypo
import app.kotlin.calculator.ui.theme.borderAndTextColor
import app.kotlin.calculator.ui.theme.gradient1
import app.kotlin.calculator.ui.theme.gradient2

@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    isCompleted: Boolean
) {
    val offSet = (LocalConfiguration.current.screenWidthDp - 76 * 4) / 5
    val height = LocalConfiguration.current.screenWidthDp * 4 / 5
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
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
                .align(alignment = Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.End
        ) {
            //Expression
            item {
                Text(
                    text = expression,
                    style = if (isCompleted)
                        NormalTypo
                    else HighlightedTypo
                )
            }

            //The result of expression
            item {
                Text(
                    text = result,
                    style = if (isCompleted)
                        HighlightedTypo
                    else NormalTypo,
                )
            }
        }
    }
}