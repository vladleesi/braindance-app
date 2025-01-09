package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.error_screen_error_message
import dev.vladleesi.braindanceapp.resources.error_screen_retry_button_text
import dev.vladleesi.braindanceapp.resources.error_screen_title
import dev.vladleesi.braindanceapp.ui.style.getTypography
import dev.vladleesi.braindanceapp.ui.style.medium
import dev.vladleesi.braindanceapp.ui.style.small
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorScreen(
    title: String = stringResource(Res.string.error_screen_title),
    errorMessage: String = stringResource(Res.string.error_screen_error_message),
    retryButtonText: String = stringResource(Res.string.error_screen_retry_button_text),
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(medium),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = getTypography().h1,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = errorMessage,
                style = getTypography().body1,
                modifier = Modifier.padding(horizontal = medium, vertical = small),
            )

            Spacer(modifier = Modifier.height(medium))

            Button(onClick = onRetry) {
                Text(text = retryButtonText, style = getTypography().button)
            }
        }
    }
}
