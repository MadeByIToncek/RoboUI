
/******************************************************************************
 *                    Copyright (c) 2024.                                     *
 *  Filmovy tabor Centrumdeti.cz & Roboticky tabor Centrumdeti.cz             *
 ******************************************************************************/

package cz.centrumdeti.filmovytabor.robosoutez.android;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicReference;

import cz.centrumdeti.filmovytabor.robosoutez.commons.types.Team;
import de.markusfisch.android.barcodescannerview.widget.BarcodeScannerView;
import de.markusfisch.android.zxingcpp.ZxingCpp;

public class MainActivity extends AppCompatActivity {
	private static final int REQUEST_CAMERA = 1;
	int sideswitch = -1;
	Team left = null;
	Team right = null;
	private Vibrator v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		checkPermissions();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		setupButtons();
		setupScanner();
	}

	private void setupButtons() {
		findViewById(R.id.leftSTButton).setOnClickListener(c -> {
			sideswitch = 0;
			((TextView) findViewById(R.id.leftTeamText)).setTextColor(getResources().getColor(R.color.teamload_selected, getTheme()));
			if (right == null) {
				((TextView) findViewById(R.id.rightTeamText)).setTextColor(getResources().getColor(R.color.teamload_empty, getTheme()));
			}
		});

		findViewById(R.id.rightSTButton).setOnClickListener(c -> {
			sideswitch = 1;
			((TextView) findViewById(R.id.rightTeamText)).setTextColor(getResources().getColor(R.color.teamload_selected, getTheme()));
			if (right == null) {
				((TextView) findViewById(R.id.leftTeamText)).setTextColor(getResources().getColor(R.color.teamload_empty, getTheme()));
			}
		});
	}

	private void setupScanner() {
		BarcodeScannerView scannerView = findViewById(R.id.barcodeScannerView);
		scannerView.setCropRatio(.75f);
		AtomicReference<String> prevscan = new AtomicReference<>("");
		scannerView.setOnBarcodeListener(result -> {
			if (prevscan.get().equals(result.getText())) {
				return true;
			}
			v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
			if (sideswitch >= 0) {

			}
			prevscan.set(result.getText());
			return true;
		});
		scannerView.formats.clear();
		scannerView.formats.add(ZxingCpp.BarcodeFormat.DATA_MATRIX);
		scannerView.openAsync();
	}

	private void checkPermissions() {
		String permission = android.Manifest.permission.CAMERA;
		if (checkSelfPermission(permission) !=
				PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{permission}, REQUEST_CAMERA);
		}
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults) {
		if (requestCode == REQUEST_CAMERA &&
				grantResults.length > 0 &&
				grantResults[0] != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(this, "No camera permission",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}