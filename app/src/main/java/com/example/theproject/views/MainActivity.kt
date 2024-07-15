package com.example.theproject.views

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.theproject.R
import com.example.theproject.databinding.ActivityMainBinding
import com.example.theproject.model.apiModel.ApiModel
import com.example.theproject.utils.API_LOG
import com.example.theproject.utils.MAIN_LOG
import com.example.theproject.utils.Util
import com.example.theproject.utils.toast
import com.example.theproject.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var binding: ActivityMainBinding

    private var query: String = ""
    private var noResultToast : Boolean = false

    // ViewModel
    private val viewModel: MainViewModel by viewModels()

    // Connectivity
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    // Live Data
    private val _searchResults = MutableLiveData<ApiModel?>()
    private val searchResults: LiveData<ApiModel?> get() = _searchResults

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        checkInternetConnection()
        notVisible(binding)

        with(binding) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    lifecycleScope.launch {
                        searchTextBox.isEnabled = true
                        this@MainActivity.toast(message = "Internet available, please search products")
                    }
                }

                override fun onLost(network: Network) {
                    lifecycleScope.launch {
                        searchTextBox.isEnabled = false
                        this@MainActivity.toast(message = "Please Connect To Internet")
                    }
                }
            }

            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

            searchTextBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    Util.log(
                        tag = MAIN_LOG,
                        message = "OnBeforeTextChanged --> ${s.toString()}"
                    )
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Util.log(
                        tag = MAIN_LOG,
                        message = "OnTextChanged --> ${s.toString()}"
                    )
                }

                override fun afterTextChanged(s: Editable?) {
                    query = s.toString()
                    Util.log(
                        tag = MAIN_LOG,
                        message = "AfterTextChanged --> $query"
                    )
                    if (query.isNotEmpty()) {
                        noResultToast = false
                        viewModel.getSearchResults(query) {
                            it?.let { apiModel ->
                                // Updating the live data
                                _searchResults.postValue(apiModel)
                                // Logs For the ApI response
                                apiLogs(apiModel)
                            }
                        }
                    } else {
                        Util.log(tag = API_LOG, message = "Query is empty = ${query.isEmpty()}")
                    }
                }
            })

            searchResults.observe(this@MainActivity) { apiModel ->
                apiModel?.let { apiResponse ->
                    if (apiResponse.total == 0) {
                        if(!noResultToast) {
                            this@MainActivity.toast(message = getString(R.string.no_result_found))
                            notVisible(binding)
                            noResultToast = true
                        }
                    } else {
                        makeVisible(binding)
                        for ((index, product) in apiResponse.products.withIndex()) {
                            when (index) {
                                0 -> {
                                    Glide.with(this@MainActivity)
                                        .load(product.thumbnail)
                                        .fitCenter()
                                        .into(firstProductImg)
                                    firstProductTitle.text = product.title
                                }

                                1 -> {
                                    Glide.with(this@MainActivity)
                                        .load(product.thumbnail)
                                        .fitCenter()
                                        .into(secondProductImg)
                                    secondProductTitle.text = product.title
                                }

                                2 -> {
                                    Glide.with(this@MainActivity)
                                        .load(product.thumbnail)
                                        .fitCenter()
                                        .into(thirdProductImg)
                                    thirdProductTitle.text = product.title
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun apiLogs(apiModel: ApiModel) {
        Util.log(
            tag = API_LOG,
            message = apiModel.toString()
        )
        Util.log(
            tag = API_LOG,
            message = "Skip: ${apiModel.skip}"
        )
        Util.log(
            tag = API_LOG,
            message = "Limit: ${apiModel.limit}"
        )
        Util.log(
            tag = API_LOG,
            message = "Total: ${apiModel.total}"
        )
    }

    private fun checkInternetConnection() {
        lifecycleScope.launch {
            val isConnected = withContext(Dispatchers.IO) {
                Util.isInternetAvailable(this@MainActivity)
            }

            if (isConnected)
                binding.searchTextBox.isEnabled = true
            else {
                binding.searchTextBox.isEnabled = false
                this@MainActivity.toast(message = "No internet. Please Connect To Internet")
            }
        }
    }

    private fun notVisible(binding: ActivityMainBinding) {
        with(binding) {
            firstProductImg.visibility = android.view.View.GONE
            firstProductTitle.visibility = android.view.View.GONE
            secondProductImg.visibility = android.view.View.GONE
            secondProductTitle.visibility = android.view.View.GONE
            thirdProductImg.visibility = android.view.View.GONE
            thirdProductTitle.visibility = android.view.View.GONE
        }
    }

    private fun makeVisible(binding: ActivityMainBinding) {
        with(binding) {
            firstProductImg.visibility = android.view.View.VISIBLE
            firstProductTitle.visibility = android.view.View.VISIBLE
            secondProductImg.visibility = android.view.View.VISIBLE
            secondProductTitle.visibility = android.view.View.VISIBLE
            thirdProductImg.visibility = android.view.View.VISIBLE
            thirdProductTitle.visibility = android.view.View.VISIBLE
        }
    }

    // Activity lifeCycle
    override fun onDestroy() {
        super.onDestroy()
        // Unregister network callback
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}