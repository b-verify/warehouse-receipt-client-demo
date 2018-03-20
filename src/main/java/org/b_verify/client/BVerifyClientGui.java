package org.b_verify.client;

import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class BVerifyClientGui {

	protected Shell shell;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Label lblUserConfigHeader;
	private Label lblUserConfigPublicKeyLabel;
	private Label lblUserConfigBalanceLabel;
	private Label lblUserConfigActualPublicKey;
	private Label lblUserConfigActualBalance;
	
	private Label lblOutgoingHeader;
	private Label lblOutgoingRecipientLabel;
	private Label lblOutgoingAmountLabel;
	private Text txtOutgoingActualRecipient;
	private Text txtOutgoingActualAmount;

	private Label lblIncomingHeader;
	private Table tableIncoming;
	
	private Label lblAllUsersHeader;
	private Table tableAllUserBalances;
	private Label lblAllUserLastUpdateLabel;
	private Label lblAllUserLastUpdateActualTime;
	
	/**
	 * Launch the application.
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
	 * Create contents of application window.
	 */
	protected void createContents() {
		// Create Application Shell
		shell = new Shell();
		shell.setSize(500, 630);
		shell.setText("B_Verify Client Application");
		
		// Create User Configuration Information Section
		createUserConfigSection();

		// Create Outgoing Transfer Information Section
		createOutgoingTransferSection();
		
		// Create Incoming Transfer Information Section
		createIncomingTransferSection();

		// Create All User Balances Information Section
		createAllUserBalancesSection();
	}
	
	/**
	 * Sets up the user configuration information section.
	 */
	private void createUserConfigSection() {
		lblUserConfigHeader = new Label(shell, SWT.NONE);
		lblUserConfigHeader.setAlignment(SWT.CENTER);
		lblUserConfigHeader.setBounds(67, 10, 371, 19);
		formToolkit.adapt(lblUserConfigHeader, true, true);
		lblUserConfigHeader.setText("User Configuration Information");
		
		lblUserConfigPublicKeyLabel = new Label(shell, SWT.NONE);
		lblUserConfigPublicKeyLabel.setBounds(67, 35, 139, 19);
		formToolkit.adapt(lblUserConfigPublicKeyLabel, true, true);
		lblUserConfigPublicKeyLabel.setText("User Public Key:");
		
		lblUserConfigBalanceLabel = new Label(shell, SWT.NONE);
		lblUserConfigBalanceLabel.setBounds(67, 60, 139, 19);
		formToolkit.adapt(lblUserConfigBalanceLabel, true, true);
		lblUserConfigBalanceLabel.setText("User Balance (Units):");
		
		lblUserConfigActualPublicKey = new Label(shell, SWT.NONE);
		lblUserConfigActualPublicKey.setBounds(212, 35, 226, 19);
		formToolkit.adapt(lblUserConfigActualPublicKey, true, true);
		lblUserConfigActualPublicKey.setText("userpublickey");
		
		lblUserConfigActualBalance = new Label(shell, SWT.NONE);
		lblUserConfigActualBalance.setBounds(212, 60, 226, 19);
		formToolkit.adapt(lblUserConfigActualBalance, true, true);
		lblUserConfigActualBalance.setText("1000");
	}
	
	/**
	 * Sets up the outgoing transfer information section.
	 */
	private void createOutgoingTransferSection() {
		lblOutgoingHeader = new Label(shell, SWT.NONE);
		lblOutgoingHeader.setAlignment(SWT.CENTER);
		lblOutgoingHeader.setBounds(67, 102, 371, 19);
		formToolkit.adapt(lblOutgoingHeader, true, true);
		lblOutgoingHeader.setText("Outgoing Transfer Information");
		
		lblOutgoingRecipientLabel = new Label(shell, SWT.NONE);
		lblOutgoingRecipientLabel.setBounds(67, 127, 139, 19);
		formToolkit.adapt(lblOutgoingRecipientLabel, true, true);
		lblOutgoingRecipientLabel.setText("Recipient (PubKey):");
		
		lblOutgoingAmountLabel = new Label(shell, SWT.NONE);
		lblOutgoingAmountLabel.setBounds(67, 152, 139, 18);
		formToolkit.adapt(lblOutgoingAmountLabel, true, true);
		lblOutgoingAmountLabel.setText("Transfer Amount (Units):");
		
		txtOutgoingActualRecipient = new Text(shell, SWT.BORDER);
		txtOutgoingActualRecipient.setText("recipientpublickey");
		txtOutgoingActualRecipient.setBounds(212, 127, 226, 19);
		formToolkit.adapt(txtOutgoingActualRecipient, true, true);
		
		txtOutgoingActualAmount = new Text(shell, SWT.BORDER);
		txtOutgoingActualAmount.setText("1000");
		txtOutgoingActualAmount.setBounds(212, 149, 226, 19);
		formToolkit.adapt(txtOutgoingActualAmount, true, true);
		
		Button btnOutgoingRequest = new Button(shell, SWT.NONE);
		btnOutgoingRequest.setBounds(67, 176, 371, 19);
		formToolkit.adapt(btnOutgoingRequest, true, true);
		btnOutgoingRequest.setText("Request Transfer");
		
		Listener transferButtonListener = new Listener() {
			public void handleEvent(Event event) {
				// Call here to b_verify client to initiate transfer request with parameters recipient public key and amount.
				String recipient = txtOutgoingActualRecipient.getText();
				String amount = txtOutgoingActualAmount.getText();
		    		txtOutgoingActualRecipient.setText("Request sent to: " + recipient);
		   		txtOutgoingActualAmount.setText("Amount sent: " + amount);
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
	
	/**
	 * Sets up the all user balances information section.
	 */
	private void createAllUserBalancesSection() {
		tableAllUserBalances = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableAllUserBalances.setBounds(67, 373, 371, 153);
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
		TableItem tableItemAllUserPublicKey = new TableItem(tableAllUserBalances, SWT.NONE);
		tableItemAllUserPublicKey.setText(new String[] {});
		tableItemAllUserPublicKey.setText("anotheruserpublickey");
		
		Button btnAllUserGetBalances = new Button(shell, SWT.NONE);
		btnAllUserGetBalances.setBounds(67, 557, 371, 19);
		formToolkit.adapt(btnAllUserGetBalances, true, true);
		btnAllUserGetBalances.setText("Update All User Balances");
		
		lblAllUserLastUpdateLabel = new Label(shell, SWT.NONE);
		lblAllUserLastUpdateLabel.setBounds(67, 532, 139, 19);
		formToolkit.adapt(lblAllUserLastUpdateLabel, true, true);
		lblAllUserLastUpdateLabel.setText("Last Verified Update:");
		
		lblAllUserLastUpdateActualTime = new Label(shell, SWT.NONE);
		lblAllUserLastUpdateActualTime.setBounds(212, 532, 226, 19);
		formToolkit.adapt(lblAllUserLastUpdateActualTime, true, true);
		lblAllUserLastUpdateActualTime.setText("March 18, 2018 3:00:00 PM EST");
		
		lblAllUsersHeader = new Label(shell, SWT.NONE);
		lblAllUsersHeader.setAlignment(SWT.CENTER);
		lblAllUsersHeader.setBounds(67, 349, 371, 18);
		formToolkit.adapt(lblAllUsersHeader, true, true);
		lblAllUsersHeader.setText("All User Balances Information");
		
		Listener getBalancesButtonListener = new Listener() {
			public void handleEvent(Event event) {
				// Call here to b_verify client to get updated verified user balances and time stamp.
		    		tableItemAllUserPublicKey.setText("updated balance from another user");
		    		lblAllUserLastUpdateActualTime.setText("timestamp of last update");
		    		lblUserConfigActualBalance.setText("updated user balance");
		    }
		};
		btnAllUserGetBalances.addListener(SWT.Selection, getBalancesButtonListener);
	}
}
