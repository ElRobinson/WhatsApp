package com.robinson.luis.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Cadastro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btn = (Button)findViewById(R.id.btnCadCadastrar);

        btn.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                EditText txtNome = (EditText) findViewById(R.id.txtCadNome);
                EditText txtSenha = (EditText) findViewById(R.id.txtCadSenha);
                EditText txtSenha2 = (EditText) findViewById(R.id.txtCadSenha2);
                final EditText txtEmail = (EditText) findViewById(R.id.txtCadEmail);

                int error = 0;

                if(txtNome.getText().toString().equals("")){
                    txtNome.setError("Preencha o campo nome.");
                    error = 1;
                }  if(txtSenha.getText().toString().equals("")){
                    txtSenha.setError("Preencha o campo senha");
                    error = 1;
                }  if(txtSenha2.getText().toString().equals("")){
                    txtSenha2.setError("Confirmação de senha vazia.");
                    error = 1;
                }  if(txtEmail.getText().toString().equals("")){
                    txtEmail.setError("Preencha o campo E-mail.");
                    error = 1;
                } if (!txtSenha.getText().toString().equals(txtSenha2.getText().toString())){
                    txtEmail.setError("Confirmação de senha difere da senha");
                    error = 1;
                }

                if (error==0){

                String URL = "https://192.168.25.5/chat/insert_user.php";
                Ion.with(getBaseContext())
                            .load(URL)
                            .setBodyParameter("nome_user",txtNome.getText().toString())
                            .setBodyParameter("senha_user",txtSenha.getText().toString())
                            .setBodyParameter("email_user",txtEmail.getText().toString())
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if (result.get("retorno").getAsString().equals("YES")) {
                                        Toast.makeText(getBaseContext(), "Cadastro realizado com sucesso", Toast.LENGTH_SHORT);
                                    } else if(result.get("retorno").getAsString().equals("EMAIL_ERROR")){
                                        //Toast.makeText(getBaseContext(), "Email já existe", Toast.LENGTH_SHORT);
                                        txtEmail.setError("Email existente");
                                    }
                                }
                            });


            }}
        });
    }


}
