package top.goosople.poemtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import top.goosople.poemtime.ui.theme.PoemTimeTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var items: List<String>
    private lateinit var poems: Poem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        poems = poemInit()
        items = listOf(
            this.getString(R.string.nav_home),
            this.getString(R.string.nav_read),
            this.getString(R.string.nav_me)
        )
        setContent {
            navController = rememberNavController()
            PoemTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = { TopBar(this.getString(R.string.app_name)) }, bottomBar = {
                        BottomNavBar(items)
                    }) { padding -> MainContent(padding) }
                }
            }
        }
    }

    @Composable
    fun BottomNavBar(items: List<String>) {
        var selectedItem by remember { mutableStateOf(0) }
        val icons = listOf(Icons.Outlined.Home, Icons.Outlined.MenuBook, Icons.Outlined.Person)
        val selectedIcons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Person)
        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else icons[index], null
                    )
                },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index; navController.navigate(items[index]) })
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(appName: String) {
        CenterAlignedTopAppBar(title = { Text(text = appName, maxLines = 1) })
    }

    @Composable
    fun MainContent(paddingValues: PaddingValues) {
        NavHost(
            navController = navController,
            startDestination = items[0],
            modifier = Modifier.fillMaxSize()
        ) {
            composable(items[0]) { HomeContent(paddingValues) }
            composable(items[1]) { PoemContent(paddingValues) }
            composable(items[2]) { MeContent(paddingValues) }
        }
    }

    @Composable
    fun HomeContent(paddingValues: PaddingValues) {
    }

    @Preview
    @Composable
    fun PoemContent(paddingValues: PaddingValues = PaddingValues()) {
        var isVisible by remember { mutableStateOf(true) }
        var poemNum by remember { mutableStateOf(1) }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = isVisible) {
                Column(
                    modifier = Modifier.padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = poems[0][0].poem[poemNum], fontSize = 36.sp)
                    Text(text = "Goosople Song [Modern]")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.ChevronLeft, "Previous")
                }
                Button(onClick = { isVisible = !isVisible }) {
                    Icon(
                        if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        "Hide/Show"
                    )
                }
                Button(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Done, "Finished")
                }
                Button(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Redo, "Skip")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = poemNum.toFloat(),
                    onValueChange = { newValue -> poemNum = newValue.toInt() },
                    valueRange = 1f..1145f,
                    modifier = Modifier.fillMaxWidth(0.75f)
                )
                Text(text = "$poemNum/1145")
            }
        }
    }

    @Composable
    fun MeContent(paddingValues: PaddingValues) {
    }

    private fun poemInit() = Gson().fromJson(
        resources.openRawResource(R.raw.poem).bufferedReader().readText(), Poem::class.java
    )

}
