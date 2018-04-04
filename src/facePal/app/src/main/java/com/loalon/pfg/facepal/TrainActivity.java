package com.loalon.pfg.facepal;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class TrainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        Intent intent = getIntent();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        ///data/user/0/com.loalon.pfg.facepal/app_imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File imgFile=new File(directory,"tempFace.jpg");

        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        ImageView myImage = (ImageView) findViewById(R.id.imageview_train);

        myImage.setImageBitmap(myBitmap);



    }
}
