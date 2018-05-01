package org.b_verify.client;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

public class BVerifyClientConfig {

	protected Shell shell;
	private Display display;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	private Text textServerAddress;
	private Text textServerTxid;
	private Combo networkSelector;
	private Text textClientAddress;
	private boolean configured;
	private BVerifyWarehouseApp bverifywarehouseapp;
	private BVerifyWarehouseGui bverifywarehousegui;
	
	private final Font sectionHeaderLabelFont = new Font(display, new FontData(".AppleSystemUIFont", 14, SWT.BOLD));
	private final Font subHeaderLabelFont = new Font(display, new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
	private static final String[] NETWORKS = new String[] { "REGTEST", "TESTNET3", "MAINNET" };

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BVerifyClientConfig window = new BVerifyClientConfig();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 210);
		shell.setText("B_verify Desktop Client");
		
		Label lblConfiguration = new Label(shell, SWT.NONE);
		lblConfiguration.setFont(sectionHeaderLabelFont);
		lblConfiguration.setBounds(10, 10, 430, 22);
		lblConfiguration.setText("B_verify Configuration");
		
		Label lblClientAddress = new Label(shell, SWT.NONE);
		lblClientAddress.setFont(subHeaderLabelFont);
		lblClientAddress.setBounds(20, 38, 96, 22);
		lblClientAddress.setText("Client Address:");
		
		Label lblServerAddress = new Label(shell, SWT.NONE);
		lblServerAddress.setFont(subHeaderLabelFont);
		lblServerAddress.setBounds(20, 66, 96, 22);
		lblServerAddress.setText("Server Address:");
		
		Label lblServerTxid = new Label(shell, SWT.NONE);
		lblServerTxid.setFont(subHeaderLabelFont);
		lblServerTxid.setBounds(20, 94, 96, 22);
		lblServerTxid.setText("Server Txid:");
		
		Label lblNetwork = new Label(shell, SWT.NONE);
		lblNetwork.setFont(subHeaderLabelFont);
		lblNetwork.setBounds(20, 122, 96, 22);
		lblNetwork.setText("Network:");
		
		textServerAddress = new Text(shell, SWT.BORDER);
		textServerAddress.setBounds(122, 64, 318, 22);
		
		textServerTxid = new Text(shell, SWT.BORDER);
		textServerTxid.setBounds(122, 92, 318, 22);
		
		networkSelector = new Combo(shell, SWT.DROP_DOWN);
		networkSelector.setBounds(122, 120, 318, 22);
		networkSelector.setItems(NETWORKS);
		formToolkit.adapt(networkSelector);
		
		textClientAddress = new Text(shell, SWT.BORDER);
		textClientAddress.setText("<-- set once the sync is started -->");
		textClientAddress.setBounds(122, 36, 318, 22);
		
		Button btnStartSync = new Button(shell, SWT.NONE);
		btnStartSync.setFont(subHeaderLabelFont);
		btnStartSync.setBounds(10, 150, 106, 28);
		btnStartSync.setText("Start Sync");
		
		// mark as starting not configured
		configured = false;
		
		Listener startSyncListener = new Listener() {
			public void handleEvent(Event event) {
				if (configured) {
					System.out.println("ALREADY CONFIGURED");
				}
				if (networkSelector.getSelectionIndex() == -1) {
					System.out.println("NOTHING SELECTED - FAIL ");
				}
				String network = networkSelector.getText();
				String address = textServerAddress.getText();
				String txid = textServerTxid.getText();
				configured = true;			
				try {
					shell.close();
					bverifywarehousegui = new BVerifyWarehouseGui();
					bverifywarehouseapp = new BVerifyWarehouseApp(address, txid, network, bverifywarehousegui);
					String clientName = bverifywarehouseapp.getClientName();
					bverifywarehousegui.setClientAddress(clientName);

					// start the client app asynchronously 
					Thread tr = new Thread(bverifywarehouseapp);
					tr.start();
					
				} catch (IOException | AlreadyBoundException | NotBoundException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		};
		btnStartSync.addListener(SWT.Selection, startSyncListener);
	}
}
