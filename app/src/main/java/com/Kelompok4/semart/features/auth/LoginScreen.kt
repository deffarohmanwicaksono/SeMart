package com.Kelompok4.semart.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Kelompok4.semart.R

// Konstanta Warna
val PrimaryBlue = Color(0xFF3B9DF8)
val DarkText = Color(0xFF1E293B)
val GrayText = Color(0xFF64748B)
val BgInput = Color(0xFFF8FAFC)
val BorderGray = Color(0xFFE2E8F0)

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // SeMart
    val semartTitleAnnotated = buildAnnotatedString {
        append("Login ke Se")
        withStyle(style = SpanStyle(color = PrimaryBlue)) {
            append("Mart")
        }
    }

    // Headline
    val headlineAnnotated = buildAnnotatedString {
        append("Marketplace Barang Bekas\nUntuk ")
        withStyle(style = SpanStyle(color = PrimaryBlue)) {
            append("Mahasiswa UNS")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFEBF3FF), Color(0xFFF5F9FF), Color(0xFFFFFFFF))
                )
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Brand Logo
            Image(
                painter = painterResource(id = R.drawable.logo_semart),
                contentDescription = "SeMart Logo",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(60.dp)
                    .wrapContentWidth(),
                contentScale = ContentScale.Inside
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Brand Headline Text
            Text(
                text = headlineAnnotated,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Jual beli aman, mudah, dan terpercaya untuk mahasiswa.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = GrayText,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Brand Illustration
            Image(
                painter = painterResource(id = R.drawable.login_illustration),
                contentDescription = "Ilustrasi Mahasiswa",
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. CARD LOGIN
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    // Card Header Section
                    Text(
                        text = semartTitleAnnotated,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Masuk untuk mulai jual beli sekarang",
                        fontSize = 11.sp,
                        color = GrayText,
                        modifier = Modifier
                            .padding(top = 2.dp, bottom = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    // Input Email
                    Text(
                        text = "Email SSO UNS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkText),
                        placeholder = { Text("email@student.uns.ac.id", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = BgInput,
                            unfocusedContainerColor = BgInput,
                            focusedIndicatorColor = PrimaryBlue,
                            unfocusedIndicatorColor = BorderGray,
                            focusedLeadingIconColor = PrimaryBlue
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Password
                    Text(
                        text = "Password",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkText),
                        placeholder = { Text("Masukkan password", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(20.dp)) {
                                Icon(image, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = BgInput,
                            unfocusedContainerColor = BgInput,
                            focusedIndicatorColor = PrimaryBlue,
                            unfocusedIndicatorColor = BorderGray,
                            focusedLeadingIconColor = PrimaryBlue
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    // Lupa Password
                    Text(
                        text = "Lupa Password?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 6.dp, bottom = 16.dp)
                            .clickable { /* Handle Lupa Password */ }
                    )

                    // Tombol Login
                    Button(
                        onClick = {
                            // Nanti di sini tempat pasang validasi API,
                            // sementara ini kita langsung trigger sukses agar pindah halaman
                            onLoginSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                                )
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Text(text = "Login", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Card Footer Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Belum punya akun? ", fontSize = 12.sp, color = GrayText)
                        Text(
                            text = "Hubungi admin",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { /* Handle kontak admin */ }
                        )
                    }
                }
            }
        }
    }
}