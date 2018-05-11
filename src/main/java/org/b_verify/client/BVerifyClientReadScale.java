package org.b_verify.client;

import org.hid4java.*;
import org.hid4java.jna.*;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// ReadDYMO
// 17_02_28 JR
// v1.0
// read weight from Dymo M10 scales
//requires hid4java.jar and jna.jar
public class BVerifyClientReadScale implements Runnable{

	// specifc to these scales
	private final Short VENDOR_ID = 0x0922;
	private final Short PRODUCT_ID = (short) 0x8003;
	
	private Short raw_weight;
	private byte[] message = new byte[8];
	
	private int msecPerSec = 1000;
	private int numSecWait = 2;
	private int activationWeight = 200;
	private int timeBetweenWeigh = 100;
	private boolean readingValueFound = true;
	private boolean startedWeigh = false;
	private Date startWeighDate;
	
	private HidDevice dymo;
	
	public BVerifyClientReadScale() {
		HidServices hidServices = HidManager.getHidServices();
		this.dymo = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, null);
		hidServices.shutdown();	
		
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(this, 0, timeBetweenWeigh, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		if (readingValueFound == false) {
			dymo.read(message, 1000);
			byte[] data = new byte[2];
			data[0] = message[5];
			data[1] = message[4];
			ByteBuffer wrapped = ByteBuffer.wrap(data);
			raw_weight = wrapped.getShort();
			if (raw_weight > activationWeight) {
				if (startedWeigh == false) {
					startWeighDate = new Date();
					startedWeigh = true;
				}
				Date currentDate = new Date();
				long differenceInSec = (currentDate.getTime() - startWeighDate.getTime()) / msecPerSec;
				if (differenceInSec > numSecWait) {
					BVerifyClientGui.updateScaleReading(raw_weight.toString());
					readingValueFound = true;
					startedWeigh = false;
				}
			}
		}
	}

	public void setReadingValueFound(boolean found) {
		this.readingValueFound = found;
	}
}

