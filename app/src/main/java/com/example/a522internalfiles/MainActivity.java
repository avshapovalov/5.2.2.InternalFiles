package com.example.a522internalfiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private EditText loginText;
    private TextView passwordText;
    private CheckBox changeStorage;
    public File loginAndPasswordFile;
    private SharedPreferences typeStorageResult;
    private SharedPreferences.Editor typeStorageResultEditor;
    public static final String LOG_TAG = "Ошибка!";

    private void checkLoginAndpassword(StringBuilder loginBuilder) {
        String[] loginAndPassword = loginBuilder.toString().split(";");
        if (loginAndPassword[0].equals(loginText.getText().toString()) && loginAndPassword[1].equals(passwordText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Авторизация прошла успешно", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Неверные логин или пароль", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFilesBeforeRegistration(String loginToFile, String passwordToFile) {
        if (loginToFile.isEmpty()){
            Toast.makeText(getApplicationContext(), "Требуется указать логин!", Toast.LENGTH_SHORT).show();
            return;
        } else if (passwordToFile.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Требуется ввести пароль!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File getFile() {
        return new File(getFolder(this, "userdata"), "loginandpassword.txt");
    }

    public File getFolder(Context context, String albumName) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginText = findViewById(R.id.loginText);
        passwordText = findViewById(R.id.passwordText);
        changeStorage = findViewById(R.id.cbx_external_storage);

        typeStorageResult = getSharedPreferences("storage_type", MODE_PRIVATE);
        typeStorageResultEditor = typeStorageResult.edit();
        typeStorageResultEditor.putBoolean("storage_type", changeStorage.isChecked());
        typeStorageResultEditor.apply();

        changeStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                typeStorageResult = getSharedPreferences("storage_type", MODE_PRIVATE);
                typeStorageResultEditor = typeStorageResult.edit();
                typeStorageResultEditor.putBoolean("storage_type", changeStorage.isChecked());
                typeStorageResultEditor.apply();

                if (typeStorageResult.getBoolean("storage_type", false) == false) {
                    Toast.makeText(getApplicationContext(), "Выбрано внутреннее хранилище", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Выбрано внешнее хранилище", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void clickToCheck(View view) {
        if (typeStorageResult.getBoolean("storage_type", false) == false) {
            try {

                FileInputStream fileInputStreamLogin = openFileInput("loginfile");
                InputStreamReader isrlogin = new InputStreamReader(fileInputStreamLogin);
                BufferedReader loginReader = new BufferedReader(isrlogin);
                String line = loginReader.readLine();

                StringBuilder loginBuilder = new StringBuilder();

                while (line != null) {
                    Log.d("Tag", line);
                    loginBuilder.append(line);
                    line = loginReader.readLine();
                }
                loginReader.close();

                checkLoginAndpassword(loginBuilder);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            loginAndPasswordFile = getFile();
            if (!loginAndPasswordFile.canRead()) {
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();
            try (Scanner scanner = new Scanner(new FileReader(loginAndPasswordFile))) {
                while (scanner.hasNextLine()) {
                    stringBuilder.append(scanner.nextLine());
                }
                checkLoginAndpassword(stringBuilder);
            } catch (IOException e) {
                Toast.makeText(this, "Cannot access file!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void clickToRegister(View view) {
        String loginToFile = loginText.getText().toString();
        String passwordToFile = passwordText.getText().toString();

        checkFilesBeforeRegistration(loginToFile, passwordToFile);

        if (typeStorageResult.getBoolean("storage_type", false) == false) {
            try {
                FileOutputStream fileOutputStreamLogin = openFileOutput("loginfile", MODE_PRIVATE);
                OutputStreamWriter outputStreamWriterLogin = new OutputStreamWriter(fileOutputStreamLogin);
                BufferedWriter bwlogin = new BufferedWriter(outputStreamWriterLogin);
                bwlogin.write(loginToFile + ";" + passwordToFile);
                bwlogin.close();

                Toast.makeText(getApplicationContext(), "Данные сохранены на внутреннее хранилище", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loginAndPasswordFile = getFile();
            try {
                Writer writer = new FileWriter(loginAndPasswordFile);
                writer.write(loginToFile + ";" + passwordToFile);
                writer.close();
                Toast.makeText(getApplicationContext(), "Данные сохранены на внутреннее хранилище", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}
