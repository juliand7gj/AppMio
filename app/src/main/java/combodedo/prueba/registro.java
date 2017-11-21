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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registro extends AppCompatActivity {

    private EditText nombre;
    private EditText contra;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        nombre = (EditText)findViewById(R.id.nombreUsuario);
        contra = (EditText)findViewById(R.id.contra);
    }

    public void registrar(View v){

        final String nom = nombre.getText().toString();
        final String con = contra.getText().toString();

        mAuth.createUserWithEmailAndPassword(nom, con)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Usuario u = new Usuario(nom, con);

                            DatabaseReference data = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference referencia = data.child("usuarios");
                            referencia.child(user.getUid()).setValue(u);


                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        }else {

                        }

                        // ...
                    }
                });

    }

}
