package top.goosople.poemtime

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.gson.Gson
import top.goosople.poemtime.ui.theme.PoemTimeTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var items: List<String>
    private lateinit var poems: Poem
    private lateinit var poemSharedPreferences: SharedPreferences
    private lateinit var dbDao: VerseDao
    private var bookNum = 0
    private var poemNum = 0
    private var verseNum = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the sharedPreferences
        poemSharedPreferences = getSharedPreferences("poem", MODE_PRIVATE)
        // Initialize database
        dbDao = Room.databaseBuilder(this, Database::class.java, "poem").build().verseDao()
        // Read and deserialize the poem list from file
        poems = poemInit()
        // Get the name of the navigation items
        items = listOf(
            this.getString(R.string.nav_home),
            this.getString(R.string.nav_read),
            this.getString(R.string.nav_me)
        )
        // Initialize poemNum
        bookNum = poemSharedPreferences.getInt("bookNum", 0)
        poemNum = poemSharedPreferences.getInt("poemNum", 0)
        verseNum = poemSharedPreferences.getInt("verseNum", 0)
        poemSharedPreferences.edit {
            putInt("bookNum", bookNum)
            putInt("poemNum", poemNum)
            putInt("verseNum", verseNum)
        }

        setContent { // Set up the UI
            navController = rememberNavController()
            PoemTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = { TopBar(this.getString(R.string.app_name)) },
                        bottomBar = { BottomNavBar(items) }) { padding ->
                        MainContent(padding)
                    }
                }
            }
        }
    }

    /**
     * Build the bottom navigation bar
     * @param items names of the items
     */
    @Composable
    fun BottomNavBar(items: List<String>) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val icons = listOf(Icons.Outlined.Home, Icons.Outlined.MenuBook, Icons.Outlined.Person)
        val selectedIcons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Person)
        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(icon = {
                    Icon(if (currentDestination?.hierarchy?.any { it.route == item } == true) selectedIcons[index] else icons[index],
                        null)
                },
                    label = { Text(item) },
                    selected = currentDestination?.hierarchy?.any { it.route == item } == true,
                    onClick = {
                        navController.navigate(items[index]) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            // when reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    })
            }
        }
    }

    /**
     * Build the top bar
     * @param appName The app name shown on the top bar
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(appName: String) {
        CenterAlignedTopAppBar(title = { Text(text = appName, maxLines = 1) })
    }

    /**
     * The main content of the app between top bar and bottom bar
     * @param paddingValues the padding values given by system
     */
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

    // TODO: fill the content
    @Composable
    fun HomeContent(paddingValues: PaddingValues) {

    }

    /**
     * Build Poem content
     * @param paddingValues the padding values given by system
     */
    @OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun PoemContent(paddingValues: PaddingValues = PaddingValues()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            poems.forEachIndexed { index, poemItems ->
                stickyHeader { Text(text = "$index ${poems.bookNames[index]}") }
            }
            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    poems.forEachIndexed { index, poemItems ->
                        Button(onClick = {
                            val intent = Intent(this@MainActivity, PoemActivity::class.java)
                            intent.putExtra("bookNum", bookNum)
                            intent.putExtra("poemNum", poemNum)
                            intent.putExtra("verseNum", verseNum)
                            startActivity(intent)
                        }) {
                            Text(text = "$index ${poems.bookNames[index]}")
                        }
                    }
                }
            }
        }
    }

    // TODO: fill the content
    @Composable
    fun MeContent(paddingValues: PaddingValues) {
    }

    /**
     * Read poem.json from the specific source (currently from raw resource)
     * @return Poem!
     */
    private fun poemInit() = Gson().fromJson(
        resources.openRawResource(R.raw.poem).bufferedReader().readText(), Poem::class.java
    )

    @Preview
    @Composable
    fun PreviewContent() {
    }
}
