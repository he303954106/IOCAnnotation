package com.hk.iocannotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.hk.ioclibrary.InjectManager;
import com.hk.ioclibrary.annotation.ContentView;
import com.hk.ioclibrary.annotation.InjectView;
import com.hk.ioclibrary.annotation.OnClick;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.tv)
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectManager.inject(this);

        mTextView.setText("change");
    }

    @OnClick(R.id.tv)
    private void method(){
        mTextView.setText("method");
    }
}
