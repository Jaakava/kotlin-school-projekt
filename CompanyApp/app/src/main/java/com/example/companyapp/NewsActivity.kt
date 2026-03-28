package com.example.companyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.companyapp.ui.theme.CompanyAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items


// ─── Data classit ───────────────────────────────────────────────────────────

data class Article(
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val publishedAt: String,
    val category: String
)

data class NewsResponse(
    val articles: List<Article>
)

// ─── Retrofit API ────────────────────────────────────────────────────────────

interface NewsApiService {
    @GET("refs/heads/main/news.json")
    suspend fun getNews(): NewsResponse
}

// ─── ViewModel ───────────────────────────────────────────────────────────────

class NewsViewModel : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val api = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/Jaakava/My-news-api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApiService::class.java)

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.getNews()
                _articles.value = response.articles
            } catch (e: Exception) {
                _error.value = "Uutisten lataaminen epäonnistui"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class NewsActivity : ComponentActivity() {

    companion object {
        const val SHARED_PREFS = "shared_prefs"
        const val EMAIL_KEY = "email_key"
        const val NAME_KEY = "name_key"
        const val PASSWORD_KEY = "password_key"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val email = sharedPreferences.getString(EMAIL_KEY, null)
        val name = sharedPreferences.getString(NAME_KEY, null)

        setContent {
            CompanyAppTheme {
                NewsScreen(
                    name = name,
                    email = email,
                    onLogout = {
                        sharedPreferences.edit().clear().apply()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onNavigate = { destination ->
                        when (destination) {
                            "home" -> startActivity(Intent(this, HomeActivity::class.java))
                            "news" -> { /* jo tällä sivulla */ }
                            "employees" -> startActivity(Intent(this, EmployeesActivity::class.java))
                            "offices" -> startActivity(Intent(this, OfficesActivity::class.java))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    name: String?,
    email: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    newsViewModel: NewsViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val articles by newsViewModel.articles.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val error by newsViewModel.error.collectAsState()



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.dog),
                        contentDescription = "Profiilikuva",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = name ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = email ?: "unknown@email.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("home")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("News") },
                    selected = true,  // ← tällä sivulla ollaan
                    icon = { Icon(Icons.Default.Email, contentDescription = "News") },
                    badge = { Text("22") },
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Employees") },
                    selected = false,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Employees") },
                    badge = { Text("45") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("employees")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Offices") },
                    selected = false,
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Offices") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("offices")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "(c) 2026 - VaJaa Company",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version: 10.01.66",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    ) { // ← ModalNavigationDrawer content-lambda alkaa tässä
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Company - News") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Avaa valikko"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->

            when {
                // Ladataan...
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Virhe
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = error ?: "Tuntematon virhe",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { newsViewModel.loadNews() }) {
                                Text("Yritä uudelleen")
                            }
                        }
                    }
                }

                // Uutislista
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        items(articles) { article ->
                            NewsListItem(article = article)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

// ─── NewsListItem ─────────────────────────────────────────────────────────────

@Composable
fun NewsListItem(article: Article) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Päivämäärä + kirjoittaja
        Text(
            text = "${article.publishedAt}  ${article.author}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Otsikko
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Kuvaus
        Text(
            text = article.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}