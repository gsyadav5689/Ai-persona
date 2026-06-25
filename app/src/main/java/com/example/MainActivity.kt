package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.AvatarRepository
import com.example.ui.screens.MainAppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PersonaViewModel
import com.example.ui.viewmodel.PersonaViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize Room Database, DAO and Repository
    val database = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "persona_studio.db"
    )
    .fallbackToDestructiveMigration() // Handle model updates gracefully
    .build()
    
    val repository = AvatarRepository(database.avatarDao())
    val factory = PersonaViewModelFactory(repository)
    val viewModel: PersonaViewModel by viewModels { factory }

    enableEdgeToEdge()
    
    setContent {
      MyApplicationTheme {
        MainAppNavigation(viewModel = viewModel)
      }
    }
  }
}
