package com.example.companyapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.companyapp.ui.theme.CompanyAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ─── Data classes ─────────────────────────────────────────────────────────────

data class ApiResponse(
    val results: List<User>
)

data class User(
    val name: Name,
    val email: String,
    val phone: String,
    val location: Location,
    val picture: Picture
)

data class Name(
    val first: String,
    val last: String
)

data class Location(
    val city: String,
    val country: String
)

data class Picture(
    val large: String,
    val medium: String,
    val thumbnail: String
)

// ─── Retrofit API ─────────────────────────────────────────────────────────────

interface RandomUserApiService {
    @GET("api/")
    suspend fun getUsers(@Query("results") results: Int): ApiResponse
}

// ─── ViewModel ────────────────────────────────────────────────────────────────

class EmployeesViewModel : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val api = Retrofit.Builder()
        .baseUrl("https://randomuser.me/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RandomUserApiService::class.java)

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Haetaan tasan 10 käyttäjää
                val response = api.getUsers(results = 10)
                _users.value = response.results
            } catch (e: Exception) {
                _error.value = "Henkilöiden lataaminen epäonnistui"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// ─── Activity ─────────────────────────────────────────────────────────────────

class EmployeesActivity : ComponentActivity() {

    companion object {
        const val SHARED_PREFS = "shared_prefs"
        const val EMAIL_KEY = "email_key"
        const val NAME_KEY = "name_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val email = sharedPreferences.getString(EMAIL_KEY, null)
        val name = sharedPreferences.getString(NAME_KEY, null)

        setContent {
            CompanyAppTheme {
                EmployeesScreen(
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
                            "offices" -> startActivity(Intent(this, OfficesActivity::class.java))
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    name: String?,
    email: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    employeesViewModel: EmployeesViewModel = viewModel()
) {
    val users by employeesViewModel.users.collectAsState()
    val isLoading by employeesViewModel.isLoading.collectAsState()
    val error by employeesViewModel.error.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    selected = false,
                    icon = { Icon(Icons.Default.Email, contentDescription = "News") },
                    badge = { Text("22") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("news")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Employees") },
                    selected = true,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Employees") },
                    badge = { Text("10") },
                    onClick = { scope.launch { drawerState.close() } }
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
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Employees") },
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
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Ladataan henkilöitä...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

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
                            Button(onClick = { employeesViewModel.loadUsers() }) {
                                Text("Yritä uudelleen")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        items(users) { user ->
                            UserListItem(user = user)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

// ─── UserListItem ─────────────────────────────────────────────────────────────

@Composable
fun UserListItem(user: User) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pyöreä profiilikuva Coililla
        AsyncImage(
            model = user.picture.medium,
            contentDescription = "${user.name.first} ${user.name.last}",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Koko nimi
            Text(
                text = "${user.name.first} ${user.name.last}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))

            // Sähköposti — klikatessa avaa sähköpostiohjelma
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${user.email}")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Lähetä sähköposti",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.height(2.dp))

            // Puhelin — klikatessa avaa numerovalitsin
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${user.phone}")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Soita",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = user.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}