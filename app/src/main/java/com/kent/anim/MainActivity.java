package com.kent.anim;


import com.kent.anim.demo.rxanim.ScaleAnimActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

//    @BindView(R.id.list_view)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        listView = findViewById(R.id.list_view);

        init();
    }


    private void init() {

        List<String> list = new ArrayList<>();
        list.add(getString(R.string.main_type_android_arc));

        ArrayAdapter<String> listAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Activity activity = MainActivity.this;

            final String actionName = ((TextView) view).getText().toString();

            if (actionName.equals(activity.getString(R.string.main_type_android_arc))) {
                ScaleAnimActivity.launch(activity);
            }
        }
    };

}

