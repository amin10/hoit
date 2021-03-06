/**************************************************************************************************
  Filename:       DeviceView.java
  Revised:        $Date: 2013-08-30 12:02:37 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27470 $
  Copyright (c) 2013 - 2014 Texas Instruments Incorporated
  All rights reserved not granted herein.
  Limited License. 
  Texas Instruments Incorporated grants a world-wide, royalty-free,
  non-exclusive license under copyrights and patents it now or hereafter
  owns or controls to make, have made, use, import, offer to sell and sell ("Utilize")
  this software subject to the terms herein.  With respect to the foregoing patent
  license, such license is granted  solely to the extent that any such patent is necessary
  to Utilize the software alone.  The patent license shall not apply to any combinations which
  include this software, other than combinations with devices manufactured by or for TI (�TI Devices�). 
  No hardware patent is licensed hereunder.
  Redistributions must preserve existing copyright notices and reproduce this license (including the
  above copyright notice and the disclaimer and (if applicable) source code license limitations below)
  in the documentation and/or other materials provided with the distribution
  Redistribution and use in binary form, without modification, are permitted provided that the following
  conditions are met:
 * No reverse engineering, decompilation, or disassembly of this software is permitted with respect to any
      software provided in binary form.
 * any redistribution and use are licensed by TI for use only with TI Devices.
 * Nothing shall obligate TI to provide you with source code for the software licensed and provided to you in object code.
  If software source code is provided to you, modification and redistribution of the source code are permitted
  provided that the following conditions are met:
 * any redistribution and use of the source code, including any resulting derivative works, are licensed by
      TI for use only with TI Devices.
 * any redistribution and use of any object code compiled from the source code and any resulting derivative
      works, are licensed by TI for use only with TI Devices.
  Neither the name of Texas Instruments Incorporated nor the names of its suppliers may be used to endorse or
  promote products derived from this software without specific prior written permission.
  DISCLAIMER.
  THIS SOFTWARE IS PROVIDED BY TI AND TI�S LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL TI AND TI�S LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.
 **************************************************************************************************/
package com.example.ti.ble.sensortag;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ti.util.Point3D;

// Fragment for Device View
public class DeviceView extends Fragment {

	// Sensor table; the iD corresponds to row number
	private static final int ID_OFFSET = 0;
	private static final int ID_KEY = 0;
	private static final int ID_ACC = 1;
	private static final int ID_MAG = 2;
	private static final int ID_OPT = 2;
	private static final int ID_GYR = 3;
	private static final int ID_OBJ = 4;
	private static final int ID_AMB = 5;
	private static final int ID_HUM = 6;
	private static final int ID_BAR = 7;

	public static DeviceView mInstance = null;

	// GUI
	// private TableLayout table;
	private String mAccValue;
	private String mMagValue;
	private String mLuxValue;
	private String mGyrValue;
	private String mObjValue;
	private String mAmbValue;
	private String mHumValue;
	private String mBarValue;
	// private ImageView mButton;
	// private ImageView mRelay;
	// private TableRow mMagPanel;
	// private TableRow mBarPanel;

	// House-keeping
	private DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");
	private DeviceActivity mActivity;
	private static final double PA_PER_METER = 12.0;
	private boolean mIsSensorTag2;
	private boolean mBusy;

	HashMap<Button, Integer> inputColorMap = new HashMap<>();
	HashMap<Button, Set<Button>> inputOutputMap = new HashMap<>();
	HashMap<Button, Uri> outputMap = new HashMap<>();
	HashMap<Button, Double> inputThresholds = new HashMap<>();
	Button testBtn;
	Button selectedInput = null;
	LinearLayout inputs, outputs;

	MediaPlayer mp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInstance = this;
		mActivity = (DeviceActivity) getActivity();
		mIsSensorTag2 = mActivity.isSensorTag2();

		// The last two arguments ensure LayoutParams are inflated properly.
		View view;

		if (mIsSensorTag2) {
			view = inflater.inflate(R.layout.services_browser2, container,
					false);
			// table = (TableLayout)
			// view.findViewById(R.id.services_browser_layout2);
			// mLuxValue = (TextView) view.findViewById(R.id.luxometerTxt);
			// mMagPanel = null;
			// mRelay = (ImageView) view.findViewById(R.id.relay);
		} else {
			view = inflater
					.inflate(R.layout.services_browser, container, false);
			// table = (TableLayout)
			// view.findViewById(R.id.services_browser_layout);
			// mMagValue = (TextView) view.findViewById(R.id.magnetometerTxt);
			// mMagPanel = (TableRow) view.findViewById(R.id.magPanel);
			// mRelay = null;
		}

		inputs = (LinearLayout) view.findViewById(R.id.inputs);
		outputs = (LinearLayout) view.findViewById(R.id.outputs);
		testBtn = (Button) view.findViewById(R.id.button);

		// UI widgets
		// mAccValue = (TextView) view.findViewById(R.id.accelerometerTxt);
		// mGyrValue = (TextView) view.findViewById(R.id.gyroscopeTxt);
		// mObjValue = (TextView) view.findViewById(R.id.objTemperatureText);
		// mAmbValue = (TextView) view.findViewById(R.id.ambientTemperatureTxt);
		// mHumValue = (TextView) view.findViewById(R.id.humidityTxt);
		// mBarValue = (TextView) view.findViewById(R.id.barometerTxt);
		// mButton = (ImageView) view.findViewById(R.id.buttons);

		// Support for calibration
		/*
		 * mBarPanel = (TableRow) view.findViewById(R.id.barPanel);
		 * OnClickListener cl = new OnClickListener() { public void onClick(View
		 * v) { switch (v.getId()) { case R.id.magPanel:
		 * mActivity.calibrateMagnetometer(); break; case R.id.barPanel:
		 * mActivity.calibrateHeight(); break; default: } } }; if (mMagPanel !=
		 * null) mMagPanel.setOnClickListener(cl);
		 * mBarPanel.setOnClickListener(cl);
		 */
		// Notify activity that UI has been inflated
		mActivity.onViewInflated(view);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateVisibility();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	List<Button> getInputs(String type) {
		ArrayList<Button> buttons = new ArrayList<>();
		for (Button b : inputThresholds.keySet()) {
			if (b.getText().toString().startsWith(type)) {
				buttons.add(b);
			}
		}
		return buttons;
	}

	List<Button> triggered = new ArrayList<Button>();

	void trigger(Button input) {
		System.out.println("test123 trigger " + input.getText().toString());
		if (triggered.contains(input))
			return;
		for (Button b : inputOutputMap.get(input)) {
			final Button output = b;
			executeOutputAction(output);
			triggered.add(output);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						wait(10000);
						triggered.remove(output);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
	}

	/**
	 * Handle changes in sensor values
	 * */
	public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {
		Point3D v;
		String msg;

		if (uuidStr.equals(SensorTagGatt.UUID_ACC_DATA.toString())) {
			v = Sensor.ACCELEROMETER.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
					+ decimal.format(v.z) + "\n";
			mAccValue = msg;

			for (Button input : getInputs("Accelerometer")) {
				if (inputThresholds.get(input) < Math.sqrt(v.x * v.x + v.y
						* v.y + v.z * v.z)) {
					trigger(input);
				}
			}
			System.out.println("test123 accelerometer: "
					+ Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z));
		}

		if (uuidStr.equals(SensorTagGatt.UUID_MAG_DATA.toString())) {
			v = Sensor.MAGNETOMETER.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
					+ decimal.format(v.z) + "\n";
			mMagValue = msg;
			for (Button input : getInputs("Magnetometer")) {
				if (inputThresholds.get(input) < Math.sqrt(v.x * v.x + v.y
						* v.y + v.z * v.z)) {
					trigger(input);
				}
			}

			System.out.println("test123 magnetometer:  "
					+ Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z));
		}

		if (uuidStr.equals(SensorTagGatt.UUID_OPT_DATA.toString())) {
			v = Sensor.LUXOMETER.convert(rawValue);
			msg = decimal.format(v.x) + "\n";
			mLuxValue = msg;

			for (Button input : getInputs("Ambient Light")) {
				if (inputThresholds.get(input) < v.x) {
					trigger(input);
				}
			}

			System.out.println("test123 luxometer:  " + v.x);
		}

		if (uuidStr.equals(SensorTagGatt.UUID_GYR_DATA.toString())) {
			v = Sensor.GYROSCOPE.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
					+ decimal.format(v.z) + "\n";
			mGyrValue = msg;

			for (Button input : getInputs("Gyroscope")) {
				if (inputThresholds.get(input) < Math.sqrt(v.x * v.x + v.y
						* v.y + v.z * v.z)) {
					trigger(input);
				}
			}

			System.out.println("test123 gyroscope:  "
					+ Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z));
		}

		if (uuidStr.equals(SensorTagGatt.UUID_IRT_DATA.toString())) {
			v = Sensor.IR_TEMPERATURE.convert(rawValue);
			msg = decimal.format(v.x) + "\n";
			mAmbValue = msg;
			msg = decimal.format(v.y) + "\n";
			mObjValue = msg;

			for (Button input : getInputs("Temperature")) {
				if (inputThresholds.get(input) < v.x) {
					trigger(input);
				}
			}

			System.out.println("test123 gyroscope:  " + v.x);
		}

		if (uuidStr.equals(SensorTagGatt.UUID_HUM_DATA.toString())) {
			v = Sensor.HUMIDITY.convert(rawValue);
			msg = decimal.format(v.x) + "\n";
			mHumValue = msg;
			for (Button input : getInputs("Humidity")) {
				if (inputThresholds.get(input) < v.x) {
					trigger(input);
				}
			}
			System.out.println("test123 humidity:  " + v.x);
		}

		if (uuidStr.equals(SensorTagGatt.UUID_BAR_DATA.toString())) {
			v = Sensor.BAROMETER.convert(rawValue);

			double h = (v.x - BarometerCalibrationCoefficients.INSTANCE.heightCalibration)
					/ PA_PER_METER;
			h = (double) Math.round(-h * 10.0) / 10.0;
			msg = decimal.format(v.x / 100.0f) + "\n" + h;
			mBarValue = msg;

			for (Button input : getInputs("Barometer")) {
				if (inputThresholds.get(input) < h) {
					trigger(input);
				}
			}
			System.out.println("test123 barometer:  " + v.x);
		}

		if (uuidStr.equals(SensorTagGatt.UUID_KEY_DATA.toString())) {
			int keys = rawValue[0];
			SimpleKeysStatus s;
			final int imgBtn;
			s = Sensor.SIMPLE_KEYS.convertKeys((byte) (keys & 3));

			switch (s) {
			case OFF_ON:
				imgBtn = R.drawable.buttonsoffon;
				setBusy(true);
				break;
			case ON_OFF:
				imgBtn = R.drawable.buttonsonoff;
				setBusy(true);
				break;
			case ON_ON:
				imgBtn = R.drawable.buttonsonon;
				break;
			default:
				imgBtn = R.drawable.buttonsoffoff;
				setBusy(false);
				break;
			}

			// mButton.setImageResource(imgBtn);

			if (mIsSensorTag2) {
				// Only applicable for SensorTag2
				final int imgRelay;

				if ((keys & 4) == 4) {
					imgRelay = R.drawable.reed_open;
				} else {
					imgRelay = R.drawable.reed_closed;
				}
				// mRelay.setImageResource(imgRelay);
			}
		}
	}

	void updateVisibility() {
		showItem(ID_KEY, mActivity.isEnabledByPrefs(Sensor.SIMPLE_KEYS));
		showItem(ID_ACC, mActivity.isEnabledByPrefs(Sensor.ACCELEROMETER));
		if (mIsSensorTag2)
			showItem(ID_OPT, mActivity.isEnabledByPrefs(Sensor.LUXOMETER));
		else
			showItem(ID_MAG, mActivity.isEnabledByPrefs(Sensor.MAGNETOMETER));
		showItem(ID_GYR, mActivity.isEnabledByPrefs(Sensor.GYROSCOPE));
		showItem(ID_OBJ, mActivity.isEnabledByPrefs(Sensor.IR_TEMPERATURE));
		showItem(ID_AMB, mActivity.isEnabledByPrefs(Sensor.IR_TEMPERATURE));
		showItem(ID_HUM, mActivity.isEnabledByPrefs(Sensor.HUMIDITY));
		showItem(ID_BAR, mActivity.isEnabledByPrefs(Sensor.BAROMETER));
	}

	private void showItem(int id, boolean visible) {
		// View hdr = table.getChildAt(id * 2 + ID_OFFSET);
		// View txt = table.getChildAt(id * 2 + ID_OFFSET + 1);
		// int vc = visible ? View.VISIBLE : View.GONE;
		// hdr.setVisibility(vc);
		// txt.setVisibility(vc);
	}

	void setBusy(boolean f) {
		if (f != mBusy) {
			mActivity.showBusyIndicator(f);
			mBusy = f;
		}
	}

	public boolean newInput() {
		final CharSequence[] items = { "Accelerometer", "Temperature",
				"Ambient Light", "Humidity", "Barometer", "Gyroscope",
				"Compass", "Magnetometer" };
		AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

		builder1.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				try {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							getActivity());
					alertDialog.setTitle(items[i]);
					alertDialog.setMessage("Trigger threshold:");

					final EditText input = new EditText(getActivity());
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					input.setLayoutParams(lp);
					alertDialog.setView(input);

					alertDialog.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {

									if (input.getText() == null
											|| input.getText().toString()
													.isEmpty())
										return;
									Double threshold;
									try {
										threshold = Double.parseDouble(input
												.getText().toString());
									} catch (Exception e) {
										return;
									}

									final Button b = new Button(getActivity());
									b.setMaxEms(10);
									b.setText(items[i]);
									Random rnd = new Random();
									int color = Color.argb(255,
											rnd.nextInt(16) * 16,
											rnd.nextInt(16) * 16,
											rnd.nextInt(16) * 16);
									while (inputColorMap.containsValue(color)) {
										color = Color.argb(255,
												rnd.nextInt(16) * 16,
												rnd.nextInt(16) * 16,
												rnd.nextInt(16) * 16);
									}
									b.setBackgroundColor(color);
									inputColorMap.put(b, color);

									b.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View view) {
											if (selectedInput == b) {
												b.setEnabled(true);
												selectedInput = null;
											} else {
												b.setEnabled(false);
												if (selectedInput != null) {
													selectedInput
															.setEnabled(true);
												}
												selectedInput = b;
											}
										}
									});

									inputs.addView(b);
									inputOutputMap
											.put(b, new HashSet<Button>());

									inputThresholds.put(b, threshold);
								}
							});

					AlertDialog alert12 = alertDialog.create();
					alert12.show();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();

		return true;
	}

	public void pickMusic(Intent data) {
		Uri uri = data.getData();
		final Button b = new Button(getActivity());
		b.setMaxEms(10);
		b.setText("SPEAKER: " + uri.toString());
		outputMap.put(b, uri);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (selectedInput != null) {
					selectedInput.setEnabled(true);
					b.setBackgroundColor(inputColorMap.get(selectedInput));
					inputOutputMap.get(selectedInput).add(b);
					selectedInput = null;
				} else {
					executeOutputAction(b);
				}
			}
		});

		outputs.addView(b);
	}

	private void executeOutputActionsOfInput(Button inputButton) {
		if (!inputOutputMap.containsKey(inputButton))
			return;
		for (Button outputButton : inputOutputMap.get(inputButton)) {
			executeOutputAction(outputButton);
		}
	}

	private void executeOutputAction(Button outputButton) {
		mp = MediaPlayer.create(getActivity(), outputMap.get(outputButton));
		mp.start();
	}

	public void testing(View v) {
		// Toast.makeText(v.getContext(), DeviceView.hello(),
		// Toast.LENGTH_SHORT).show();
		// Data read
		// String uuidStr =
		// intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
		// byte[] value =
		// intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
		// onCharacteristicsRead(SensorTagGatt.UUID_IRT_DATA.toString(), value,
		// BluetoothGatt.GATT_SUCCESS);
	}
}