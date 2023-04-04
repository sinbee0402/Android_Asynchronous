package com.example.asynctask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.asynctask.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private final String TAG = "AsyncTask";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        RelativeLayout view = binding.getRoot();
        setContentView(view);

        binding.editSearch.setOnQueryTextListener(this);

        Log.d(TAG, "start");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null) {
            isLoading(true);
            MyAsyncTask task = new MyAsyncTask();
            task.execute(query);
            //doInBackground에서 전달받을 인자(params)를 통해
            //UI 업데이트가 수행된다
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

    public class MyAsyncTask extends AsyncTask<String, String, ApiResult> {

        //String1 query, String2 Stringjson, ApiResult return

        //String1 입력 매개변수 타입 - query 문자열(doInBackground)
        //String2 작업 수행 중 진행 상태를 업데이트하는데 사용되는 매개변수 타입 - 해당 override를 사용하지 않음(publishProgress)
        //ApiResult 작업 결과의 타입 - 작업 결과 반환(onPostExecute)

        @Override
        protected ApiResult doInBackground(String... strings) {
            // 백그라운드 스레드에서 실행되는 작업 구현 - 비동기 작업 처리
            // onPreExecute 메서드 실행 후 호출되는 메서드
            // 현재는 없으니 task.execute 후에 바로 호출
            String query = strings[0];
            String data = getData(query);
            return parsingJson(data);
        }

        @Override
        protected void onPostExecute(ApiResult apiResult) {
            super.onPostExecute(apiResult);
            // 작업이 끝난 후 수행할 작업 구현 - UI 업데이트 수행
            // doInBackground 메서드 실행 후에 호출되는 메서드
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // UI 업데이트 작업 수행
                    isLoading(false);
                    final ViewAdapter adapter = new ViewAdapter();
                    adapter.setData(apiResult.getHits());
                    binding.recyclerView.setAdapter(adapter);
                }
            };
            //3초 지연
            new Handler().postDelayed(runnable, 3000);
        }
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
        Log.d(TAG, "data : "+data);
        return data;
    }

    private ApiResult parsingJson(String data) {
        Gson gson = new Gson();
        final ApiResult result = gson.fromJson(data, ApiResult.class);
        Log.d(TAG, "parsingData: ");
        return result;
    }
}

