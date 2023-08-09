package top.goosople.poemtime

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.goosople.poemtime.ui.theme.PoemTimeTheme
import kotlin.math.log10

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
        poemSharedPreferences.edit().putInt("bookNum", bookNum).apply()
        poemNum = poemSharedPreferences.getInt("poemNum", 0)
        poemSharedPreferences.edit().putInt("poemNum", poemNum).apply()
        verseNum = poemSharedPreferences.getInt("verseNum", 0)
        poemSharedPreferences.edit().putInt("verseNum", verseNum).apply()

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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            poems.forEachIndexed { index, poemItems ->
                Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "$index ${poems.bookNames[index]}")
                }
            }
        }
    }

    /**
     * Build Poem content
     * @param paddingValues the padding values given by system
     */
    @Preview
    @Composable
    fun PoemContent(paddingValues: PaddingValues = PaddingValues()) {
        var isVisible by remember { mutableStateOf(true) }
        var mVerseNum by remember { mutableStateOf(verseNum) }
        var isFullPoemVisible by remember { mutableStateOf(true) }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = isVisible) {
                Column(
                    modifier = Modifier.padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isFullPoemVisible) poems[bookNum][poemNum].poem[mVerseNum]
                        else poems[bookNum][poemNum].poem[mVerseNum][0].toString(),
                        fontSize = (45 - 15 * log10(
                            (if (isFullPoemVisible) poems[bookNum][poemNum].poem[mVerseNum]
                            else poems[bookNum][poemNum].poem[mVerseNum][0].toString()).length + 5f
                        )).toInt().sp,
                        modifier = Modifier.clickable { isFullPoemVisible = !isFullPoemVisible },
                        lineHeight = 1.2.em,
                        textAlign = TextAlign.Center
                    )
                    Text(text = poems[bookNum][poemNum].detail)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (mVerseNum > 0) {
                        mVerseNum--
                        verseNum = mVerseNum
                        poemSharedPreferences.edit().putInt("verseNum", verseNum).apply()
                    }
                }, enabled = mVerseNum > 0) {
                    Icon(Icons.Default.ChevronLeft, "Previous")
                }
                Button(onClick = { isVisible = !isVisible }) {
                    Icon(
                        if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        "Hide/Show"
                    )
                }
                Button(onClick = {
                    if (mVerseNum < poems[bookNum][poemNum].totalNum - 1) {
                        mVerseNum++
                        verseNum = mVerseNum
                        poemSharedPreferences.edit().putInt("verseNum", verseNum).apply()
                        GlobalScope.launch {
                            if (dbDao.update(
                                    Verse(bookNum, poemNum, verseNum, true)
                                ) == 0
                            ) dbDao.insert(Verse(bookNum, poemNum, verseNum, true))
                        }
                    }
                }) {
                    Icon(Icons.Default.Done, "Finished")
                }
                Button(
                    onClick = {
                        if (mVerseNum < poems[bookNum][poemNum].totalNum - 1) {
                            mVerseNum++
                            verseNum = mVerseNum
                            poemSharedPreferences.edit().putInt("verseNum", verseNum).apply()
                        }
                    }, enabled = mVerseNum < poems[bookNum][poemNum].totalNum - 1
                ) {
                    Icon(Icons.Default.Redo, "Skip")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = mVerseNum.toFloat() + 1,
                    onValueChange = { newValue ->
                        mVerseNum = newValue.toInt() - 1
                        verseNum = mVerseNum
                        poemSharedPreferences.edit().putInt("verseNum", verseNum).apply()
                    },
                    valueRange = 1f..poems[bookNum][poemNum].totalNum.toFloat(),
                    modifier = Modifier.fillMaxWidth(0.75f)
                )
                Text(text = "${mVerseNum + 1}/${poems[bookNum][poemNum].totalNum}")
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

}
