package com.aarsh.app.studysyncv1;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebViewActivity extends AppCompatActivity {

    private PDFView pdfView;
    private String pdfUrl;

    ImageView down;
    static ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        down = findViewById(R.id.button);
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progress);
        Intent intent = getIntent();
        if (intent != null) {
            pdfUrl = intent.getStringExtra("link");
        }

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomAlertDialog();
            }
        });

        // Download and load PDF in the background
        new DownloadPdfTask(pdfView).execute(pdfUrl);
    }

    private void showCustomAlertDialog() {
        String defaultFileName = "StudySync";

        // Inflate the custom layout
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_layout, null);

        // Get references to the views in the custom layout
        EditText input = customLayout.findViewById(R.id.editTextFileName);
        TextView buttonYes = customLayout.findViewById(R.id.buttonYes);
        TextView buttonNo = customLayout.findViewById(R.id.buttonNo);

        // Create an AlertDialog using the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        // Set onClickListener for Yes button
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customFileName = input.getText().toString().trim();
                downloadPdf(pdfUrl, customFileName);
                dialog.dismiss();
            }
        });

        // Set onClickListener for No button
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void downloadPdf(String pdfUrl, String customFileName) {
        String defaultFileName = "StudySync";
        // If the user entered a custom file name, use it; otherwise, use the default file name
        String fileName = TextUtils.isEmpty(customFileName) ? defaultFileName : customFileName;

        // Create a DownloadManager request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setTitle(fileName);
        request.setDescription("Downloading...");

        // Set the destination in the Downloads folder
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Enqueue the download and get download ID
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        // Show a Toast notification
        Toast.makeText(WebViewActivity.this, "Download started", Toast.LENGTH_SHORT).show();
    }

    private static class DownloadPdfTask extends AsyncTask<String, Void, File> {

        private WeakReference<PDFView> pdfViewReference;

        public DownloadPdfTask(PDFView pdfView) {
            pdfViewReference = new WeakReference<>(pdfView);
        }

        @Override
        protected File doInBackground(String... strings) {
            String pdfUrl = strings[0];
            try {
                URL url = new URL(pdfUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                File outputFile = new File(pdfViewReference.get().getContext().getFilesDir(), "sample.pdf");
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                fileOutputStream.close();
                bufferedInputStream.close();

                return outputFile;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File pdfFile) {
            PDFView pdfView = pdfViewReference.get();
            if (pdfFile != null && pdfView != null) {
                // Load the downloaded PDF file into PDFView
                pdfView.fromFile(pdfFile)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .load();
                progressBar.setVisibility(View.GONE);
            } else {
                // Handle the case where the PDF file download failed
                Toast.makeText(pdfView.getContext(), "Check Network Connection", Toast.LENGTH_LONG).show();
            }
        }
    }
}
