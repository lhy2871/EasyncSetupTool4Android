package com.appspot.usbhidterminal.core;

public abstract class Consts {
	public static final String BINARY = "二进制";
	public static final String INTEGER = "十进制";
	public static final String HEXADECIMAL = "十六进制l";
	public static final String TEXT = "文本";

	public static final String ACTION_USB_PERMISSION = "com.google.android.HID.action.USB_PERMISSION";
	public static final String MESSAGE_SELECT_YOUR_USB_HID_DEVICE = "请选择设备";
	public static final String MESSAGE_CONNECT_YOUR_USB_HID_DEVICE = "请连接设备";
	public static final String RECEIVE_DATA_FORMAT = "接收数据类型";
	public static final String DELIMITER = "换行";
	public static final String DELIMITER_NONE = "无";
	public static final String DELIMITER_NEW_LINE = "换行符";
	public static final String DELIMITER_SPACE = "空格";
	public static final String NEW_LINE = "\n";
	public static final String SPACE = " ";

	public static final String ACTION_USB_SHOW_DEVICES_LIST = "ACTION_USB_SHOW_DEVICES_LIST";
	public static final String ACTION_USB_DATA_TYPE = "ACTION_USB_DATA_TYPE";
	public static final int RESULT_SETTINGS = 7;
	public static final String USB_HID_TERMINAL_CLOSE_ACTION = "USB_HID_TERMINAL_EXIT";
	public static final int USB_HID_TERMINAL_NOTIFICATION = 45277991;

	private Consts() {
	}
}