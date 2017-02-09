package com.example.quentin.mapmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

/**
 * Created by tiphaine on 08/02/17.
 */

public class OptionActivity extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        setTitle("Option");
    }
    public void onRadioButtonCliked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()){
            case R.id.radioButton1:
                if(checked) {}
                break;
            case R.id.radioButton2:
                if(checked){}
                break;


        }

    }
}
