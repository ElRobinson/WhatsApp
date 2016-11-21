package com.robinson.luis.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.ImageViewBitmapInfo;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Cadastro extends AppCompatActivity {

    private static final int REQUEST_DIALOG_PHOTO = 1;
    private int havePhoto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btn = (Button)findViewById(R.id.btnCadCadastrar);
        final ImageView imgFoto = (ImageView) findViewById(R.id.imgCadFoto);

        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    imgFoto.setImageResource(android.R.color.transparent);
                    Intent camera = ImagePicker.getPickImageIntent(getBaseContext());
                    startActivityForResult(camera, REQUEST_DIALOG_PHOTO);
            }
        });

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
                    txtNome.requestFocus();
                    error = 1;
                }  if(txtSenha.getText().toString().equals("")){
                    txtSenha.setError("Preencha o campo senha");
                    txtSenha.requestFocus();
                    error = 1;
                }  if(txtSenha2.getText().toString().equals("")){
                    txtSenha2.setError("Confirmação de senha vazia.");
                    txtSenha2.requestFocus();
                    error = 1;
                }  if(txtEmail.getText().toString().equals("")){
                    txtEmail.setError("Preencha o campo E-mail.");
                    txtEmail.requestFocus();
                    error = 1;
                } if (!txtSenha.getText().toString().equals(txtSenha2.getText().toString())){
                    txtEmail.setError("Confirmação de senha difere da senha");
                    error = 1;
                } if (havePhoto == 0){
                    Toast.makeText(getBaseContext(),"Selecione uma foto",Toast.LENGTH_SHORT).show();
                    error = 1;
                }

                if (error==0){

                    String URL = "https://192.168.25.5/chat/insert_user.php";

                    String photoFile = "";
                    try {
                        photoFile = getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(),0).applicationInfo.dataDir + "//photo//perfil.png";
                    } catch (PackageManager.NameNotFoundException e) {

                    }

                    Ion.with(getBaseContext())
                            .load(URL)
                            .setMultipartParameter("nome_user",txtNome.getText().toString())
                            .setMultipartParameter("senha_user",txtSenha.getText().toString())
                            .setMultipartParameter("email_user",txtEmail.getText().toString())
                            .setMultipartFile("photo_user",new File(photoFile))
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

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (requestCode == REQUEST_DIALOG_PHOTO){
            if (requestCode == Activity.RESULT_OK){

                //retrieving photo
                Bitmap photoUser = ImagePicker.getImageFromResult(getBaseContext(),resultCode,data);
                ImageView imgFoto = (ImageView) findViewById(R.id.imgCadFoto);
                imgFoto.setImageBitmap(photoUser);
                havePhoto = 1;

                // write data directory
                File directory = Environment.getDataDirectory();
                String path = "//data//" + getBaseContext().getPackageName() + "//photo//";

                directory = new File(directory,path);
                directory.mkdirs();

                OutputStream out = null;

                File outputFile = new File(directory,"perfil.png");

                try {
                    out = new FileOutputStream(outputFile);
                    photoUser.compress(Bitmap.CompressFormat.PNG,100,out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getBaseContext(),"Selecione uma foto",Toast.LENGTH_SHORT).show();
            }

        }

    }


}
