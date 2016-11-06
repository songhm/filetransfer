package com.example.file_transfer.data;

public class MsgConst {
	public static final int USERIN  = 0xF0000001;
	public static final int USEROUT  = 0xF0000002;
	public static final int REQUESTCONNECT = 0xF0000003;
	public static final int REJECTCONNECT = 0xF0000004;
	public static final int ACCEPTCONNECT = 0xF0000005;
	public static final int USEROFF = 0xF0000006;
	public static final int RECEIVEFILE = 0xF0000007;
	public static final int FILEPAUSE = 0xF0000008;
	public static final int FILECANCEL = 0xF0000009;
	public static final int WIFIACTIVE = 0xF000000A;
	public static final int EXEUSERMOVE = 0xF000000B;
	public static final int FILERECEIVEINFO = 0xF000000C;
	public static final int FILERECEIVESUCCESS = 0xF000000D;
	public static final int PORT = 4567;
	public static final int STOPSEARCH  = 0x00000003;
	public static final int STARTSEARCH =  0x00000004;
	public static final int SEARCHBACK =   0x00000005;
	public static final int HELLO = 0x00000006;
	public static final int HELLOTOO = 0x00000007;
	
	
}

