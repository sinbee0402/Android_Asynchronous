package com.example.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import com.example.coroutine.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope, SearchView.OnQueryTextListener {
    private val TAG: String = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private lateinit var job: Job

    companion object {
        const val BASE_URL = "https://pixabay.com/api/"     // base url.
        const val API_KEY = ""    // Pixabay api key.
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Job 생성
        job = Job()

        binding.editSearch.setOnQueryTextListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Coroutine 취소
        job.cancel()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query!!.isNotEmpty()) {
            run(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun isLoading(loading: Boolean) {
        runOnUiThread(Runnable {
            if (loading) {
                binding.progressView.visibility = View.VISIBLE
            } else
                binding.progressView.visibility = View.GONE
        })
    }

    private fun run(query: String) {
        // Coroutine 시작
        launch {
            isLoading(true)
            val data = async { getData(query) } // 데이터 가져오기
            withContext(Dispatchers.Main) { //main thread

                delay(3000)
                var params = data.await()
                val image = parsingJson(params)
                val adapter = ViewAdapter(image.hits)
                binding.recyclerView.adapter = adapter

                Log.d(TAG, "onCreate: ")
            }
            isLoading(false)
        }

    }

    private suspend fun getData(query: String): String = withContext(Dispatchers.IO) {
        //필요한 데이터를 가져오는 작업 수행
        var data = ""    // result data.

        val url = URL("${BASE_URL}?key=${API_KEY}&q=$query&image_type=photo")
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        val inputStream: InputStream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String = reader.readLine()

        while (line.isNotEmpty()) {
            stringBuilder.append(line)
            line = reader.readLine() ?: ""
        }

        data = stringBuilder.toString()
        inputStream.close()
        reader.close()

        return@withContext data
    }

    private fun parsingJson(data: String): ApiResult {
        val gson = Gson()
        val user = gson.fromJson(data, ApiResult::class.java)
        Log.d(TAG, "parsingData: ")
        return user
    }
}