package combodedo.prueba;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class Inicio extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inicio);
    }

    public void ubicacion(View v){
        Intent i = new Intent(this, ubicacion.class);
        startActivity(i);
        finish();
    }


}
