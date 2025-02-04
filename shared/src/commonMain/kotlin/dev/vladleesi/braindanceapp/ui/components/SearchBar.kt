package dev.vladleesi.braindanceapp.ui.components

import Search
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.vladleesi.braindanceapp.ui.style.secondary
import dev.vladleesi.braindanceapp.ui.style.white

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "Search for games",
) {
    var query by rememberSaveable { mutableStateOf("") }
    TextField(
        value = query,
        onValueChange = { value ->
            query = value
            // view model call
        },
        singleLine = true,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.body2,
            )
        },
        shape = CircleShape,
        colors =
            TextFieldDefaults.textFieldColors(
                backgroundColor = secondary,
                cursorColor = white,
                textColor = white,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        leadingIcon = {
            Icon(
                imageVector = Search,
                contentDescription = null,
            )
        },
        modifier = modifier.fillMaxWidth(),
    )
}
