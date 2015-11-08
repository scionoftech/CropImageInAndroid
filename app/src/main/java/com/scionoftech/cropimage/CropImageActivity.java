package com.scionoftech.cropimage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CropImageActivity extends AppCompatActivity {
    ImageView preview;
    public static int CHOICE_AVATAR_FROM_CAMERA = 1;
    public static int CHOICE_AVATAR_FROM_CAMERA_CROP = 3;
    public static int CHOICE_AVATAR_FROM_GALLERY = 2;
    Uri cameraFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        preview = (ImageView) findViewById(R.id.preview);

        Button select = (Button) findViewById(R.id.select);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


    }

    //options to select image from gallery or take photo from camera
    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Avatar");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    AvatarFromCamera();

                } else if (options[item].equals("Choose from Gallery")) {
                    AvatarFromGallery();

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //get image from camera
    public void AvatarFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraFileName = getOutputMediaFileUri();
                /* put uri as extra in intent object */
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileName);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CHOICE_AVATAR_FROM_CAMERA_CROP);
    }


    //take image from gallery
    public void AvatarFromGallery() {
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(getCropIntent(choosePictureIntent), CHOICE_AVATAR_FROM_GALLERY);
    }


    //crop image
    private Intent getCropIntent(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        return intent;
    }


    //get cropped image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOICE_AVATAR_FROM_CAMERA || requestCode == CHOICE_AVATAR_FROM_GALLERY) {

                Bitmap avatar = getBitmapFromData(data);

                preview.setVisibility(View.VISIBLE);
                preview.setImageBitmap(avatar);

                // this bitmap is the finish image
            } else if (requestCode == CHOICE_AVATAR_FROM_CAMERA_CROP) {
                Intent intent = new Intent("com.android.camera.action.CROP");

                intent.setDataAndType(cameraFileName, "image/*");
                startActivityForResult(getCropIntent(intent), CHOICE_AVATAR_FROM_CAMERA);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //get bitmap from intent data
    public static Bitmap getBitmapFromData(Intent data) {
        Bitmap photo = null;
        Uri photoUri = data.getData();
        if (photoUri != null) {
            photo = BitmapFactory.decodeFile(photoUri.getPath());
        }
        if (photo == null) {
            Bundle extra = data.getExtras();
            if (extra != null) {
                photo = (Bitmap) extra.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
        }


        return photo;
    }

    //get file uri
    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    //save image to storage
    private static File getOutputMediaFile() {

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "IMG_" + timeStamp + ".jpg");

        return file;
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Are you sure you want to Exit");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sv();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                finish();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
