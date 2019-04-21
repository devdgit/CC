package com.example.leena.mypills;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ConversationService myConversationService = null;
    private TextView chatDisplayTV;
    private EditText userStatementET;
    private final String IBM_USERNAME = "80002103-9cb1-4c32-90b7-20b9e2cbbcc2";
    private final String IBM_PASSWORD = "3Y0qjDB1mKju";
    private final String IBM_WORKSPACE_ID = "694a31dd-9ee0-4a45-a9e1-3653daaf81eb";

    Button speakBtn;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        speakBtn=findViewById(R.id.speak_btn);
        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        chatDisplayTV = findViewById(R.id.tv_chat_display);
        userStatementET = (EditText)findViewById(R.id.et_user_statement);

        //instantiating IBM Watson Conversation Service
        myConversationService =
                new ConversationService(
                        "2017-12-06",
                        IBM_USERNAME,
                        IBM_PASSWORD
                );

        userStatementET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int action, KeyEvent keyEvent) {
                if (action == EditorInfo.IME_ACTION_DONE) {
                    //show the user statement
                    final String userStatement = userStatementET.getText().toString();
                    chatDisplayTV.append(
                            Html.fromHtml("<p><b>YOU:</b> " + userStatement + "</p>")
                    );
                    userStatementET.setText("");

                    MessageRequest request = new MessageRequest.Builder()
                            .inputText(userStatement)
                            .build();
                    // initiate chat conversation
                    myConversationService
                            .message(IBM_WORKSPACE_ID, request)
                            .enqueue(new ServiceCallback<MessageResponse>() {
                                @Override
                                public void onResponse(MessageResponse response) {
                                    final String botStatement = response.getText().get(0);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatDisplayTV.append(
                                                    Html.fromHtml("<p><b>BOT:</b> " +
                                                            botStatement + "</p>")
                                            );
                                        }
                                    });

                                    t1.speak(botStatement, TextToSpeech.QUEUE_FLUSH, null);

                                    // if the intent is joke then we access the third party
                                    // service to get a random joke and respond to user
//                                    if (response.getIntents().get(0).getIntent().endsWith("Joke")) {
//                                        final Map<String, String> params = new HashMap<String, String>() {{
//                                            put("Accept", "text/plain");
//                                        }};
//                                        Fuel.get("https://icanhazdadjoke.com/").header(params)
//                                                .responseString(new Handler<String>() {
//                                                    @Override
//                                                    public void success(Request request, Response response, String body) {
//                                                        Log.d(TAG, "" + response + " ; " + body);
//                                                        chatDisplayTV.append(
//                                                                Html.fromHtml("<p><b>BOT:</b> " +
//                                                                        body + "</p>")
//                                                        );
//                                                    }
//
//                                                    @Override
//                                                    public void failure(Request request, Response response, FuelError fuelError) {
//                                                    }
//                                                });
//                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.d(TAG, e.getMessage());
                                }
                            });
                }
                return false;
            }
        });
    }

    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    userStatementET.setText(result.get(0));
                }
                break;
            }

        }
    }
}

