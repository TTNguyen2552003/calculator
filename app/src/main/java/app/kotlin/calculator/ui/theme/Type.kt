package app.kotlin.calculator.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TextUnit.fontScale(): TextUnit {
    val localFontScale = LocalConfiguration.current.fontScale
    return (this.value / localFontScale).sp
}

val NormalTypo = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    color = borderAndTextColor,
    textAlign = TextAlign.Right
)

val HighlightedTypo = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    color = borderAndTextColor,
    textAlign = TextAlign.Right
)


