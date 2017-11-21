package combodedo.prueba;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final TextView text = (TextView) findViewById(R.id.text1);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            DatabaseReference data = FirebaseDatabase.getInstance().getReference();
            DatabaseReference referencia = data.child("usuarios");


            referencia.orderByChild("nombre").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                        Usuario u = postSnapshot.getValue(Usuario.class);
                        text.setText(u.getNombre());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //subir
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        StorageReference imagesRef = storageRef.child("images");
//        StorageReference spaceRef = storageRef.child("images/space.jpg");
//        StorageReference filePath= storageRef.child("fotos").child("https://images.search.yahoo.com/images/view;_ylt=AwrB8qEoTfJZMi0ACf6JzbkF;_ylu=X3oDMTIyZGRpNm4yBHNlYwNzcgRzbGsDaW1nBG9pZAMxNWRmYTAyMTQwMzRiYTA0NjI4OThkZjVhMjdiN2UxMgRncG9zAzYEaXQDYmluZw--?.origin=&back=https%3A%2F%2Fimages.search.yahoo.com%2Fsearch%2Fimages%3Fp%3DPaul%2BMcCartney%26fr%3Dsfp-tts-img%26fr2%3Dpiv-web%26tab%3Dorganic%26ri%3D6&w=2000&h=1000&imgurl=i.huffpost.com%2Fgen%2F2994606%2Fimages%2Fo-PAUL-MCCARTNEY-facebook.jpg&rurl=http%3A%2F%2Fwww.huffingtonpost.com%2F2015%2F05%2F26%2Ffriends-paul-mccartney_n_7417348.html&size=123.0KB&name=%3Cb%3EPaul+McCartney%3C%2Fb%3E+Almost+Ended+Up+On+%26%2339%3BFriends%26%2339%3B+%7C+HuffPost&p=Paul+McCartney&oid=15dfa0214034ba0462898df5a27b7e12&fr2=piv-web&fr=sfp-tts-img&tt=%3Cb%3EPaul+McCartney%3C%2Fb%3E+Almost+Ended+Up+On+%26%2339%3BFriends%26%2339%3B+%7C+HuffPost&b=0&ni=21&no=6&ts=&tab=organic&sigr=12ej16fhd&sigb=13aomj17a&sigi=11va0o3hp&sigt=125pn9fa5&sign=125pn9fa5&.crumb=n0qFC0cxBXg&fr=sfp-tts-img&fr2=piv-web");

        //bajar


    }

    public void ubicacion(View v){
        Intent i = new Intent(this, ubicacion.class);
        startActivity(i);
        finish();
    }

    public void camara(View v){
        Intent i = new Intent(this, camara.class);
        startActivity(i);
        finish();
    }
}
