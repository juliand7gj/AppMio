package combodedo.prueba;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText nombre;
    private EditText contra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombre = (EditText)findViewById(R.id.nombreUsuario);
        contra = (EditText)findViewById(R.id.contra);
    }

    public void ingresar(View v){

        String nom = nombre.getText().toString();
        String con = contra.getText().toString();

        mAuth.signInWithEmailAndPassword(nom, con)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                        }else{
                            Intent i = new Intent(getApplicationContext(), home.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });


    }

    public void registrar(View v){
        Intent i = new Intent(this, registro.class);
        startActivity(i);
        finish();
    }

}
