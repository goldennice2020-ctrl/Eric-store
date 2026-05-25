package com.golden.earthol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.golden.earthol.data.AppDatabase
import com.golden.earthol.data.GameRepository
import com.golden.earthol.theme.EarthOLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = GameRepository(AppDatabase.get(this))
        setContent {
            EarthOLTheme {
                EarthOlApp(repository)
            }
        }
    }
}
