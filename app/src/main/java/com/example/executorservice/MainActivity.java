package com.example.executorservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.executorservice.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final String TAG = "ExecutorService";

    private ActivityMainBinding binding;

    // 코어 스레드 풀 크기
    private static final int CORE_POOL_SIZE = 1;
    // 최대 스레드 풀 크기
    private static final int MAXIMUM_POOL_SIZE = 2;
    // 스레드 풀 유지 시간
    private static final long KEEP_ALIVE_TIME = 300L; // 대기 상태 시간 0.3초
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        RelativeLayout view = binding.getRoot();
        setContentView(view);

        binding.editSearch.setOnQueryTextListener(this);

        // 스레드 풀 생성
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, // 1
                MAXIMUM_POOL_SIZE,  // 2
                KEEP_ALIVE_TIME,    // 300L = 0.3초
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        Log.d(TAG, "start");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null) {
            isLoading(true);

            executor.execute(() -> {
                String data = getData(query);
                ApiResult result = parsingJson(data);

                CompletableFuture.runAsync(() -> {
                    // 3초 지연
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //UI 업데이트 작업 수행
                    runOnUiThread(() -> {
                        isLoading(false);
                        final ViewAdapter adapter = new ViewAdapter();
                        adapter.setData(result.getHits());
                        binding.recyclerView.setAdapter(adapter);
                    });
                });
            });

        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    private void isLoading(Boolean loading) {
        runOnUiThread(() -> {
            if (loading) {
                binding.progressView.setVisibility(View.VISIBLE);
            } else
                binding.progressView.setVisibility(View.GONE);
        });
    }

    private String getData(String query) {
        final String baseUrl = "https://pixabay.com/api/";
        final String apiKey = "";
        String data = "";

        try {
            String urlString = baseUrl + "?key=" + apiKey + "&q=" + query + "&image_type=photo";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            data = stringBuilder.toString();
            inputStream.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "data : " + data);
        return data;
    }

    private ApiResult parsingJson(String data) {
        Gson gson = new Gson();
        final ApiResult result = gson.fromJson(data, ApiResult.class);
        Log.d(TAG, "parsingData: ");
        return result;
    }
}