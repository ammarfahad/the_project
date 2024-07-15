package com.example.theproject.viewModel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theproject.model.api.ApiEndPoints
import com.example.theproject.model.apiModel.ApiModel
import com.example.theproject.utils.API_LOG
import com.example.theproject.utils.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var apiEndPoints: ApiEndPoints
) : ViewModel() {
    private val handler = Handler(Looper.getMainLooper())

    fun getSearchResults(query: String, callback: (ApiModel?) -> Unit) {
        val runnableTask = Runnable {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    supervisorScope {
                        try {
                            Util.log(tag = API_LOG, message = "Api Hit -> $query")
                            callback(apiEndPoints.searchProducts(query))
                        } catch (e: Exception) {
                            Util.log(tag = API_LOG, message = "Error: ${e.message}", errorLog = true)
                            callback(null)
                        }
                    }
                }
            }
        }
        Util.log(tag = API_LOG, message = "Api Hit After 100ms Delay")
        handler.postDelayed(runnableTask, 100)
    }
}