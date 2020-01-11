package com.example.shark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_76;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;

public class MainActivity extends AppCompatActivity {
    private TextView tv_shake,tvMsg;
    private Button btn_shake;
    private ScrollView svContent;
    private Client mClient;
    int i=0;
    private static final int STATUS_CLOSE = 0;
    private static final int STATUS_CONNECT = 1;
    private static final int STATUS_MESSAGE = 2;

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String message = String.format("%s\n", msg.obj.toString());
            if ("#123#456\n".equals(message)){
                tvMsg.append("开始");
                Intent intent=new Intent(MainActivity.this,ShakeActivity.class);
                startActivity(intent);
                mClient.close();
                finish();
            }else {
                tvMsg.append(message);}
            svContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    svContent.fullScroll(View.FOCUS_DOWN);
                }
            }, 100);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_shake =  findViewById(R.id.tv_shake);
        tvMsg=findViewById(R.id.tvMsg);
        btn_shake=findViewById(R.id.btn_shake);
        svContent=findViewById(R.id.svContent);
        connectToWebsocket();
        btn_shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mClient) {
                    String msg ="#123#456";
                    if (!TextUtils.isEmpty(msg)) {
                        try {
                            mClient.send(msg);
                            btn_shake.setEnabled(false);
                        } catch (NotYetConnectedException e) {
                            e.printStackTrace();
                            return;
                        }

                    }
                }
            }
        });

        Intent intent=getIntent();
        int times=intent.getIntExtra("times",0);
        tv_shake.setText(times+"");
        sendgrade();
    }


    private void connectToWebsocket(){
        String ip = "192.168.43.188";
        String port = "2445";
        String address = String.format("ws://%s:%s", ip, port);
        Draft draft = new Draft_17();

        try {
            URI uri = new URI(address);
            mClient = new Client(uri, draft);
            mClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

    }
    private class Client extends WebSocketClient {

        public Client(URI serverURI) {
            super(serverURI);
        }

        public Client(URI serverUri, Draft draft) {
            super(serverUri, draft);
        }

        @Override
        public void onOpen(ServerHandshake handShakeData) {

        }

        @Override
        public void onMessage(String message) {
            Message msg = new Message();
            msg.what = STATUS_MESSAGE;
            msg.obj = message;
            mHandle.sendMessage(msg);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {

        }

        @Override
        public void onWebsocketPong(WebSocket conn, Framedata f) {
            super.onWebsocketPong(conn, f);
        }

        @Override
        public void onWebsocketPing(WebSocket conn, Framedata f) {
            super.onWebsocketPing(conn, f);
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
        }

        public String parseFramedata(Framedata framedata){
            String result = "null";
            ByteBuffer buffer = framedata.getPayloadData();
            if(null == buffer){
                return result;
            }
            byte[] data = buffer.array();
            if(null != data && data.length > 0){
                return new String(data);
            }
            return result;
        }
    }

  private void sendgrade() {
        System.out.println(tv_shake.getText().toString());

      if (!"0".equals(tv_shake.getText().toString()))
      {
          if (null != mClient) {
              String msg ="徐海帆:"+tv_shake.getText();
              if (!TextUtils.isEmpty(msg)) {
                  try {
                      while (!mClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                          Log.i("GradeActivity","waiting...");
                      }
                      mClient.send(msg);
                  } catch (NotYetConnectedException e) {
                      e.printStackTrace();
                      return;
                  }
              }

          }
      }
  }
}
