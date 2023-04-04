package com.example.rxjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.rxjava.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final String TAG = "RxJava";
    private ActivityMainBinding binding;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        RelativeLayout view = binding.getRoot();
        setContentView(view);

        binding.editSearch.setOnQueryTextListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 구독자 해제
        disposable.dispose();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null){
            isLoading(true);
            // Observable을 생성하고, 구독자를 등록
            Observable<String> observable = run(query);
            disposable = observable.subscribe();
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

    public Observable<String> run(String query) {
        return Observable.fromCallable(() -> getData(query))
                .delay(3, TimeUnit.SECONDS) //3초 지연
                .subscribeOn(Schedulers.io())   // IO 스레드에서 작업 수행
                .observeOn(AndroidSchedulers.mainThread()) // 결과를 UI 스레드에서 처리
                .doOnNext(this::onNext) // 처리 결과 수신
                .doOnError(e -> Log.e(TAG, "onError : "+e.getMessage())) // 에러 발생 시 처리
                .doOnComplete(this::onComplete); // 처리 완료 시 처리
    }

    public void onComplete() {
        Log.d(TAG, "onComplete");
        isLoading(false);
    }

    private void onNext(String data) {
        Log.d(TAG, "onNext");
        final ApiResult result = parsingJson(data);
        Log.d(TAG, "onNext: ");
        // adapter 생성 및 setadapter
        final ViewAdapter adapter = new ViewAdapter();
        adapter.setData(result.getHits());
        binding.recyclerView.setAdapter(adapter);
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

        return data;
    }

    private ApiResult parsingJson(String data) {
        Gson gson = new Gson();
        final ApiResult result = gson.fromJson(data, ApiResult.class);
        Log.d(TAG, "parsingData: ");
        return result;
    }
}