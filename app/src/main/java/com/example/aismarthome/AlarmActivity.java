package com.example.aismarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.aismarthome.databinding.ActivityAlarmBinding;
import com.example.aismarthome.databinding.ActivityHomeBinding;
import com.example.aismarthome.databinding.ActivityMainBinding;

public class AlarmActivity extends AppCompatActivity {
    private ActivityAlarmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater()); //inflate 메서드를 활용해서 엑티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        setContentView(binding.getRoot());
    }
}