package org.b_verify.client;

import org.hid4java.*;
import org.hid4java.jna.*;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Reads the weight from the DYMO M10 scale in order to be reflected as the weight
 * in the client gui. Demonstrates BVerify's IoT capabilities. Requires the hid4java.jar
 * in order to connect and read from scale. Scale must be turned on prior to running the
 * demo and cannot turn off during the demo or else the SWT window will crash.
 * 
 * @author Binh
 */
public class BVerifyClientReadScale implements Runnable{

	// ID sepcific to DYMO M10 scale
	private final Short VENDOR_ID = 0x0922;
	private final Short PRODUCT_ID = (short) 0x8003;
	
	private Short raw_weight;
	private byte[] message = new byte[8];
	
	private int msecPerSec = 1000;
	// Number of seconds to wait in order to take a reading of the scale so the item stabilizes on the scale.
	private int numSecWait = 2;
	// Min weight for it to begin weighing for the gui.
	private int activationWeight = 100;
	// Time between scale readings to minimize the number of instructions the program has to handle.
	private int timeBetweenWeigh = 100;
	// Tracks whether a reading value is found for the current receipt. Only resets when the 
	// current receipt is issued or if the item is taken off of the scale and it goes to 0.
	private boolean readingValueFound = true;
	// Tracks when the current weighing is started.
	private boolean startedWeigh = false;
	private Date startWeighDate;
	private HidDevice dymo;
	
	/**
	 * Connects to scale and starts reading scale measurements.
	 */
	public BVerifyClientReadScale() {
		// Connects to scale using hidapi
		HidServices hidServices = HidManager.getHidServices();
		this.dymo = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, null);
		hidServices.shutdown();	
		
		// Gets scale measurements every timeBetweenWeigh milliseconds
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(this, 0, timeBetweenWeigh, TimeUnit.MILLISECONDS);
	}

	/**
	 * Gets scale reading and updates receipt weight field on gui.
	 */
	@Override
	public void run() {
		dymo.read(message, 1000);
		byte[] data = new byte[2];
		data[0] = message[5];
		data[1] = message[4];
		ByteBuffer wrapped = ByteBuffer.wrap(data);
		raw_weight = wrapped.getShort();
		// Reset if weighed item is taken off scale
		if (raw_weight == 0) {
			readingValueFound = false;
			startedWeigh = false;
		}
		if (readingValueFound == false) {
			if (raw_weight > activationWeight) {
				// Signals the start of the current weighing
				if (startedWeigh == false) {
					startWeighDate = new Date();
					startedWeigh = true;
				}
				Date currentDate = new Date();
				long differenceInSec = (currentDate.getTime() - startWeighDate.getTime()) / msecPerSec;
				// Waits for numSecWait for item to stabilize on scale to take measurment
				if (differenceInSec > numSecWait) {
					BVerifyClientGui.updateScaleReading(raw_weight.toString());
					readingValueFound = true;
					startedWeigh = false;
				}
			}
		}
	}

	/**
	 * Sets whether the current receipts weight value has been recorded.
	 * @param found
	 */
	public void setReadingValueFound(boolean found) {
		this.readingValueFound = found;
	}
}

