package com.example.companyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import com.example.companyapp.ui.theme.CompanyAppTheme
import kotlinx.coroutines.launch

class OfficesActivity : ComponentActivity() {

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
                OfficesScreen(
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
                            "news" -> startActivity(Intent(this, NewsActivity::class.java))
                            "employees" -> startActivity(Intent(this, EmployeesActivity::class.java))
                            "offices" -> { /* jo tällä sivulla */ }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficesScreen(
    name: String?,
    email: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
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
                    selected = true, // ← tällä sivulla ollaan
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Offices") },
                    onClick = { scope.launch { drawerState.close() } }
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
                    title = { Text("Offices") },
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Offices - tulossa pian!", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}