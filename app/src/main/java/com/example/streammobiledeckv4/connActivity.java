package com.example.streammobiledeckv4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class connActivity extends AppCompatActivity {

    String HOST;
    final int PUERTO = 5000;
    DataInputStream in;
    DataOutputStream out;
    String[] array;
    List<String> listaCanciones;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        HOST = intent.getStringExtra("scannedData");


        new Thread(this::connSocket).start();
        linearLayout = findViewById(R.id.linearLayout);



    }

    void connSocket() {
        try {
            Socket sc = new Socket(HOST, PUERTO);
            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            out.writeUTF("conectado");

            String respuesta = in.readUTF();

            // Actualizar el TextView en el hilo principal
            runOnUiThread(() -> {

                Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show();
                System.out.println("Conectado: " + respuesta);
                listaCanciones = Arrays.asList(respuesta.split(","));
                LinearLayout();
            });

            sc.close();
        } catch (IOException e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error de conexión: "+ e.getMessage());
            });
            e.printStackTrace();

        }
    }

    void LinearLayout(){

        for (String item : listaCanciones) {
            Button button = new Button(this);
            button.setText(item);
            button.setOnClickListener(v -> {
                new Thread(() -> SendSound(item)).start();
                System.out.println("Clicked: " + item);
            });
            linearLayout.addView(button);
        }
    }

    void SendSound(String item){
        try {
            Socket sc = new Socket(HOST, PUERTO);
            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            out.writeUTF(item);

            String respuesta = in.readUTF();
            System.out.println("Respuesta: " + respuesta);

            sc.close();
        } catch (IOException e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error de conexión: "+ e.getMessage());
            });
            e.printStackTrace();

        }
    }
}
