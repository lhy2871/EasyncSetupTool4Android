package com.hanyuan6.easyncsetuptool;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hanyuan6.easyncsetuptool.core.Consts;
import com.hanyuan6.easyncsetuptool.core.events.LogMessageEvent;
import com.hanyuan6.easyncsetuptool.core.events.PrepareDevicesListEvent;
import com.hanyuan6.easyncsetuptool.core.events.SelectDeviceEvent;
import com.hanyuan6.easyncsetuptool.core.events.ShowDevicesListEvent;
import com.hanyuan6.easyncsetuptool.core.events.USBDataReceiveEvent;
import com.hanyuan6.easyncsetuptool.core.events.USBDataSendEvent;
import com.hanyuan6.easyncsetuptool.core.services.USBHIDService;

import java.util.Calendar;

import de.greenrobot.event.EventBus;
// Removed EventBusException as it's no longer used



public class USBHIDTerminal extends AppCompatActivity implements View.OnClickListener {

	private SharedPreferences sharedPreferences;

	private Intent usbService;

	//private String currentTime;
	private String currentTimeHex;
	private String fps2397;
	private String fps24;
	private String fps25;
	private String fps = "帧速率未知";
	private EditText edtlogText;
	private EditText powerlog;
	private TextView timeView;
	//private EditText editTextTip;
	//private EditText edtxtHidInput;
	//private Button btnSend;
	private Button btnSelectHIDDevice;
	//private Button btnClear;
	private Button button_RTC;
	private Button button_f0;
	private Button button_2397;
	private Button button_24;
	private Button button_25;
	//private RadioButton rbSendText;
	//private RadioButton rbSendDataType;
	private String settingsDelimiter;

	private String receiveDataFormat;
	private String delimiter;

	protected EventBus eventBus;

	private final ActivityResultLauncher<Intent> settingsLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			});

	private void prepareServices() {
		usbService = new Intent(this, USBHIDService.class);
		startService(usbService);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		eventBus = EventBus.getDefault();
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		initUI();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
			}
		}
	}

	private void initUI() {
		setVersionToTitle();
		//btnSend = (Button) findViewById(R.id.btnSend);
		//btnSend.setOnClickListener(this);

		btnSelectHIDDevice = (Button) findViewById(R.id.btnSelectHIDDevice);
		btnSelectHIDDevice.setOnClickListener(this);

		//btnClear = (Button) findViewById(R.id.btnClear);
		//btnClear.setOnClickListener(this);

		button_2397 = (Button) findViewById(R.id.button_2397);
		button_2397.setOnClickListener(this);
		button_24 = (Button) findViewById(R.id.button_24);
		button_24.setOnClickListener(this);
		button_25 = (Button) findViewById(R.id.button_25);
		button_25.setOnClickListener(this);
		button_RTC = (Button) findViewById(R.id.button_RTC);
		button_RTC.setOnClickListener(this);
		button_f0 = (Button) findViewById(R.id.button_f0);
		button_f0.setOnClickListener(this);

		//edtxtHidInput = (EditText) findViewById(R.id.edtxtHidInput);
		edtlogText = (EditText) findViewById(R.id.edtlogText);
		powerlog = (EditText) findViewById(R.id.powerlog);
		timeView = (TextView) findViewById(R.id.timeView);
		//editTextTip = (EditText) findViewById(R.id.editTextTip);

		//rbSendDataType = (RadioButton) findViewById(R.id.rbSendData);
		//rbSendText = (RadioButton) findViewById(R.id.rbSendText);
		//rbSendDataType.setOnClickListener(this);
		//rbSendText.setOnClickListener(this);

		Calendar c = Calendar.getInstance();
		//currentTime = Integer.toString(c.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(c.get(Calendar.MINUTE)) + ":" + Integer.toString(c.get(Calendar.SECOND));
		currentTimeHex = "0x" + Integer.toHexString(c.get(Calendar.HOUR_OF_DAY)) + " 0x" + Integer.toHexString(c.get(Calendar.MINUTE)) + " 0x" + Integer.toHexString(c.get(Calendar.SECOND));
		/*showTip("请选择设备\n十六进制计数--12点对应0x0c\n0xfe 0xdc 0x03 0x00 [小时] 0x00 [分钟] 0x00 [秒钟]\n现在时间: " + currentTime + "\n应给指令：\n0xfe 0xdc 0x03 " + currentTimeHex, false);
		mLog("",false);*/
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		//currentTime = "0x00 0x00 0x00";
		//edtxtHidInput.setText("0xfe 0xdc 0x03 " + currentTimeHex);
		// btnSend.setEnabled(true);
	}

	public void onClick(View v) {
		if /*(v == btnSend) {
			eventBus.post(new USBDataSendEvent(edtxtHidInput.getText().toString()));
		} else if*/(v == button_2397) {
			sendToUSBService(Consts.ACTION_USB_DATA_TYPE, true);
			fps2397 = "0xfe 0xdc 0x02 0x00";
			eventBus.post(new USBDataSendEvent(fps2397));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			eventBus.post(new USBDataSendEvent(fps2397));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));

		} else if (v == button_24) {
			sendToUSBService(Consts.ACTION_USB_DATA_TYPE, true);
			fps24 = "0xfe 0xdc 0x02 0x00";
			eventBus.post(new USBDataSendEvent(fps24));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			eventBus.post(new USBDataSendEvent(fps24));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));
		} else if (v == button_25){
			sendToUSBService(Consts.ACTION_USB_DATA_TYPE, true);
			fps25 = "0xfe 0xdc 0x02 0x01";
			eventBus.post(new USBDataSendEvent(fps25));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			eventBus.post(new USBDataSendEvent(fps25));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));

		//} else if (v == rbSendText || v == rbSendDataType) {
		/*} else if (v == rbSendDataType) {
			sendToUSBService(Consts.ACTION_USB_DATA_TYPE, rbSendDataType.isChecked());
		} else if (v == btnClear) {
			edtlogText.setText(""); */
		} else if (v == btnSelectHIDDevice) {
			eventBus.post(new PrepareDevicesListEvent());
		} else if (v == button_RTC) {
			sendToUSBService(Consts.ACTION_USB_DATA_TYPE, true);
			Calendar c = Calendar.getInstance();
			currentTimeHex = "0x" + Integer.toHexString(c.get(Calendar.HOUR_OF_DAY)) + " 0x" + Integer.toHexString(c.get(Calendar.MINUTE)) + " 0x" + Integer.toHexString(c.get(Calendar.SECOND));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x03 " + currentTimeHex));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x03 " + currentTimeHex));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));
		} else if (v == button_f0) {
			sendToUSBService(Consts.ACTION_USB_DATA_TYPE, true);
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x03 0x00 0x00 0x00"));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x03 0x00 0x00 0x00"));
			eventBus.post(new USBDataSendEvent("0xfe 0xdc 0x01"));
		}

	}

	void showListOfDevices(CharSequence devicesName[]) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		if (devicesName.length == 0) {
			builder.setTitle(Consts.MESSAGE_CONNECT_YOUR_USB_HID_DEVICE);
		} else {
			builder.setTitle(Consts.MESSAGE_SELECT_YOUR_USB_HID_DEVICE);
		}

		builder.setItems(devicesName, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mLog(String.valueOf(which),true);
				eventBus.post(new SelectDeviceEvent(which));
			}
		});
		builder.setCancelable(true);
		builder.show();
	}

	public void onEvent(USBDataReceiveEvent event) {
		String e = event.getData();
		if (e == null || e.trim().isEmpty()) {
			return;
		}

		String[] parts = e.trim().split("\\s+");
		if (parts.length < 4) {
			return;
		}

		try {
			int type = Integer.parseInt(parts[3]); // Assuming Byte 4 is the type identifier
			if (type == 132) {
				if (parts.length >= 6) {
					int p1 = Integer.parseInt(parts[5]);
					int p2 = Integer.parseInt(parts[6]);
					int powerpercent = (p1 * 256 + p2) * 825 / 512;
					if (powerpercent > 4200) { powerlog("100 %", true); }
					else if (powerpercent < 3600) { powerlog("0 %", true); }
					else {
						powerpercent = (powerpercent - 3600) / 6;
						powerlog(powerpercent + " %", true);
					}
				}
			} else if (type == 129) {
				if (parts.length > 47) {
					int fpsVal = Integer.parseInt(parts[47]);
					if (fpsVal == 0) { fps = "24"; }
					else if (fpsVal == 1) { fps = "25"; }
				}
			} else {
				// Original logic was e = e.substring(16, 27);
				// Let's assume this was bytes 5, 6, 7, 8
				if (parts.length >= 9) {
					String timeStr = parts[5] + ": " + parts[6] + ": " + parts[7] + ": " + parts[8] + " @ " + fps + " fps";
					mLog(timeStr, true);
				}
			}
		} catch (Exception ex) {
			// Ignore malformed packets
		}
	}

	public void onEvent(LogMessageEvent event) {
		mLog(event.getData(), true);
	}

	public void onEvent(ShowDevicesListEvent event) {
		showListOfDevices(event.getCharSequenceArray());
	}

	@Override
	protected void onStart() {
		super.onStart();
		receiveDataFormat = sharedPreferences.getString(Consts.RECEIVE_DATA_FORMAT, Consts.INTEGER);
		prepareServices();
		setDelimiter();
		eventBus.register(this);
	}

	@Override
	protected void onStop() {
		eventBus.unregister(this);
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		setSelectedMenuItemsFromSettings(menu);
		return true;
	}

	private void setSelectedMenuItemsFromSettings(Menu menu) {
		receiveDataFormat = sharedPreferences.getString(Consts.RECEIVE_DATA_FORMAT, Consts.INTEGER);
		if (receiveDataFormat != null) {
			if (receiveDataFormat.equals(Consts.BINARY)) {
				menu.findItem(R.id.menuSettingsReceiveBinary).setChecked(true);
			} else if (receiveDataFormat.equals(Consts.INTEGER)) {
				menu.findItem(R.id.menuSettingsReceiveInteger).setChecked(true);
			} else if (receiveDataFormat.equals(Consts.HEXADECIMAL)) {
				menu.findItem(R.id.menuSettingsReceiveHexadecimal).setChecked(true);
			} else if (receiveDataFormat.equals(Consts.TEXT)) {
				menu.findItem(R.id.menuSettingsReceiveText).setChecked(true);
			}
		}

		setDelimiter();
		if (settingsDelimiter.equals(Consts.DELIMITER_NONE)) {
			menu.findItem(R.id.menuSettingsDelimiterNone).setChecked(true);
		} else if (settingsDelimiter.equals(Consts.DELIMITER_NEW_LINE)) {
			menu.findItem(R.id.menuSettingsDelimiterNewLine).setChecked(true);
		} else if (settingsDelimiter.equals(Consts.DELIMITER_SPACE)) {
			menu.findItem(R.id.menuSettingsDelimiterSpace).setChecked(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		item.setChecked(true);
		int itemId = item.getItemId();
		if (itemId == R.id.menuSettings) {
			Intent i = new Intent(this, SettingsActivity.class);
			settingsLauncher.launch(i);
		} else if (itemId == R.id.menuSettingsReceiveBinary) {
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.BINARY).apply();
		} else if (itemId == R.id.menuSettingsReceiveInteger) {
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.INTEGER).apply();
		} else if (itemId == R.id.menuSettingsReceiveHexadecimal) {
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.HEXADECIMAL).apply();
		} else if (itemId == R.id.menuSettingsReceiveText) {
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.TEXT).apply();
		} else if (itemId == R.id.menuSettingsDelimiterNone) {
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_NONE).apply();
		} else if (itemId == R.id.menuSettingsDelimiterNewLine) {
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_NEW_LINE).apply();
		} else if (itemId == R.id.menuSettingsDelimiterSpace) {
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_SPACE).apply();
		}

		receiveDataFormat = sharedPreferences.getString(Consts.RECEIVE_DATA_FORMAT, Consts.INTEGER);
		setDelimiter();
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		switch (action) {
			case Consts.USB_HID_TERMINAL_CLOSE_ACTION:
				stopService(new Intent(this, USBHIDService.class));
				((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Consts.USB_HID_TERMINAL_NOTIFICATION);
				finish();
				break;
		}
	}

	private void setDelimiter() {
		settingsDelimiter = sharedPreferences.getString(Consts.DELIMITER, Consts.DELIMITER_SPACE);
		if (settingsDelimiter != null) {
			if (settingsDelimiter.equals(Consts.DELIMITER_NONE)) {
				delimiter = "";
			} else if (settingsDelimiter.equals(Consts.DELIMITER_NEW_LINE)) {
				delimiter = Consts.NEW_LINE;
			} else if (settingsDelimiter.equals(Consts.DELIMITER_SPACE)) {
				delimiter = Consts.SPACE;
			}
		}
		if (usbService != null) {
			usbService.setAction(Consts.RECEIVE_DATA_FORMAT);
			usbService.putExtra(Consts.RECEIVE_DATA_FORMAT, receiveDataFormat);
			usbService.putExtra(Consts.DELIMITER, delimiter);
			startService(usbService);
		}
	}

	void sendToUSBService(String action) {
		if (usbService != null) {
			usbService.setAction(action);
			startService(usbService);
		}
	}

	void sendToUSBService(String action, boolean data) {
		usbService.putExtra(action, data);
		sendToUSBService(action);
	}

	void sendToUSBService(String action, int data) {
		usbService.putExtra(action, data);
		sendToUSBService(action);
	}

	private void mLog(String log, boolean newLine) {
		edtlogText.setText(log);//单行显示
		//timeView.setText(log);//单行显示
	}

	private void powerlog(String log, boolean newLine) {
		powerlog.setText(log);//单行显示
	}

	/*private void showTip(String log, boolean newLine) {
		if (newLine) {
			editTextTip.append(Consts.NEW_LINE);
		}
		editTextTip.append(log);
		if(editTextTip.getLineCount()>15) {
			editTextTip.setText("");
		}
	}*/

	private void setVersionToTitle() {
		try {
			this.setTitle(Consts.SPACE + this.getTitle() + Consts.SPACE + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}