package com.kokadwarakshay.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText currentReadingEditText, previousReadingEditText, motorUnitEditText, motorAmountEditText, rentEditText, cleaningChargesEditText, previousBalanceEditText;
    private TextView electricityBillTextView, totalAmountTextView;
    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize the UI elements
        currentReadingEditText = findViewById(R.id.currentReadingEditText);
        previousReadingEditText = findViewById(R.id.previousReadingEditText);
        motorUnitEditText = findViewById(R.id.motorUnitEditText);
        motorAmountEditText = findViewById(R.id.motorAmountEditText);
        rentEditText = findViewById(R.id.rentEditText);
        cleaningChargesEditText = findViewById(R.id.cleaningChargesEditText);
        previousBalanceEditText = findViewById(R.id.previousBalanceEditText);
        electricityBillTextView = findViewById(R.id.electricityBillTextView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);

        // Add a button click listener to calculate the bill
        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input values
                double currentReading = Double.parseDouble(currentReadingEditText.getText().toString());
                double previousReading = Double.parseDouble(previousReadingEditText.getText().toString());
                double motorUnit = Double.parseDouble(motorUnitEditText.getText().toString());
                double motorAmount = Double.parseDouble(motorAmountEditText.getText().toString());
                double rent = Double.parseDouble(rentEditText.getText().toString());
                double cleaningCharges = Double.parseDouble(cleaningChargesEditText.getText().toString());
                double previousBalance = Double.parseDouble(previousBalanceEditText.getText().toString());

                // Calculate the electricity bill
                double electricityUnit = currentReading - previousReading;
                double electricityBill = electricityUnit * 12;
                double totalElectricityBill = electricityBill;

                // Add motor bill, if applicable
                if (motorAmount > 0) {
                    totalElectricityBill += motorAmount;
                }
                if (motorUnit > 0) {
                    double motorBill = motorUnit * 12;
                    totalElectricityBill += motorBill;
                }

                // Calculate the total amount
                double totalAmount = totalElectricityBill + rent + cleaningCharges + previousBalance;

                // Display the electricity bill and total amount
                electricityBillTextView.setText(String.format(Locale.getDefault(), "%.2f", totalElectricityBill));
                electricityBillTextView.setTextSize(15);
                totalAmountTextView.setText(String.format(Locale.getDefault(), "%.2f", totalAmount));
                totalAmountTextView.setTextSize(30);
            }
        });

        // Add a button click listener to share the bill
        Button shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a bitmap of the scrolling view
                View v1 = getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                // Save the bitmap as a file
                String filename = "bill.png";
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File file = new File(directory, filename);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Create a share intent
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                intent.putExtra(Intent.EXTRA_STREAM, uri);

                // Check if there is an app available to handle the intent
                PackageManager pm = getPackageManager();
                List<ResolveInfo> resInfos = pm.queryIntentActivities(intent, 0);
                if (resInfos.size() > 0) {
                    // Launch the share intent
                    Intent chooserIntent = Intent.createChooser(intent, "Share bill via");
                    startActivity(chooserIntent);
                } else {
                    // Show an error message
                    Toast.makeText(MainActivity.this, "No app available to handle the share intent", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}