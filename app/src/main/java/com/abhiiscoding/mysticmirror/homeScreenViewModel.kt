package com.abhiiscoding.mysticmirror

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private val _response = MutableStateFlow<String?>(null)
    val response: StateFlow<String?> = _response.asStateFlow()

    fun sendQuestion(selectedCards: List<String>, questionText: String) {
        viewModelScope.launch {
            val userInput =
                "In a tarot reading game, I chose ${selectedCards[0]}, ${selectedCards[1]}, and ${selectedCards[2]} out of 72. My question was '$questionText'. Give me a prediction that someone will say."

            GeminiApiService.generateResponse(userInput) { response ->
                _response.value = response
                Log.d("Response", _response.value.toString())
            }
        }
    }
}
