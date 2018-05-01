package org.b_verify.client;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

import org.b_verify.common.InsufficientFundsException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;



public class BVerifyClientGui {

	protected Shell shell;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Display display;
	
	// server config 
	private Text serverAddress;
	private Text serverTXID;
	
	// client config
	private Text clientAddress;
	private BVerifyClientApp bverifyclientapp;

	private Label lblOutgoingHeader;
	private Label lblOutgoingRecipientLabel;
	private Label lblOutgoingAmountLabel;
	private Text txtOutgoingActualRecipient;
	private Text txtOutgoingActualAmount;

	private Label lblIncomingHeader;
	private Table tableIncoming;
	private Table tableAllUserBalances;
	private HashMap<String, TableItem> currentTableMap = new HashMap<>();

	private Label lblSyncStatus;
	private Label lblSyncLastVerifiedUpdateDataLabel;
	private Label lblSyncLastVerifiedUpdateDataValue;
	private Label lblSyncLastVerifiedUpdateCommitmentNumberLabel;
	private Label lblSyncLastVerifiedUpdateCommitmentNumberValue;
	private Label lblSyncLastVerifiedUpdateTxnHashLabel;
	private Label lblSyncLastVerifiedUpdateTxnHashValue;

	
	// configuration information (freeze this once started)
	private boolean configured;
	private Combo networkSelector;
	private Button startSync;
	private static final String[] NETWORKS = new String[] { "REGTEST", "TESTNET3", "MAINNET" };


	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BVerifyClientGui window = new BVerifyClientGui();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window and start the main GUI thread
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch()) {
//				display.sleep();
//			}
//		}
	}

	/**
	 * Create contents of application window.
	 */
	protected void createContents() {
		// Create Application Shell
		shell = new Shell();
		shell.setSize(500, 700);
		shell.setText("b_verify Client Application");

		// create server config 
		createServerConfig();
		
		// sync progress
		syncProgressStatus();

		// Create User Configuration Information Section

		// Create Outgoing Transfer Information Section
		createOutgoingTransferSection();

		// Create Incoming Transfer Information Section
		// createIncomingTransferSection();

		// Create All User Balances Information Section
		createAllUserBalancesSection();
	}

	/**
	 * Sets up server configuration information section.
	 */
	private void createServerConfig() {
		Label serverConfigHeader = new Label(shell, SWT.NONE);
		serverConfigHeader.setAlignment(SWT.CENTER);
		serverConfigHeader.setBounds(67, 10, 371, 19);
		formToolkit.adapt(serverConfigHeader, true, true);
		serverConfigHeader.setText("Configuration Information");
		
		Label clientAddressLabel = new Label(shell, SWT.NONE);
		clientAddressLabel.setBounds(67, 35, 139, 19);
		formToolkit.adapt(clientAddressLabel, true, true);
		clientAddressLabel.setText("Client Address:");

		Label serverAddressLabel = new Label(shell, SWT.NONE);
		serverAddressLabel.setBounds(67, 60, 139, 19);
		formToolkit.adapt(serverAddressLabel, true, true);
		serverAddressLabel.setText("Server Address:");

		Label serverTXIDLabel = new Label(shell, SWT.NONE);
		serverTXIDLabel.setBounds(67, 85, 139, 19);
		formToolkit.adapt(serverTXIDLabel, true, true);
		serverTXIDLabel.setText("Server TxID:");
		
		Label networkLabel = new Label(shell, SWT.NONE);
		networkLabel.setBounds(67, 120, 139, 19);
		formToolkit.adapt(networkLabel, true, true);
		networkLabel.setText("Network:");

		clientAddress = new Text(shell, SWT.NONE);
		clientAddress.setBounds(212, 35, 226, 19);
		formToolkit.adapt(clientAddress, true, true);
		clientAddress.setText("<---set once the sync is started --->");
		
		serverAddress = new Text(shell, SWT.NONE);
		serverAddress.setBounds(212, 60, 226, 19);
		formToolkit.adapt(serverAddress, true, true);
		serverAddress.setText("");

		serverTXID = new Text(shell, SWT.NONE);
		serverTXID.setBounds(212, 85, 226, 19);
		formToolkit.adapt(serverTXID, true, true);
		serverTXID.setText("");

		networkSelector = new Combo(shell, SWT.DROP_DOWN);
		networkSelector.setBounds(212, 120, 226, 19);
		networkSelector.setItems(NETWORKS);
		formToolkit.adapt(networkSelector);
		
		// start sync button
		startSync = new Button(shell, SWT.NONE);
		startSync.setBounds(67, 150, 226, 30);
		formToolkit.adapt(startSync, true, true);
		startSync.setText("START SYNC");
		
		// mark as starting not configured
		configured = false;

		// creates a catena wallet and starts syncing!
		BVerifyClientGui gui = this;
		
		Listener startSyncListener = new Listener() {
			public void handleEvent(Event event) {
				if (configured) {
					System.out.println("ALREADY CONFIGURED");
				}
				if (networkSelector.getSelectionIndex() == -1) {
					System.out.println("NOTHING SELECTED - FAIL ");
				}
				String network = networkSelector.getText();
				String address = serverAddress.getText();
				String txid = serverTXID.getText();
				configured = true;			
				try {
					bverifyclientapp = new BVerifyClientApp(address,
							txid, network, gui);
					String clientName = bverifyclientapp.getClientName();
					// update client name
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							clientAddress.setText(clientName);
						}
					});
					
					// start the client app asynchronously 
					Thread tr = new Thread(bverifyclientapp);
					tr.start();
				} catch (IOException | AlreadyBoundException | NotBoundException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		};
		
		startSync.addListener(SWT.Selection, startSyncListener);
	}
	
	
	// all updates to GUI must be scheduled via the GUI thread 
	// this is an example
	public void updateCurrentCommitment(int newCommitmentNumber, String newCommitmentData, String newCommitmentTxnHash) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				lblSyncLastVerifiedUpdateCommitmentNumberValue.setText(Integer.toString(newCommitmentNumber).toString());
				lblSyncLastVerifiedUpdateDataValue.setText(newCommitmentData);
				lblSyncLastVerifiedUpdateTxnHashValue.setText(newCommitmentTxnHash);
			}
		});
	}
	
	// update should also be on GUI thread
	public void updateUserBalances(HashMap<String, String> userBalancesMap) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				// Iterate over all user pubkey in userBalancesMap
			    // Have hash map where keys are public keys of clients and values are references to table items
			    // Once user balances are updated, find TableItem mapping to public key and update the setText with new balance
				
				// If public key is not present in current table, create table item
				// Temporary table entry
				TableItem item1 = new TableItem(tableAllUserBalances, SWT.NONE);
			    item1.setText(new String[] { "currentUserPubKey", "User Balance"});
			    
				// Else get reference to table item and update balance text
				// Temporary table entry
				currentTableMap.get("currentUserPubKey").setText(new String[] { "currentUserPubKey", "Updated Balance"});
			}
		});
	}
	
	/**
	 * Sets up the outgoing transfer information section.
	 */
	private void createOutgoingTransferSection() {
		lblOutgoingHeader = new Label(shell, SWT.NONE);
		lblOutgoingHeader.setAlignment(SWT.CENTER);
		lblOutgoingHeader.setBounds(67, 200, 371, 19);
		formToolkit.adapt(lblOutgoingHeader, true, true);
		lblOutgoingHeader.setText("Transfer");

		lblOutgoingRecipientLabel = new Label(shell, SWT.NONE);
		lblOutgoingRecipientLabel.setBounds(67, 230, 139, 19);
		formToolkit.adapt(lblOutgoingRecipientLabel, true, true);
		lblOutgoingRecipientLabel.setText("Recipient PubKey:");

		lblOutgoingAmountLabel = new Label(shell, SWT.NONE);
		lblOutgoingAmountLabel.setBounds(67, 260, 139, 18);
		formToolkit.adapt(lblOutgoingAmountLabel, true, true);
		lblOutgoingAmountLabel.setText("Amount:");

		txtOutgoingActualRecipient = new Text(shell, SWT.BORDER);
		txtOutgoingActualRecipient.setText("recipientpublickey");
		txtOutgoingActualRecipient.setBounds(212, 230, 226, 19);
		formToolkit.adapt(txtOutgoingActualRecipient, true, true);

		txtOutgoingActualAmount = new Text(shell, SWT.BORDER);
		txtOutgoingActualAmount.setText("1000");
		txtOutgoingActualAmount.setBounds(212, 260, 226, 19);
		formToolkit.adapt(txtOutgoingActualAmount, true, true);

		Button btnOutgoingRequest = new Button(shell, SWT.NONE);
		btnOutgoingRequest.setBounds(67, 300, 210, 25);
		formToolkit.adapt(btnOutgoingRequest, true, true);
		btnOutgoingRequest.setText("Request Transfer");

		Listener transferButtonListener = new Listener() {
			public void handleEvent(Event event) {
				// Call here to b_verify client to initiate transfer request with parameters
				// recipient public key and amount.
				String recipient = txtOutgoingActualRecipient.getText();
				int amount = Integer.parseInt(txtOutgoingActualAmount.getText());
				try {
					bverifyclientapp.startTransfer(recipient, amount);
				} catch (RemoteException | InsufficientFundsException e) {
					e.printStackTrace();
				}
				
			}
		};
		btnOutgoingRequest.addListener(SWT.Selection, transferButtonListener);
	}

	/**
	 * Sets up the incoming transfer information section.
	 */
	private void createIncomingTransferSection() {
		lblIncomingHeader = new Label(shell, SWT.NONE);
		lblIncomingHeader.setAlignment(SWT.CENTER);
		lblIncomingHeader.setBounds(67, 215, 371, 19);
		formToolkit.adapt(lblIncomingHeader, true, true);
		lblIncomingHeader.setText("Incoming Transfer Information");

		tableIncoming = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableIncoming.setBounds(67, 240, 371, 88);
		formToolkit.adapt(tableIncoming);
		formToolkit.paintBordersFor(tableIncoming);
		tableIncoming.setHeaderVisible(true);
		tableIncoming.setLinesVisible(true);

		TableColumn tblclmnIncomingPublicKey = new TableColumn(tableIncoming, SWT.NONE);
		tblclmnIncomingPublicKey.setWidth(219);
		tblclmnIncomingPublicKey.setText("Sender Public Key");

		TableColumn tblclmnIncomingAmount = new TableColumn(tableIncoming, SWT.NONE);
		tblclmnIncomingAmount.setWidth(91);
		tblclmnIncomingAmount.setText("Amount (Units)");

		TableColumn tblclmnIncomingConfirm = new TableColumn(tableIncoming, SWT.NONE);
		tblclmnIncomingConfirm.setWidth(59);
		tblclmnIncomingConfirm.setText("Confirm?");

		// Temporary table entry
		TableItem tableItemIncomingPublicKey = new TableItem(tableIncoming, SWT.NONE);
		tableItemIncomingPublicKey.setText("senderpublickey");
	}

	
	private void syncProgressStatus() {
		lblSyncStatus = new Label(shell, SWT.NONE);
		lblSyncStatus.setAlignment(SWT.CENTER);
		lblSyncStatus.setBounds(67, 349, 371, 20);
		formToolkit.adapt(lblSyncStatus, true, true);
		lblSyncStatus.setText("Last Verified Commitment");
		
		lblSyncLastVerifiedUpdateCommitmentNumberLabel = new Label(shell, SWT.NONE);
		lblSyncLastVerifiedUpdateCommitmentNumberLabel.setBounds(67, 380, 139, 20);
		formToolkit.adapt(lblSyncLastVerifiedUpdateCommitmentNumberLabel, true, true);
		lblSyncLastVerifiedUpdateCommitmentNumberLabel.setText("Number:");

		lblSyncLastVerifiedUpdateCommitmentNumberValue = new Label(shell, SWT.NONE);
		lblSyncLastVerifiedUpdateCommitmentNumberValue.setBounds(212, 380, 226, 20);
		formToolkit.adapt(lblSyncLastVerifiedUpdateCommitmentNumberValue, true, true);
		lblSyncLastVerifiedUpdateCommitmentNumberValue.setText("N/A");
		
		
		lblSyncLastVerifiedUpdateDataLabel = new Label(shell, SWT.NONE);
		lblSyncLastVerifiedUpdateDataLabel.setBounds(67, 410, 139, 20);
		formToolkit.adapt(lblSyncLastVerifiedUpdateDataLabel, true, true);
		lblSyncLastVerifiedUpdateDataLabel.setText("Data:");

		lblSyncLastVerifiedUpdateDataValue = new Label(shell, SWT.NONE);
		lblSyncLastVerifiedUpdateDataValue.setBounds(212, 410, 226, 20);
		formToolkit.adapt(lblSyncLastVerifiedUpdateDataValue, true, true);
		lblSyncLastVerifiedUpdateDataValue.setText("N/A");
		
		lblSyncLastVerifiedUpdateTxnHashLabel = new Label(shell, SWT.NONE);
		lblSyncLastVerifiedUpdateTxnHashLabel.setBounds(67, 440, 139, 20);
		formToolkit.adapt(lblSyncLastVerifiedUpdateTxnHashLabel, true, true);
		lblSyncLastVerifiedUpdateTxnHashLabel.setText("Txn Hash:");

		lblSyncLastVerifiedUpdateTxnHashValue = new Label(shell, SWT.NONE);
		lblSyncLastVerifiedUpdateTxnHashValue.setBounds(212, 440, 226, 20);
		formToolkit.adapt(lblSyncLastVerifiedUpdateTxnHashValue, true, true);
		lblSyncLastVerifiedUpdateTxnHashValue.setText("N/A");		
	}
	
	/**
	 * Sets up the all user balances information section.
	 */
	private void createAllUserBalancesSection() {
		tableAllUserBalances = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableAllUserBalances.setBounds(67, 480, 371, 153);
		formToolkit.adapt(tableAllUserBalances);
		formToolkit.paintBordersFor(tableAllUserBalances);
		tableAllUserBalances.setHeaderVisible(true);
		tableAllUserBalances.setLinesVisible(true);

		TableColumn tblclmnAllUserPublicKey = new TableColumn(tableAllUserBalances, SWT.NONE);
		tblclmnAllUserPublicKey.setWidth(263);
		tblclmnAllUserPublicKey.setText("User Public Key");
		
		TableColumn tblclmnAllUserBalance = new TableColumn(tableAllUserBalances, SWT.NONE);
		tblclmnAllUserBalance.setWidth(106);
		tblclmnAllUserBalance.setText("Balance");

		// Temporary table entry
		TableItem item1 = new TableItem(tableAllUserBalances, SWT.NONE);
	    item1.setText(new String[] { "Column1 text", "Column2 text"});
	}
}
