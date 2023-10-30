package app.kotlin.calculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.kotlin.calculator.R
import app.kotlin.calculator.ui.theme.borderAndTextColor
import app.kotlin.calculator.ui.theme.gradient1
import app.kotlin.calculator.ui.theme.gradient2

val listImgSrc = listOf(
    R.drawable.numpad_ac,
    R.drawable.numpad_del,
    R.drawable.numpad_percentage_1,
    R.drawable.numpad_divide,
    R.drawable.numpad_7,
    R.drawable.numpad_8,
    R.drawable.numpad_9,
    R.drawable.numpad_multi,
    R.drawable.numpad_4,
    R.drawable.numpad_5,
    R.drawable.numpad_6,
    R.drawable.numpad_minus,
    R.drawable.numpad_1,
    R.drawable.numpad_2,
    R.drawable.numpad_3,
    R.drawable.numpad_plus,
    R.drawable.numpad_round,
    R.drawable.numpad_0,
    R.drawable.numpad_decimal,
    R.drawable.numpad_result
)

@Composable
fun Numpad(
    listImgSrc: List<Int>,
    listOfAction: List<() -> Unit>
) {
    val height = LocalConfiguration.current.screenWidthDp * 5 / 4
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 0..4)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (j in (i * 4)..(i * 4 + 3))
                    NumpadButton(
                        imgSrc = listImgSrc[j],
                        action = listOfAction[j]
                    )
            }
    }
}


@Composable
fun NumpadButton(
    imgSrc: Int,
    action: () -> Unit
) {
    Button(
        onClick = action,
        modifier = Modifier
            .width(76.dp)
            .height(76.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        gradient1,
                        gradient2
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = 2.dp, color = borderAndTextColor),
        contentPadding = PaddingValues(all = 12.dp)
    ) {
        Image(
            painter = painterResource(id = imgSrc),
            contentDescription = "",
            modifier = Modifier
                .width(56.dp)
                .height(56.dp),
        )
    }
}