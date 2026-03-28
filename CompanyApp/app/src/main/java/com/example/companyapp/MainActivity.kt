package com.example.companyapp

import android.R.attr.checked
import android.R.attr.name
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.companyapp.ui.theme.CompanyAppTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val SHARED_PREFS = "shared_prefs"
        const val EMAIL_KEY = "email_key"
        const val PASSWORD_KEY = "password_key"

        const val NAME_KEY = "name_key"
        const val VALID_EMAIL = "testi@test.com"

        const val VALID_Name = "Jaani Vainio"
        const val VALID_PASSWORD = "password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString(EMAIL_KEY, null)
        val savedPassword = sharedPreferences.getString(PASSWORD_KEY, null)

        if (savedEmail != null && savedPassword != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContent {
            CompanyAppTheme {
                LoginScreen(
                    onLoginSuccess = {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {

    val ctx = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = ctx.getSharedPreferences(
        MainActivity.SHARED_PREFS, Context.MODE_PRIVATE
    )
    var rememberMe by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val fillFieldsMsg = stringResource(R.string.fill_fields)
    val wrongCredMsg = stringResource(R.string.wrong_credentials)

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // Otsikko
                Text(
                    text = stringResource(R.string.sign_in),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sähköpostikenttä
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_your_email),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                // Salasanakenttä
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_password),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text(
                        text = stringResource(R.string.check),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Kirjaudu-nappi
                Button(
                    onClick = {
                        when {
                            email.isEmpty() || password.isEmpty() -> {
                                Toast.makeText(ctx, fillFieldsMsg, Toast.LENGTH_SHORT).show()
                            }
                            email == MainActivity.VALID_EMAIL &&
                                    password == MainActivity.VALID_PASSWORD -> {
                                sharedPreferences.edit()
                                    .putString(MainActivity.EMAIL_KEY, email)
                                    .putString(MainActivity.PASSWORD_KEY, password)
                                    .putString(MainActivity.NAME_KEY, MainActivity.VALID_Name)
                                    .apply()
                                onLoginSuccess()
                            }
                            else -> {
                                Toast.makeText(ctx, wrongCredMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sign_in),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}