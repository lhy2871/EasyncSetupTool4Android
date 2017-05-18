package com.appspot.usbhidterminal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.appspot.usbhidterminal.core.Consts;
import com.appspot.usbhidterminal.core.events.DeviceAttachedEvent;
import com.appspot.usbhidterminal.core.events.DeviceDetachedEvent;
import com.appspot.usbhidterminal.core.events.LogMessageEvent;
import com.appspot.usbhidterminal.core.events.PrepareDevicesListEvent;
import com.appspot.usbhidterminal.core.events.SelectDeviceEvent;
import com.appspot.usbhidterminal.core.events.ShowDevicesListEvent;
import com.appspot.usbhidterminal.core.events.USBDataReceiveEvent;
import com.appspot.usbhidterminal.core.events.USBDataSendEvent;
import com.appspot.usbhidterminal.core.services.USBHIDService;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.appspot.usbhidterminal.R.id.timeView;


public class USBHIDTerminal extends Activity implements View.OnClickListener {

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

	private void prepareServices() {
		usbService = new Intent(this, USBHIDService.class);
		startService(usbService);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			eventBus = EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).installDefaultEventBus();
		} catch (EventBusException e) {
			eventBus = EventBus.getDefault();
		}
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		initUI();
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
				eventBus.post(new SelectDeviceEvent(which));
			}
		});
		builder.setCancelable(true);
		builder.show();
	}

	public void onEvent(USBDataReceiveEvent event) {
		//mLog(event.getData() + " \n接收到 " + event.getBytesCount() + " 位", true);
		String e = event.getData();
		if (Integer.parseInt(e.substring(11,15)) == 132) {
			String[] tmp = null;
			tmp = e.substring(16,21).split(" ");
			int powerpercent = (Integer.parseInt(tmp[0]) * 256 + Integer.parseInt(tmp[1])) * 825 / 512;
			if (powerpercent > 4200) { powerlog("100 %",true); }
			else if (powerpercent < 3600) { powerlog("0 %",true); }
			else {
				powerpercent = (powerpercent - 3600) / 6;
				powerlog(powerpercent + " %",true);
			}
		} else if (Integer.parseInt(e.substring(11,15)) == 129) {
			String tmp[] = null;
			tmp = e.split(" ");
			//fps = tmp[47];
			if (Integer.parseInt(tmp[47]) == 0 ) {fps = "24";}
			else if (Integer.parseInt(tmp[47]) == 1 ) {fps = "25";}
			//if (Integer.parseInt(tmp[47]) == 2 ) {fps = "23.976";}
			//else fps = tmp[47];
		} else {
			e = e.substring(16, 27);
			String[] tmp = null;
			tmp = e.split(" ");
			e = tmp[0] + ": " + tmp[1] + ": " + tmp[2] + ": " + tmp[3] + " @ " + fps + " fps";
			if (Integer.parseInt(tmp[0]) < 25 && Integer.parseInt(tmp[1]) < 61 && Integer.parseInt(tmp[2]) < 61) mLog(e,true);
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
		switch (item.getItemId()) {
			case R.id.menuSettings:
			Intent i = new Intent(this, SettingsActivity.class);
			startActivityForResult(i, Consts.RESULT_SETTINGS);
			break;
		case R.id.menuSettingsReceiveBinary:
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.BINARY).apply();
			break;
		case R.id.menuSettingsReceiveInteger:
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.INTEGER).apply();
			break;
		case R.id.menuSettingsReceiveHexadecimal:
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.HEXADECIMAL).apply();
			break;
		case R.id.menuSettingsReceiveText:
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.TEXT).apply();
			break;
		case R.id.menuSettingsDelimiterNone:
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_NONE).apply();
			break;
		case R.id.menuSettingsDelimiterNewLine:
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_NEW_LINE).apply();
			break;
		case R.id.menuSettingsDelimiterSpace:
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_SPACE).apply();
			break;
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
		usbService.setAction(Consts.RECEIVE_DATA_FORMAT);
		usbService.putExtra(Consts.RECEIVE_DATA_FORMAT, receiveDataFormat);
		usbService.putExtra(Consts.DELIMITER, delimiter);
		startService(usbService);
	}

	void sendToUSBService(String action) {
		usbService.setAction(action);
		startService(usbService);
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
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}