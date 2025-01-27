package com.example.proyectoandroid_soler_martin_perez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class newUserMenu extends AppCompatActivity {

    /*
     * Clase mediante la que realizamos la creacion de usuarios en la base de datos
     * (Solo administradores de la aplicacion)
     */
    private EditText mEditTextNombre;
    private EditText mEditTextCorreo;
    private EditText mEditTextPass;
    private Button btnRegistrar;
    private Button btnCerrar;

    private String nombre;
    private String correo;
    private String pass;

    FirebaseAuth mAuth;
    DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user_menu);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        mEditTextNombre = (EditText) findViewById(R.id.editTextName);
        mEditTextCorreo = (EditText) findViewById(R.id.editTextCorreo);
        mEditTextPass = (EditText) findViewById(R.id.editTextPass);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        btnCerrar = (Button) findViewById(R.id.btnLogOut);

        btnRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                nombre = mEditTextNombre.getText().toString();
                correo = mEditTextCorreo.getText().toString();
                pass = mEditTextPass.getText().toString();

                if(!nombre.isEmpty() && !correo.isEmpty() && !pass.isEmpty()){
                    if(pass.length() >= 6){
                        RegistrarUsuario();

                    }else{
                        Toast.makeText(newUserMenu.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(newUserMenu.this, "No puede haber campos vacios!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("datos", MODE_PRIVATE).edit();
                editor.clear().apply();
                Intent intent = new Intent(newUserMenu.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void RegistrarUsuario(){
        //Metodo para el registro de usuarios
        mAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if(task.isSuccessful()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", nombre);
                    map.put("correo", correo);
                    map.put("pass", pass);

                    //String id = mAuth.getCurrentUser().getUid();

                    mDataBase.child("Users").child("Clientes").child(correo).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                startActivity(new Intent(newUserMenu.this, usuario_creado.class));
                            }else{
                                Toast.makeText(newUserMenu.this, "No se pudieron crear los datos correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(newUserMenu.this, "No se pudo registrar este usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
