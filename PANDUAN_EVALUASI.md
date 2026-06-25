# Panduan Evaluasi & Menjalankan Aplikasi Android (SeMart)

Aplikasi Android ini dibangun menggunakan **Kotlin** dan **Jetpack Compose**. Aplikasi ini dikonfigurasi untuk terhubung dengan server backend (Laravel) secara lokal (Localhost).

## ⚠️ PERHATIAN PENTING (BACA SEBELUM MENJALANKAN)
Aplikasi ini melakukan request API ke alamat `http://10.0.2.2:8000/api/` dan terhubung ke WebSocket (Reverb) di port `8080`.
Karena menggunakan IP `10.0.2.2`, **Aplikasi ini WAJIB dijalankan menggunakan Emulator bawaan Android Studio** agar bisa mendeteksi `localhost` dari PC/Laptop tempat server berjalan. 

> **Jangan** menjalankan aplikasi ini melalui device Android fisik (menggunakan kabel USB), karena device fisik tidak akan bisa mengakses server `10.0.2.2`!

## Langkah Menjalankan Aplikasi
1. **Pastikan Backend Menyala:** Pastikan server Laravel dan WebSocket (Reverb) dari proyek backend sudah berjalan (lihat panduan di folder backend).
2. **Buka Proyek:** Buka folder proyek ini (`SeMart`) menggunakan **Android Studio**.
3. **Tunggu Sinkronisasi Gradle:** Biarkan Android Studio men-download dependencies dan melakukan sinkronisasi Gradle hingga selesai.
4. **Pilih Emulator:** Pilih device **Emulator** (misalnya Pixel 6 API 34 atau sejenisnya) di bagian atas Android Studio.
5. **Run App:** Klik tombol hijau **Run** (atau tekan `Shift + F10`) untuk meng-install dan menjalankan aplikasi di dalam emulator.

Aplikasi siap digunakan dan akan langsung berkomunikasi dengan server lokal Anda!
