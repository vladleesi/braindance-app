package dev.vladleesi.braindanceapp.android.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.vladleesi.braindanceapp.android.style.Typography

@Composable
fun TopBar(@StringRes label: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = label),
            style = Typography.h1,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 24.dp, end = 24.dp, top = 96.dp)
        )
    }
}
