package com.example.opensourceprinter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import magick.ColorspaceType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;


public class MainActivity extends AppCompatActivity {

    String filepath;
    TextView textView;
    byte[] bytes;
    File file;
    String filename;
    int pageCount;
    int currentPage;
    //String tempPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/temp").toString();
    String tempPath;
    String ip;
    int port;
    EditText ipBox, portBox;
    public static final String LAST_TEXT1 = "ipkey";
    public static final String LAST_TEXT2 = "portkey";


    
    //Known errors,that cannot be fixed by me. I will not fix them ever. Please no commits, pull requests or Issues.
    //If you can fix them yourself, please copy the project to you and make the changes you want.
    //If you want to use this commercially, do it. If you want to sell this app, do it. You can do whatever you want with this code.
    //You can modify it as you like. I would like to see other people to actually release this or a modified version to playstore or other app stores.
    //So people can use this.
    //Here is the list:
    //-Add more formats to print like JPG/PNG/TEXT. This software supports only PDF mono printing.
    //-Rename the project to something better, than TESTPROJECT.
    //-PCL files have a top and left white padding. This is an error with ImageMagick libary for converting PCL. I cannot fix that in any way.
    //-PCL files also have, if they are colored wrong colors and color mixing problem. Therefore I convert the PNG to mono PCL and this is send to printer.
    //-Only tested with Android 12 emulator and device.
    //-Code could be much cleaner.
    //-Printing should work only for DIN A4 with 300 DPI. Not tested on other printer,than my own. Please modify for your printer, if you need.
    //-Add checks for ip and port, otherwise, if any of them is empty, the app will crash.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Get the values back! The Start!
        ipBox = (EditText) findViewById(R.id.ipBox);
        portBox = (EditText) findViewById(R.id.portBox);

        final SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences pref2 = PreferenceManager.getDefaultSharedPreferences(this);

        ipBox.setText(pref1.getString(LAST_TEXT1, ""));
        portBox.setText(pref2.getString(LAST_TEXT2, ""));

        ipBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref1.edit().putString(LAST_TEXT1, s.toString()).commit();
            }
        });


        portBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence t, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence t, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable t) {
                pref2.edit().putString(LAST_TEXT2, t.toString()).commit();
            }
        });
        //Get the values back! The End!


        tempPath = this.getFilesDir() + "/temp";
        askForPermissionDialog();
        deleteFiles(tempPath);
        textView = (TextView) findViewById(R.id.txt_pathShow);
        getDocument();
        setItNow();
        thread.start();
    }


    //Update the UI text every second
    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                while (!thread.isInterrupted()) {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setItNow();
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };


    //The text, that will be shown on the textview. In this case it will be the uri of the files sent.
    private void setItNow() {
        askForPermissionDialog();
        ((TextView) findViewById(R.id.txt_pathShow)).setText(filepath);
    }


    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        getDocument();
    }


    //This is the intent we will get from other apps, that share their uri.
    private void getDocument() {
        askForPermissionDialog();
        deleteFiles(tempPath);
        Intent getIntent = getIntent();
        String getIntentAction = getIntent.getAction();
        String getIntentType = getIntent.getType();
        //if(getIntentAction.equals(Intent.ACTION_SEND)) {
        //if(getIntentType.startsWith("´image/")) {
        Uri imageUri = getIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            //imageView.setImageURI(imageUri);
            filepath = imageUri.getPath();
            //Toast.makeText(this, filepath, Toast.LENGTH_SHORT).show();
            textView.setText(filepath);
            bytes = getBytesFromUri(getApplicationContext(), imageUri);
            filename = filepath.substring(filepath.lastIndexOf("/") + 1);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    writeToFile(bytes);
                }
            });
        }
        //}
        //}
    }

    //Show Alert, that print is job sent to printer, so users does not accidentally clicks on print button again.
    public void printJobisSentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Print Job");
        builder.setMessage("Print Job sent to printer.");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Show Alert, that no file is selcted, so he should select a file to print.
    public void noFileSelectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No File Selected");
        builder.setMessage("Please select a file before printing.");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openFileDialog(View view) {
        //askForPermissionDialog();
        deleteFiles(tempPath);
        //Perhaps this might nor work.
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.addCategory(Intent.CATEGORY_OPENABLE);
        data.setType("*/*");
        //String[] mimeTypes = {"text/*", "image/*", "application/pdf"};
        //String[] mimeTypes = {"*/*"};
        String[] mimeTypes = {"application/pdf"};
        data.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        data = Intent.createChooser(data, "Choose a file");
        sActivityResultLauncher.launch(data);
    }


    byte[] getBytesFromUri(Context context, Uri uri) {
        InputStream iStream = null;
        try {
            iStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = iStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (Exception ex) {

        }
        return null;
    }

    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Perhaps this might nor work.
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        bytes = getBytesFromUri(getApplicationContext(), uri);
                        //textView.setText(new String(bytes));
                        filepath = uri.toString();
                        filename = filepath.substring(filepath.lastIndexOf("/") + 1);
                        textView.setText(filepath);

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                writeToFile(bytes);
                            }
                        });
                    }
                }
            }
    );


    private void writeToFile(byte[] fileData) {
        try {
            File root = new File(tempPath);
            File dir = new File(root + File.separator);
            if (!dir.exists()) dir.mkdir();

            file = new File(root + File.separator + filename);

            file.createNewFile();

            FileOutputStream out = new FileOutputStream(file);
            out.write(fileData);
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        File pdfFilePath = file;
        File DestinationFolder = new File(tempPath);
        try {
            getImagesFromPDF(pdfFilePath, DestinationFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showPDF();
    }


    public void showPDF() {
        // Capture file of recently saved image, convert to PCL
        // File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/temp/image0.png");
        for (currentPage = 0; currentPage < pageCount; currentPage++) {

            DecimalFormat formatter = new DecimalFormat("000");
            String aFormatted = formatter.format(currentPage);

            File root = new File(tempPath + "/image" + aFormatted + ".png");

            try {
                ImageInfo originalInfo = new ImageInfo(root.toString());
                MagickImage mImage = new MagickImage(originalInfo);

//           String newInfoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/test.pcl").toString();
//           mImage.setFileName(newInfoPath);
//           mImage.setImageFormat("pcl");

                String newInfoPath = new File(tempPath + "/temp" + aFormatted + ".pcl").toString();
                mImage.setFileName(newInfoPath);
                mImage.rgbTransformImage((ColorspaceType.GRAYColorspace));
                mImage.setImageFormat("pcl");
                ImageInfo newInfo = new ImageInfo(newInfoPath);
                newInfo.setDensity("300");
                newInfo.setPage("A4");

                mImage.writeImage(newInfo);
            } catch (MagickException exception) {
                // Exception found
            }
        }
    }



    private static void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
            }
        }
    }


    public AlertDialog myAlertDialog;
    public void askForPermissionDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                if (myAlertDialog != null && myAlertDialog.isShowing()) return;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissions needed");
                builder.setMessage("Please allow for file access permissions, otherwise this app will not work.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                })

                 .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                askForPermissions();
                            }
                        });
                builder.setCancelable(false);
                myAlertDialog = builder.create();
                myAlertDialog.show();
            }}
    }

    public void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }
    }

    //Read the files as byte array and send them to the printer.
    public void PrintOnAndroid(View v) {
        Print();
    }

    public void Print() {
            //File pdfFile = file;
            //File pdfFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/temp/temp.pcl");

                if (filepath == null) {
                    noFileSelectedDialog();
                }

                else {
                    ip = ipBox.getText().toString();
                    port = Integer.valueOf(portBox.getText().toString());


                    for (currentPage = 0; currentPage < pageCount; currentPage++) {
                        DecimalFormat formatter = new DecimalFormat("000");
                        String aFormatted = formatter.format(currentPage);

                        File pdfFile = new File(tempPath + "/temp" + aFormatted + ".pcl");
                        Socket clientSocket = null;
                        FileInputStream fis;
                        BufferedInputStream bis = null;

                        try {
                            // clientSocket = new Socket("192.168.2.1", 9100);
                            clientSocket = new Socket(ip, port);
                        }
                        catch (UnknownHostException e) {
                            // e.printStackTrace();
                        }
                        catch (IOException e) {
                            //e.printStackTrace();
                        }

                        byte[] mybytearray = new byte[(int) pdfFile.length()];
                        try {
                            fis = new FileInputStream(pdfFile);
                            bis = new BufferedInputStream(fis);
                            bis.read(mybytearray, 0, mybytearray.length);
                            OutputStream os = clientSocket.getOutputStream();
                            os.write(mybytearray, 0, mybytearray.length);
                            os.flush();
                        } catch (FileNotFoundException e) {
                            //e.printStackTrace();
                        } catch (IOException e) {
                            //e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                                bis.close();
                            } catch (IOException e)
                            {
                                // e.printStackTrace();
                            }
                        }

                    }
                    printJobisSentDialog();
                    //Perhaps this might nor work.
                    deleteFiles(tempPath);
                }
}


    // This method is used to extract all pages in image (PNG) format.
    private void getImagesFromPDF(File pdfFilePath, File DestinationFolder) throws IOException {
        // Check if destination already exists then delete destination folder.
        if(DestinationFolder.exists()){
            DestinationFolder.delete();
        }
        // Create empty directory where images will be saved.
        DestinationFolder.mkdirs();
        // Reading pdf in READ Only mode.
        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFilePath, ParcelFileDescriptor.MODE_READ_ONLY);
        // Initializing PDFRenderer object.
        PdfRenderer renderer = new PdfRenderer(fileDescriptor);
        // Getting total pages count.

        //final int pageCount = renderer.getPageCount();
         pageCount = renderer.getPageCount();

        // Iterating pages
        for (int i = 0; i < pageCount; i++) {
            // Getting Page object by opening page.
            PdfRenderer.Page page = renderer.openPage(i);


            // Creating empty bitmap. Bitmap.Config can be changed.
            //Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),Bitmap.Config.ARGB_8888);

            // We could use 72 dpi too perhaps. Best settings so far are 65 dpi like commented below.
//            Bitmap bitmap = Bitmap.createBitmap(
//                    getResources().getDisplayMetrics().densityDpi * page.getWidth() / 65,
//                    getResources().getDisplayMetrics().densityDpi * page.getHeight() / 65,
//                    Bitmap.Config.ARGB_8888);


//          This will match only A4 size 300DPI.
            Bitmap bitmap = Bitmap.createBitmap(
                    2380,
                    2908,
                    Bitmap.Config.ARGB_8888);


            // Creating Canvas from bitmap.
            Canvas canvas = new Canvas(bitmap);
            // Set White background color.
            canvas.drawColor(Color.WHITE);
            // Draw bitmap.
            canvas.drawBitmap(bitmap, 0, 0, null);
            // Rednder bitmap and can change mode too.
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // closing page
            page.close();
            // saving image into sdcard.

            DecimalFormat formatter = new DecimalFormat("000");
            String aFormatted = formatter.format(i);

            File file = new File(DestinationFolder.getAbsolutePath(), "image"+  aFormatted + ".png");
            // check if file already exists, then delete it.
            if (file.exists()) file.delete();

            // Saving image in PNG format with 100% quality.
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Log.v("Saved Image - ", file.getAbsolutePath());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}