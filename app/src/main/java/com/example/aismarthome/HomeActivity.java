package com.example.aismarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.aismarthome.databinding.ActivityHomeBinding;
import com.example.aismarthome.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    public Socket socket;
    public InputStream in;
    private OutputStream out;
//    private String ip = "220.69.209.73"; // IP
    private String ip = "220.69.207.111"; // IP
    private int port = 9797; // PORT 번호

    private Handler mHandler, mHandler1;

    private String html = "";

    public byte aa[] = new byte[18];  //제어값을 담는 배열
    byte con_a[] = new byte[18];
    public boolean tempcon_checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());       //inflate 메서드를 활용해서 엑티비티에서 사용할 바인딩 클래스의
        setContentView(binding.getRoot());

        mHandler = new Handler();
        connThread thread = new connThread();
//        thread.setDaemon(true);
        thread.start();

        checkUpdate thread1 = new checkUpdate();
//        thread1.setDaemon(true);
        thread1.start();

//        contr_thread thread2 = new contr_thread();
//        thread2.start();
        tempconchek();
        con_temp();
        init();
    }

    public void con_temp(){
        binding.livConTemp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String cons = binding.livConTemp.getText().toString();
                con_a = cons.getBytes();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if(tempcon_checked == true) {
                                try {
                                    Thread.sleep(5000);
                                    out = socket.getOutputStream();
                                    out.write(con_a);
                                    out.flush();

                                    Log.d("vvaa", "val " + con_a[0]);
                                    Log.d("vvaa", "val " + cons);
                                } catch (InterruptedException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.roomConTemp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String cons = binding.roomConTemp.getText().toString();
                con_a = cons.getBytes();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if(tempcon_checked == true) {
                                try {
                                    Thread.sleep(5000);
                                    out = socket.getOutputStream();
                                    out.write(con_a);
                                    out.flush();

                                    Log.d("vvaa", "vala " + con_a[0]);
                                    Log.d("vvaa", "vala " + cons);

                                } catch (InterruptedException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{

                            }

                        }
                    }
                }).start();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public void tempconchek(){
        binding.tempConSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tempcon_checked = true;
                }
                else{
                    tempcon_checked = false;
                }
            }
        });
    }

    public void init(){
        binding.livLedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("button", "sa" +aa);
                if(b){
                    aa[0] = 'L' ;
                    aa[1] = '1';
                    Log.d("button", "b" +aa[0]);
                    Log.d("button", "b" +aa[1]);
                }
                else{
                    aa[0]='L';
                    aa[1]='0';
                    Log.d("button", "b1" +aa[0]);
                    Log.d("button", "b1" +aa[1]);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            out = socket.getOutputStream();
                            out.write(aa);
                            out.flush();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        binding.roomLedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("button", "sa" +aa);
                if(b){
                    aa[0] = 'R' ;
                    aa[1] = '1';
                    Log.d("button", "b" +aa[0]);
                    Log.d("button", "b" +aa[1]);
                }
                else{
                    aa[0]='R';
                    aa[1]='0';
                    Log.d("button", "b1" +aa[0]);
                    Log.d("button", "b1" +aa[1]);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            out = socket.getOutputStream();
                            out.write(aa);
                            out.flush();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        binding.entLedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("button", "sa" +aa);
                if(b){
                    aa[0] = 'E' ;
                    aa[1] = '1';
                    Log.d("button", "b" +aa[0]);
                    Log.d("button", "b" +aa[1]);
                }
                else{
                    aa[0]='E';
                    aa[1]='0';
                    Log.d("button", "b1" +aa[0]);
                    Log.d("button", "b1" +aa[1]);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            out = socket.getOutputStream();
                            out.write(aa);
                            out.flush();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        binding.fanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("button", "sa" +aa);
                if(b){
                    aa[0] = 'K' ;
                    aa[1] = '1';
                    Log.d("button", "b" +aa[0]);
                    Log.d("button", "b" +aa[1]);
                }
                else{
                    aa[0]='K';
                    aa[1]='0';
                    Log.d("button", "b1" +aa[0]);
                    Log.d("button", "b1" +aa[1]);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            out = socket.getOutputStream();
                            out.write(aa);
                            out.flush();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        binding.livWindowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                byte a[] = new byte[18];
                Log.d("button", "sa" +aa);
                if(b){
                    aa[0] = 'X' ;
                    aa[1] = '1';
                    Log.d("button", "b" +aa[0]);
                    Log.d("button", "b" +aa[1]);
                }
                else{
                    aa[0]='X';
                    aa[1]='0';
                    Log.d("button", "b1" +aa[0]);
                    Log.d("button", "b1" +aa[1]);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            out = socket.getOutputStream();
                            out.write(aa);
                            out.flush();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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
                    Log.d("hom_socket", "1");
                    byte[] a = new byte[18];
                    Log.d("hom_socket", "a= "+a);
                    in = socket.getInputStream();
                    Log.d("hom_socket", "in= "+in);
                    in.read(a); //바이트로 값을 읽음
                    Log.d("hom_socket", "a2= "+a);
                    html = new String(a);
                    Log.d("hom_socket", "html= "+html);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("socket", "main run");
                            if (a[0] == 'A') {
                                String result1 = html.substring(1, 5); //실외 온도
                                String result2 = html.substring(5, 9); //실외 습도

                                Log.d("hom_socket", "html= " + result1);
                                Log.d("hom_socket", "html= " + result2);

                                if (isStrungDouble(result1) == true) {
                                    binding.outTemp.setText(result1 + "℃"); //실외 온도
                                }
                                if (isStrungDouble(result2) == true) {
                                    binding.outHumid.setText(result2 + "%"); //실외 습도
                                }
                            }

                            if (a[0] == 'B') {
                                String result1 = html.substring(1, 5); //거실 온도
                                String result2 = html.substring(5, 9); //거실 습도
                                String result3 = html.substring(9, 10); //발열
                                String result4 = html.substring(10, 11); //쿨러
                                String result5 = html.substring(11, 12); //거실 창문
                                String result6 = html.substring(12, 13); //거실 알람
                                String result7 = html.substring(13, 14); //거실 LED
                                Log.d("hom_socket", "html= " + result1);
                                Log.d("hom_socket", "html= " + result2);
                                Log.d("hom_socket", "html= " + result3);
                                Log.d("hom_socket", "html= " + result4);
                                Log.d("hom_socket", "html= " + result5);
                                Log.d("hom_socket", "html= " + result6);
                                Log.d("hom_socket", "html= " + result7);

                                if (isStrungDouble(result1) == true) {
                                    binding.livingTemp.setText(result1 + "℃");
                                }
                                if (result5.equals("1")) { //거실 창문 스위치 상태 on
                                    binding.livWindowSwitch.setChecked(true);
                                } else if (result5.equals("0")) {    //거실 창문 스위치 상태 off
                                    binding.livWindowSwitch.setChecked(false);
                                }

                                if (result7.equals("1")) { //거실 LED 스위치 상태 on
                                    binding.livLedSwitch.setChecked(true);
                                } else if (result7.equals("0")) { //거실 LED 스위치 상태 off
                                    binding.livLedSwitch.setChecked(false);
                                }
                            }

                            if (a[0] == 'C') {
                                String result1 = html.substring(1, 5); //방 온도
                                String result2 = html.substring(5, 9); //방 습도
                                String result3 = html.substring(9, 10); // 방 발열
                                String result4 = html.substring(10, 11); // 방 쿨러
                                String result5 = html.substring(11, 12); //방 창문
                                String result6 = html.substring(12, 13); //방 알람
                                String result7 = html.substring(13, 14); //방 LED
                                Log.d("hom_socket", "html= " + result1);
                                Log.d("hom_socket", "html= " + result2);
                                Log.d("hom_socket", "html= " + result3);
                                Log.d("hom_socket", "html= " + result4);
                                Log.d("hom_socket", "html= " + result5);
                                Log.d("hom_socket", "html= " + result6);
                                Log.d("hom_socket", "html= " + result7);

                                if (isStrungDouble(result1) == true) {
                                    binding.roomTemp.setText(result1 + "℃");
                                }
                                if (result5.equals("1")) { //방 창문 스위치 상태 on
                                } else if (result5.equals("0")) {    //방 창문 스위치 상태 off
                                }

                                if (result7.equals("1")) { //방 LED 스위치 상태 on
                                    binding.roomLedSwitch.setChecked(true);
                                } else if (result7.equals("0")) { //방 LED 스위치 상태 off
                                    binding.roomLedSwitch.setChecked(false);
                                }
                            }

                            if (a[0] == 'D') {
                                String result1 = html.substring(1, 4); //부엌 가스
                                String result2 = html.substring(4, 5); //환풍기
                                String result3 = html.substring(5, 6); //인체 감지
                                String result4 = html.substring(6, 7); //현관 LED

                                Log.d("hom_socket", "result1 = " + result1);
                                Log.d("hom_socket", "result2 = " + result2);
                                Log.d("hom_socket", "result3 = " + result3);
                                Log.d("hom_socket", "result4 = " + result4);

                                if (isStrungDouble(result1) == true) {
                                    binding.inGas.setText(result1);
                                }

                                if (result2.equals("1")) { //부엌 환풍기 스위치 상태 on
                                    binding.fanSwitch.setChecked(true);
                                } else if (result2.equals("0")) { //부엌 환풍기 스위치 상태 off
                                    binding.fanSwitch.setChecked(false);
                                }

                                if (result4.equals("1")) { //현관 LED 스위치 상태 on
                                    binding.entLedSwitch.setChecked(true);
                                } else if (result4.equals("0")) {    //현관 LED 스위치 상태 off
                                    binding.entLedSwitch.setChecked(false);
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

//    class contr_thread extends Thread {
//        public void run() {
//            try {
//                byte a[]=new byte[18];
//                out=socket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    binding.livLedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                        try {
//                            out= socket.getOutputStream();
//                            byte a[] = new byte[18];
//
//                            if(isChecked){
//                                a[0]='L';
//                                a[1]='0';
//                            }
//                            else{
//                                a[0]='L';
//                                a[1]='1';
//                            }
//
//                            out.write(a);
//                            out.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//            });
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