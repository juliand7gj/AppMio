package combodedo.prueba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class camara extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);
    }

    public void regreso(View v){
        Intent i = new Intent(this, home.class);
        startActivity(i);
        finish();
    }
}
