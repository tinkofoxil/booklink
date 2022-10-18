package it.mirea.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ViewUsersActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_users_book_page);
    }


    public void ClickMain(View view) {
        Intent intent = new Intent(ViewUsersActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickProfile(View view) {
        Intent intent;
        intent = new Intent(ViewUsersActivity.this, ProfileActivity.class);
        switch (new Single().getInstance().type) {

            case 0:
                intent = new Intent(ViewUsersActivity.this, ProfileActivity.class);
                break;
            case 1:
                intent = new Intent(ViewUsersActivity.this, ProfileAuthorActivity.class);
                break;
            case 2:
                intent = new Intent(ViewUsersActivity.this, ProfileSupervisorActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }

    public void ClickSettings(View view) {
        Intent intent = new Intent(ViewUsersActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

}
