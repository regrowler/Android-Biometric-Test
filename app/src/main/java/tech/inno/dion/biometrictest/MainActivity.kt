package tech.inno.dion.biometrictest

import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import tech.inno.dion.biometrictest.ui.theme.BiometricTestTheme
import java.security.KeyStore
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class MainActivity : ComponentActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = ContextCompat.getMainExecutor(this)
        val keySpec = KeyGenParameterSpec.Builder(
            "KEY_NAME",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
//            .setUserAuthenticationParameters()
            .build()
        val keySpec2 = KeyGenParameterSpec.Builder(
            "",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .build()
        generateSecretKey(keySpec)
        biometricPrompt = BiometricPrompt
            .Builder(this)
            .setTitle("TITLE")
            .setDescription("Desc")
            .setNegativeButton(
                "Negative",
                executor,
                { dialog, which ->

                }
            )
            .build()
        setContent {
            BiometricTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Greeting("Android")
                        Button(
                            onClick = {
                                // Exceptions are unhandled within this snippet.
                                val cipher = getCipher()
                                val secretKey = getSecretKey()
                                cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                                biometricPrompt.authenticate(
                                    BiometricPrompt.CryptoObject(cipher),
                                    CancellationSignal(),
                                    executor,
                                    object : BiometricPrompt.AuthenticationCallback() {
                                        override fun onAuthenticationError(
                                            errorCode: Int,
                                            errString: CharSequence?
                                        ) {
                                            super.onAuthenticationError(errorCode, errString)
                                        }

                                        override fun onAuthenticationHelp(
                                            helpCode: Int,
                                            helpString: CharSequence?
                                        ) {
                                            super.onAuthenticationHelp(helpCode, helpString)
                                        }

                                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                                            super.onAuthenticationSucceeded(result)
                                        }

                                        override fun onAuthenticationFailed() {
                                            super.onAuthenticationFailed()
                                        }
                                    }
                                )

                            },
                            content = {
                                Text(text = "Button")
                            }
                        )
                    }
                }
            }
        }
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey("KEY_NAME", null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BiometricTestTheme {
        Greeting("Android")
    }
}