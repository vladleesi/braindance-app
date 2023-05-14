package io.github.vladleesi.braindanceapp.android.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.vladleesi.braindanceapp.android.R
import io.github.vladleesi.braindanceapp.android.style.Typography
import io.github.vladleesi.braindanceapp.android.style.secondary
import io.github.vladleesi.braindanceapp.android.style.white


@Composable
fun TopBar(@StringRes label: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // TODO: Replace this Text to tooltip from design
        Text(
            text = stringResource(id = label),
            style = Typography.h1,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp, end = 16.dp)
        )
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(color = secondary, shape = CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = null,
                    tint = white
                )
            }
            Box(
                modifier = Modifier
                    .background(color = secondary, shape = CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = null,
                    tint = white
                )
            }
        }
    }
}