package com.example.aismarthome;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.aismarthome.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    //소켓 통신 관련 변수
    public Socket socket;
    public InputStream in;
    private OutputStream out;
    private String ip = "220.69.207.111"; // IP
    private int port = 9797; // PORT 번호

    //메인쓰레드와 통신할 핸들러
    private Handler mHandler;

    //수신 데이터를 문자열에 저장할 변수
    private String html = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); //inflate 메서드를 활용해서 엑티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        setContentView(binding.getRoot());

        mHandler = new Handler();

        connThread thread = new connThread();
        thread.setDaemon(true);
        thread.start();

        checkUpdate thread1 = new checkUpdate();
        thread1.setDaemon(true);
        thread1.start();

        initview();
    }

    public void initview()
    {
        binding.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        binding.alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });

        binding.graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        binding.cctvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CctvActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setSocket(String ips, int ports) throws IOException {
        try {
            socket = new Socket(ips, ports);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    class connThread extends Thread {
        public void run() {
            try {
                setSocket(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class checkUpdate extends Thread {
        public void run() {
            try {
                while (true) {
                    Thread.sleep(500);

                    byte[] a = new byte[18];
                    in = socket.getInputStream();
                    in.read(a);                                                    //바이트로 값을 읽음
                    html = new String(a);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("socket", "main run");
                            if (a[0] == 'A') {
                                String result1 = html.substring(1, 5);                    //실외 온도
                                String result2 = html.substring(5, 9);                    //실외 습도
                                String result3 = html.substring(9, 12);                   //자외선
                                String result4 = html.substring(12, 17);                  //먼지
                                String result5 = html.substring(17, 18);                  //빗물감지
                                Log.d("socket", "html= "+result1);
                                Log.d("socket", "html= "+result2);
                                Log.d("socket", "html= "+result3);
                                Log.d("socket", "html= "+result4);
                                Log.d("socket", "html= "+result5);

                                if (isStrungDouble(result1) == true) {
                                    binding.outTemp.setText(result1 + "℃");                //실외 온도
                                }
                                if (isStrungDouble(result2) == true) {
                                    binding.outHumid.setText(result2 + "%");                //실외 습도
                                }
                                if (isStrungDouble(result3) == true) {
                                    if (Float.parseFloat(result3) < 3.0) {                    //자외선 수치 낮음
                                        binding.outUv.setText("낮음");
                                    } else if (Float.parseFloat(result3) < 6.0) {                 //자외선 수치 보통
                                        binding.outUv.setText("보통");
                                    } else if (Float.parseFloat(result3) < 8.0) {                     //자외선 수치 높음
                                        binding.outUv.setText("높음");
                                    } else if (Float.parseFloat(result3) < 11.0) {                //자외선 수치 매우 높음
                                        binding.outUv.setText("매우높음");
                                    } else { //자외선 수치 위험
                                        binding.outUv.setText("위험");
                                    }
                                }
                                if (isStrungDouble(result4) == true) {
                                    if (Float.parseFloat(result4) < 80.0) {                          //미세먼지 좋음
                                        binding.dust.setText("좋음");
                                        binding.dustImage.setImageResource(R.drawable.dust_good);
                                    } else if (Float.parseFloat(result4) < 80.0) {                      //미세먼지 보통
                                        binding.dust.setText("보통");
                                        binding.dustImage.setImageResource(R.drawable.dust_normal);
                                    } else if (Float.parseFloat(result4) < 150.0) {                        //미세먼지 나쁨
                                        binding.dust.setText("나쁨");
                                        binding.dustImage.setImageResource(R.drawable.dust_bad);
                                    } else {                                                            //미세먼지 매우나쁨
                                        binding.dust.setText("매우나쁨");
                                        binding.dustImage.setImageResource(R.drawable.dust_v_bad);
                                    }
                                }
                                if (result5.equals("1")) {                                                  //날씨 비
                                    binding.weather.setImageResource(R.drawable.rain);
                                } else {                                                                  //날씨 맑음
                                    binding.weather.setImageResource(R.drawable.sun);
                                }
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//    public void checkupdate(byte[] result) {
//
//        if (result != null) {
//            String html = new String(result);
//            if (result[0] == 'A') {
//
//                String result1 = html.substring(1, 5); //실외 온도
//                String result2 = html.substring(5, 9); //실외 습도
//                String result3 = html.substring(9, 12); //자외선
//                String result4 = html.substring(12, 17); //먼지
//                String result5 = html.substring(17, 18);  //빗물감지
//
//                if (isStrungDouble(result1)) {
//                    binding.outTemp.setText(result1 + "℃"); //실외 온도
//                }
//                if (isStrungDouble(result2)) {
//                    binding.outHumid.setText(result2 + "%"); //실외 습도
//                }
//                if (isStrungDouble(result3)) {
//                    if (Float.parseFloat(result3) < 3.0) { //자외선 수치 낮음
//                        binding.outUv.setText("낮음");
//                    } else if (Float.parseFloat(result3) < 6.0) { //자외선 수치 보통
//                        binding.outUv.setText("보통");
//                    } else if (Float.parseFloat(result3) < 8.0) { //자외선 수치 높음
//                        binding.outUv.setText("높음");
//                    } else if (Float.parseFloat(result3) < 11.0) { //자외선 수치 매우 높음
//                        binding.outUv.setText("매우높음");
//                    } else { //자외선 수치 위험
//                        binding.outUv.setText("위험");
//                    }
//                }
//                if (isStrungDouble(result4)) {
//                    if (Float.parseFloat(result4) < 80.0) { //미세먼지 좋음
//                        binding.dust.setText("좋음");
//                        binding.dustImage.setImageResource(R.drawable.dust_good);
//                    } else if (Float.parseFloat(result4) < 80.0) { //미세먼지 보통
//                        binding.dust.setText("보통");
//                        binding.dustImage.setImageResource(R.drawable.dust_normal);
//                    } else if (Float.parseFloat(result4) < 150.0) { //미세먼지 나쁨
//                        binding.dust.setText("나쁨");
//                        binding.dustImage.setImageResource(R.drawable.dust_bad);
//                    } else { //미세먼지 매우나쁨
//                        binding.dust.setText("매우나쁨");
//                        binding.dustImage.setImageResource(R.drawable.dust_v_bad);
//                    }
//                }
//
//                if (result5.equals("1")) { //날씨 비
//                    binding.weather.setImageResource(R.drawable.rain);
//                } else { //날씨 맑음
//                    binding.weather.setImageResource(R.drawable.sun);
//                }
//            }
//        }
//    }

//    public void setSocket(){
//        try {
//            socket = new Socket(ip, port);
//            Log.d("socket", "연결성공");
//
//            if (socket.isConnected()) {
//                OutputStream output = socket.getOutputStream();
//                InputStream input = socket.getInputStream();
//                Log.d("socket", "연결 성공");
//
//                while (true) {
//                    int maxBufferSize = 18;
//                    byte[] recvBuffer = new byte[maxBufferSize];
//                    DataInputStream dataInputStream = new DataInputStream(input);
//
//                    try {
//                        //버퍼(recvBuffer) 인자로 넣어서 받음. 반환 값은 받아온 size
//                        int nReadSize = dataInputStream.read(recvBuffer);
//                        //받아온 값이 0보다 클때
//                        if (nReadSize > 0) {
//                            byte[] temp = new byte[nReadSize];
//                            System.arraycopy(recvBuffer, 0, temp, 0, nReadSize);
//
//                            checkupdate(temp);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public boolean isStrungDouble(String s){
        try{
            Double.parseDouble(s);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
}
