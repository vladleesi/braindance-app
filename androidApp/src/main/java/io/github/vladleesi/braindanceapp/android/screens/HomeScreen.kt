package io.github.vladleesi.braindanceapp.android.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.vladleesi.braindanceapp.android.R
import io.github.vladleesi.braindanceapp.android.style.Typography
import io.github.vladleesi.braindanceapp.android.style.hint_text
import io.github.vladleesi.braindanceapp.android.style.secondary
import io.github.vladleesi.braindanceapp.android.style.white
import io.github.vladleesi.braindanceapp.android.viewmodels.SearchViewModel

@Composable
fun HomeScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar()
    }
}

@Composable
fun SearchBar(searchViewModel: SearchViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    val searchResult by searchViewModel.searchResult.observeAsState()
    TextField(
        value = query,
        onValueChange = { value ->
            query = value
            searchViewModel.search(value)
        },
        singleLine = true,
        placeholder = {
            Text(
                text = "Search for games",
                style = Typography.body2
            )
        },
        shape = CircleShape,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = secondary,
            cursorColor = hint_text,
            textColor = white,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp)
    )
    Text(
        text = "Results: ${
        searchResult.orEmpty().joinToString(", ") { game ->
            game.name.orEmpty()
        }
        }",
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
    )
}
