package it.mirea.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RegisterActivity extends Activity{

    RadioGroup radioGroup;
    RadioButton radioReader, radioSupervisor, radioAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
    }

    public void Register(View view) {

        new Single().getInstance().logic = true;
        final EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
        final EditText editNumber = (EditText) findViewById(R.id.editTextPhone);
        final EditText editEmail = (EditText) findViewById(R.id.editTextTextEmailAddress2);
        new Single().getInstance().name = editText.getText().toString();
        new Single().getInstance().number = editNumber.getText().toString();
        new Single().getInstance().email = editEmail.getText().toString();
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        System.out.println(new Single().getInstance().name);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioReader = (RadioButton) findViewById(R.id.radioButton);
        radioAuthor = (RadioButton) findViewById(R.id.radioButton2);
        new Single().getInstance().type = 0;
        switch(selectedId) {
            case R.id.radioButton:
                new Single().getInstance().type = 0;
                break;
            case R.id.radioButton2:
                new Single().getInstance().type = 1;
                break;
        }
        startActivity(intent);
        finish();
    }
}
