package it.mirea.myapplication;

import static it.mirea.myapplication.user.User.TYPE.AUTHOR;
import static it.mirea.myapplication.user.User.TYPE.READER;
import static it.mirea.myapplication.user.User.TYPE.SUPERVISOR;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;

import it.mirea.myapplication.user.User;


public class EntryActivity extends Activity {
    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 1;
    Button buttonEntry, buttonRegister;
    ConstraintLayout root;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences("Login session", Context.MODE_PRIVATE);
        editor = mSettings.edit();
        buttonEntry = findViewById(R.id.buttonEntry);
        buttonRegister = findViewById(R.id.buttonRegister);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        users = db.getReference("/");
        root = findViewById(R.id.rootElement);
        setContentView(R.layout.entry_page);
        requestAppPermissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ClickRegister(View view) {
        /*Intent intent=new Intent(EntryActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();*/
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialog.setTitle("Регистрация");
        dialog.setMessage("Введите все данные для регистрации");

        LayoutInflater inflater = LayoutInflater.from(this);
        View registerWindow = inflater.inflate(R.layout.register_page, null);
        EditText editText = (EditText) registerWindow.findViewById(R.id.editTextTextPersonName);
        EditText editNumber = (EditText) registerWindow.findViewById(R.id.editTextPhone);
        EditText editEmail = (EditText) registerWindow.findViewById(R.id.editTextTextEmailAddress2);
        EditText password = (EditText) registerWindow.findViewById(R.id.editTextTextPassword2);
        RadioGroup radioGroup = registerWindow.findViewById(R.id.radioGroup);

        dialog.setView(registerWindow);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                    Toast.makeText(EntryActivity.this, "Введите Вашу почту", Toast.LENGTH_SHORT).show();
                    //Snackbar.make(root, "Введите Вашу почту", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editNumber.getText().toString())) {
                    Snackbar.make(root, "Введите Ваш номер", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    Snackbar.make(root, "Введите Ваше имя", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() < 8) {
                    Snackbar.make(root, "Пароль должен быть больше 8 символов", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (radioGroup == null) {
                    Snackbar.make(root, "Выбери тип учетной записи", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                // Регистрация пользователя
                auth.createUserWithEmailAndPassword(editEmail.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setName(editText.getText().toString());
                                new Single().getInstance().name = editText.getText().toString();
                                user.setEmail(editEmail.getText().toString());
                                new Single().getInstance().email = editEmail.getText().toString();
                                user.setPhone(editNumber.getText().toString());
                                new Single().getInstance().number = editNumber.getText().toString();

                                editor.putString("nickname", editEmail.getText().toString()).commit();
                                editor.putString("password", password.getText().toString()).commit();
                                //settings.setPreferences("nickname", editEmail.getText().toString());
                                //settings.setPreferences("password", password.getText().toString());

                                int selectedId = radioGroup.getCheckedRadioButtonId();
                                switch(selectedId){
                                    case R.id.radioButton:
                                        // do operations specific to this selection
                                        user.setType(READER);
                                        new Single().getInstance().type = 0;
                                        break;
                                    case R.id.radioButton2:
                                        // do operations specific to this selection
                                        user.setType(AUTHOR);
                                        new Single().getInstance().type = 1;
                                        break;
                                }
                                user.setPass(password.getText().toString());


                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(EntryActivity.this, "Вы зарегистрировались!", Toast.LENGTH_SHORT).show();
                                                //new Single().getInstance().logic = true;
                                                //editor.putBoolean("log", true).commit();
                                                //settings.setPreferencesBoolean("log", true);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EntryActivity.this, "Хуй!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("reading")
                                        .setValue("{\"Map\":  {}}")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(EntryActivity.this, "Вы зарегистрировались!", Toast.LENGTH_SHORT).show();
                                                new Single().getInstance().logic = true;
                                                editor.putBoolean("log", true).commit();
                                                //settings.setPreferencesBoolean("log", true);

                                                Intent intent = new Intent(EntryActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EntryActivity.this, "Хуй!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EntryActivity.this, "Что-то не так", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        new Single().getInstance().auth = auth;
        new Single().getInstance().db = db;
        dialog.show();

    }

    public void ClickMain(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialog.setTitle("Войти");
        dialog.setMessage("Введите все данные для входа");

        LayoutInflater inflater = LayoutInflater.from(this);
        View signInWindow = inflater.inflate(R.layout.sign_in_page, null);
        dialog.setView(signInWindow);

        final EditText editEmail = (EditText) signInWindow.findViewById(R.id.editTextTextEmailAddress2);
        final EditText password = (EditText) signInWindow.findViewById(R.id.editTextTextPassword2);
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                    Snackbar.make(root, "Введите Вашу почту", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (password.getText().toString().length() < 8) {
                    Snackbar.make(root, "Пароль должен содержать больше 8 символов", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                editor.putString("nickname", editEmail.getText().toString()).commit();
                editor.putString("password", password.getText().toString()).commit();
                auth.signInWithEmailAndPassword(editEmail.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        new Single().getInstance().logic = true;
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        users.child(firebaseUser.getUid()).child("type").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                }
                                else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    String type = (String) task.getResult().getValue();
                                    switch (type){
                                        case "READER":
                                            new Single().getInstance().type = 0;
                                            break;
                                        case "AUTHOR":
                                            new Single().getInstance().type = 1;
                                            break;
                                        case "SUPERVISOR":
                                            new Single().getInstance().type = 2;
                                            break;
                                    }
                                }
                            }
                        });
                        editor.putBoolean("log", true).commit();
                        //settings.setPreferencesBoolean("log", true);
                        users.child(firebaseUser.getUid()).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    new Single().getInstance().name = (String) task.getResult().getValue();
                                }
                            }
                        });
                        users.child(firebaseUser.getUid()).child("phone").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    new Single().getInstance().number = (String) task.getResult().getValue();
                                }
                            }
                        });
                        users.child(firebaseUser.getUid()).child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    new Single().getInstance().email = (String) task.getResult().getValue();
                                }
                            }
                        });

                        users.child(firebaseUser.getUid()).child("reading").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    new Single().getInstance().reading = (String) task.getResult().getValue();
                                }
                            }
                        });

                        startActivity(new Intent(EntryActivity.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EntryActivity.this, "Ошибка авторизации " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        new Single().getInstance().auth = auth;
        new Single().getInstance().db = db;
        dialog.show();

    }

   @Override
    public void onResume() {
       Intent intentToProfile;
        super.onResume();
        mSettings = getSharedPreferences("Login session", Context.MODE_PRIVATE);
        if (mSettings != null) {
            if (mSettings.getBoolean("log", false) && mSettings.getString("nickname", "") != "" &&
                    mSettings.getString("nickname", "").toString() != null) {
                auth.signInWithEmailAndPassword(mSettings.getString("nickname", "").toString(), mSettings.getString("password", ""));
                new Single().getInstance().logic = true;
                FirebaseUser firebaseUser = auth.getCurrentUser();
                users.child(firebaseUser.getUid()).child("type").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            String type = (String) task.getResult().getValue();
                            switch (type) {
                                case "READER":
                                    new Single().getInstance().type = 0;
                                    break;
                                case "AUTHOR":
                                    new Single().getInstance().type = 1;
                                    break;
                                case "SUPERVISOR":
                                    new Single().getInstance().type = 2;
                                    break;
                            }
                        }
                    }
                });
                users.child(firebaseUser.getUid()).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            new Single().getInstance().name = (String) task.getResult().getValue();
                        }
                    }
                });
                users.child(firebaseUser.getUid()).child("phone").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            new Single().getInstance().number = (String) task.getResult().getValue();
                        }
                    }
                });
                users.child(firebaseUser.getUid()).child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            new Single().getInstance().email = (String) task.getResult().getValue();
                        }
                    }
                });

                users.child(firebaseUser.getUid()).child("reading").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            new Single().getInstance().reading = (String) task.getResult().getValue();
                        }
                    }
                });
                new Single().getInstance().auth = auth;
                new Single().getInstance().db = db;
/*                intentToProfile = new Intent(EntryActivity.this, ProfileActivity.class);
                switch(new Single().getInstance().type){
                    case 0:
                        intentToProfile = new Intent(EntryActivity.this, ProfileActivity.class);
                        break;
                    case 1:
                        intentToProfile = new Intent(EntryActivity.this, ProfileAuthorActivity.class);
                        break;
                    case 2:
                        intentToProfile = new Intent(EntryActivity.this, ProfileSupervisorActivity.class);
                        break;
                }*/
                startActivity(new Intent(EntryActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
}