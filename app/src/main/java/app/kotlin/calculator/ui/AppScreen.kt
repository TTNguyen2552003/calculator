package app.kotlin.calculator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.calculator.R
import app.kotlin.calculator.ui.components.CalculatorDisplay
import app.kotlin.calculator.ui.components.Numpad
import app.kotlin.calculator.ui.components.listImgSrc

@Composable
fun AppScreen(
    appViewModel: AppViewModel = viewModel()
) {
    val appState: State<AppUiState> = appViewModel.uiState.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //App background
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 20.dp)
        )

        //Add blur layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.1f)
                    )
                )
            )
        }

        //Add app component
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insets = WindowInsets.statusBars)
                .windowInsetsPadding(insets = WindowInsets.navigationBars),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            item {
                CalculatorDisplay(
                    expression = appState.value.expression,
                    result = appState.value.result,
                    isCompleted = appState.value.isCompleted
                )
            }

            item {
                Numpad(
                    listImgSrc = listImgSrc,
                    listOfAction = appViewModel.listOfAction
                )
            }


        }
    }

}