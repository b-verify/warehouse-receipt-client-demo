package org.b_verify.client;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BVerifyWarehouseGui {

	protected Shell shell;
	private Display display;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private final Font sectionHeaderLabelFont = new Font(display, new FontData(".AppleSystemUIFont", 14, SWT.BOLD));
	private final Font subHeaderLabelFont = new Font(display, new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
	
	// configuration section variables
	private boolean configured;
	private Text textClientAddress;
	private Text textServerAddress;
	private Text textServerTxid;
	private static final String[] NETWORKS = new String[] { "REGTEST", "TESTNET3", "MAINNET" };
	private Button buttonSync;
	private BVerifyClientApp bverifyclientapp;

	// process new receipt section variables
	private Text textWarehouse;
	private Text textAccountant;
	private Text textOwner;
	private Text textDepositor;
	private Text textCategory;
	private Text textDate;
	private Text textInsurance;
	private Text textWeight;
	private Text textVolume;
	private Text textQuantity;
	private Text textPrice;
	private Text textOtherDetails;
	
	// all receipts section variables
	private Table tableAllReceipts;
	private Label lblLastUpdatedTime;
	private static final String[] ALL_RECEIPT_COLUMNS = {"Warehouse", "Accountant", "Owner", "Depositor", 
			"Category", "Date", "Insurance", "Weight", "Volume", "Quantity", "Price", "Details"};
	private static final int ALL_RECEIPT_COLUMN_COUNT = ALL_RECEIPT_COLUMNS.length;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BVerifyWarehouseGui window = new BVerifyWarehouseGui();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
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
		shell.setSize(900, 670);
		shell.setText("b_verify Warehouse Application");
		
		// Create Configuration Section
		createConfigurationSection();
		
		// Create Process New Receipt Section
		createNewReceiptSection();
		
		// Create All Warehouse Receipts Section
		createAllReceiptsSection();
	}
	
	/**
	 * Creates contents of configuration section.
	 */
	private void createConfigurationSection() {
		Label lblConfigurationInformation = new Label(shell, SWT.NONE);
		lblConfigurationInformation.setFont(sectionHeaderLabelFont);
		lblConfigurationInformation.setBounds(10, 10, 263, 24);
		lblConfigurationInformation.setText("CONFIGURATION INFORMATION");
		
		Label labelClientAddress = new Label(shell, SWT.NONE);
		labelClientAddress.setText("Client Address:");
		labelClientAddress.setFont(subHeaderLabelFont);
		labelClientAddress.setBounds(30, 38, 90, 24);
		formToolkit.adapt(labelClientAddress, true, true);
		
		textClientAddress = new Text(shell, SWT.BORDER);
		textClientAddress.setText("<---set once the sync is started --->");
		textClientAddress.setBounds(126, 36, 314, 24);
		
		Label labelServerAddress = new Label(shell, SWT.NONE);
		labelServerAddress.setText("Server Address:");
		labelServerAddress.setFont(subHeaderLabelFont);
		labelServerAddress.setBounds(30, 67, 90, 24);
		formToolkit.adapt(labelServerAddress, true, true);
		
		textServerAddress = new Text(shell, SWT.BORDER);
		textServerAddress.setBounds(126, 67, 314, 24);
		
		Label labelServerTxid = new Label(shell, SWT.NONE);
		labelServerTxid.setText("Server Txid:");
		labelServerTxid.setFont(subHeaderLabelFont);
		labelServerTxid.setBounds(480, 36, 90, 24);
		formToolkit.adapt(labelServerTxid, true, true);
		
		textServerTxid = new Text(shell, SWT.BORDER);
		textServerTxid.setBounds(576, 36, 314, 24);
		
		Label labelNetwork = new Label(shell, SWT.NONE);
		labelNetwork.setText("Network:");
		labelNetwork.setFont(subHeaderLabelFont);
		labelNetwork.setBounds(480, 67, 90, 24);
		formToolkit.adapt(labelNetwork, true, true);
		
		Combo networkSelector = new Combo(shell, SWT.DROP_DOWN);
		networkSelector.setBounds(576, 67, 314, 24);
		networkSelector.setItems(NETWORKS);
		formToolkit.adapt(networkSelector);
		
		buttonSync = new Button(shell, SWT.NONE);
		buttonSync.setText("Start Server Sync");
		buttonSync.setBounds(10, 97, 187, 24);
		formToolkit.adapt(buttonSync, true, true);
		
		// mark as starting not configured
		configured = false;
		
		// create event listener for button and sync when clicked 
	}
	
	/**
	 * Creates contents of new receipt section.
	 */
	private void createNewReceiptSection() {
		Label lblProcessNewReceipt = new Label(shell, SWT.NONE);
		lblProcessNewReceipt.setFont(sectionHeaderLabelFont);
		lblProcessNewReceipt.setBounds(10, 131, 174, 24);
		lblProcessNewReceipt.setText("PROCESS NEW RECEIPT");
		
		Label labelWarehouse = new Label(shell, SWT.NONE);
		labelWarehouse.setText("Warehouse:");
		labelWarehouse.setFont(subHeaderLabelFont);
		labelWarehouse.setBounds(30, 161, 90, 24);
		formToolkit.adapt(labelWarehouse, true, true);
		
		textWarehouse = new Text(shell, SWT.BORDER);
		textWarehouse.setBounds(126, 161, 314, 24);
		formToolkit.adapt(textWarehouse, true, true);
		
		Label labelAccountant = new Label(shell, SWT.NONE);
		labelAccountant.setText("Accountant:");
		labelAccountant.setFont(subHeaderLabelFont);
		labelAccountant.setBounds(30, 191, 90, 24);
		formToolkit.adapt(labelAccountant, true, true);
		
		textAccountant = new Text(shell, SWT.BORDER);
		textAccountant.setBounds(126, 191, 314, 24);
		formToolkit.adapt(textAccountant, true, true);
		
		Label labelOwner = new Label(shell, SWT.NONE);
		labelOwner.setText("Owner:");
		labelOwner.setFont(subHeaderLabelFont);
		labelOwner.setBounds(30, 221, 90, 24);
		formToolkit.adapt(labelOwner, true, true);
		
		textOwner = new Text(shell, SWT.BORDER);
		textOwner.setBounds(126, 221, 314, 24);
		
		Label labelDepositor = new Label(shell, SWT.NONE);
		labelDepositor.setText("Depositor:");
		labelDepositor.setFont(subHeaderLabelFont);
		labelDepositor.setBounds(30, 251, 90, 24);
		formToolkit.adapt(labelDepositor, true, true);
		
		textDepositor = new Text(shell, SWT.BORDER);
		textDepositor.setBounds(126, 251, 314, 23);
		formToolkit.adapt(textDepositor, true, true);
		
		Label labelCategory = new Label(shell, SWT.NONE);
		labelCategory.setText("Category:");
		labelCategory.setFont(subHeaderLabelFont);
		labelCategory.setBounds(30, 280, 90, 24);
		formToolkit.adapt(labelCategory, true, true);
		
		textCategory = new Text(shell, SWT.BORDER);
		textCategory.setBounds(126, 280, 314, 24);
		
		Label labelDate = new Label(shell, SWT.NONE);
		labelDate.setText("Date:");
		labelDate.setFont(subHeaderLabelFont);
		labelDate.setBounds(30, 310, 90, 24);
		formToolkit.adapt(labelDate, true, true);
		
		textDate = new Text(shell, SWT.BORDER);
		textDate.setText("DD/MM/YYYY");
		textDate.setBounds(126, 310, 314, 23);
		
		Label labelInsurance = new Label(shell, SWT.NONE);
		labelInsurance.setText("Insurance:");
		labelInsurance.setFont(subHeaderLabelFont);
		labelInsurance.setBounds(480, 158, 90, 24);
		formToolkit.adapt(labelInsurance, true, true);
		
		textInsurance = new Text(shell, SWT.BORDER);
		textInsurance.setBounds(576, 158, 314, 24);
		formToolkit.adapt(textInsurance, true, true);
		
		Label labelWeight = new Label(shell, SWT.NONE);
		labelWeight.setText("Weight (kg):");
		labelWeight.setFont(subHeaderLabelFont);
		labelWeight.setBounds(480, 188, 90, 24);
		formToolkit.adapt(labelWeight, true, true);
		
		textWeight = new Text(shell, SWT.BORDER);
		textWeight.setBounds(576, 188, 314, 24);
		
		Label labelVolume = new Label(shell, SWT.NONE);
		labelVolume.setText("Volume (m^3):");
		labelVolume.setFont(subHeaderLabelFont);
		labelVolume.setBounds(480, 218, 90, 24);
		formToolkit.adapt(labelVolume, true, true);
		
		textVolume = new Text(shell, SWT.BORDER);
		textVolume.setBounds(576, 218, 314, 24);
		
		Label labelQuanity = new Label(shell, SWT.NONE);
		labelQuanity.setText("Quanity:");
		labelQuanity.setFont(subHeaderLabelFont);
		labelQuanity.setBounds(480, 248, 90, 24);
		formToolkit.adapt(labelQuanity, true, true);
		
		textQuantity = new Text(shell, SWT.BORDER);
		textQuantity.setBounds(576, 248, 314, 24);
		formToolkit.adapt(textQuantity, true, true);
		
		Label labelPrice = new Label(shell, SWT.NONE);
		labelPrice.setText("Price (EUR):");
		labelPrice.setFont(subHeaderLabelFont);
		labelPrice.setBounds(480, 277, 90, 24);
		formToolkit.adapt(labelPrice, true, true);
		
		textPrice = new Text(shell, SWT.BORDER);
		textPrice.setBounds(576, 277, 314, 24);
		formToolkit.adapt(textPrice, true, true);
		
		Label labelOtherDetails = new Label(shell, SWT.NONE);
		labelOtherDetails.setText("Other Details:");
		labelOtherDetails.setFont(subHeaderLabelFont);
		labelOtherDetails.setBounds(480, 307, 90, 24);
		formToolkit.adapt(labelOtherDetails, true, true);
		
		textOtherDetails = new Text(shell, SWT.BORDER);
		textOtherDetails.setBounds(576, 307, 314, 24);
		textOtherDetails.setText("n/a");
		formToolkit.adapt(textOtherDetails, true, true);
		
		Button btnIssueNewReceipt = new Button(shell, SWT.NONE);
		btnIssueNewReceipt.setBounds(10, 340, 187, 24);
		btnIssueNewReceipt.setText("Issue New Receipt");
		
		Listener issueReceiptButtonListener = new Listener() {
			public void handleEvent(Event event) {
				// Method with preset data labels.
				if (!textWarehouse.getText().equals("") && !textAccountant.getText().equals("") && !textOwner.getText().equals("") 
						&& !textDepositor.getText().equals("") && !textCategory.getText().equals("") && !textDate.getText().equals("")
						&& !textInsurance.getText().equals("") && !textWeight.getText().equals("") && !textVolume.getText().equals("") 
						&& !textQuantity.getText().equals("") && !textPrice.getText().equals("") && !textOtherDetails.getText().equals("")) {
					HashMap<String, String> receiptMap = new HashMap<String, String>();
					receiptMap.put("Warehouse", textWarehouse.getText());
					receiptMap.put("Accountant", textAccountant.getText());
					receiptMap.put("Owner", textOwner.getText());
					receiptMap.put("Depositor", textDepositor.getText());
					receiptMap.put("Category", textCategory.getText());
					receiptMap.put("Date", textDate.getText());
					receiptMap.put("Insurance", textInsurance.getText());
					receiptMap.put("Weight", textWeight.getText());
					receiptMap.put("Volume", textVolume.getText());
					receiptMap.put("Quantity", textQuantity.getText());
					receiptMap.put("Price", textPrice.getText());
					receiptMap.put("Details", textOtherDetails.getText());
					
					textWarehouse.setText("");
					textAccountant.setText("");
					textOwner.setText("");
					textDepositor.setText("");
					textCategory.setText("");
					textDate.setText("");
					textInsurance.setText("");
					textWeight.setText("");
					textVolume.setText("");
					textQuantity.setText("");
					textPrice.setText("");
					textOtherDetails.setText("");
					
					processIssuedReceipt(receiptMap);
				} else {
					// Display error message indicating missing fields.
					int style = SWT.ICON_ERROR;				    
				    MessageBox messageBox = new MessageBox(shell, style);
				    messageBox.setText("WARNING");
				    messageBox.setMessage("Missing receipt fields.");
				    messageBox.open();
				}
			}
		};
		btnIssueNewReceipt.addListener(SWT.Selection, issueReceiptButtonListener);
	}
	
	/**
	 * Create contents of all receipts section.
	 */
	private void createAllReceiptsSection() {
		Label lblAllWarehouseReceipts = new Label(shell, SWT.NONE);
		lblAllWarehouseReceipts.setFont(sectionHeaderLabelFont);
		lblAllWarehouseReceipts.setBounds(10, 370, 174, 24);
		lblAllWarehouseReceipts.setText("WAREHOUSE RECEIPTS");
		
		tableAllReceipts = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableAllReceipts.setBounds(10, 403, 880, 202);
		tableAllReceipts.setHeaderVisible(true);
		tableAllReceipts.setLinesVisible(true);
		
		TableColumn tblclmnWarehouse = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnWarehouse.setWidth(82);
		tblclmnWarehouse.setText("Warehouse");
		
		TableColumn tblclmnAccountant = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnAccountant.setWidth(82);
		tblclmnAccountant.setText("Accountant");
		
		TableColumn tblclmnOwner = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnOwner.setWidth(75);
		tblclmnOwner.setText("Owner");
		
		TableColumn tblclmnDepositor = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnDepositor.setWidth(73);
		tblclmnDepositor.setText("Depositor");
		
		TableColumn tblclmnCategory = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnCategory.setWidth(78);
		tblclmnCategory.setText("Category");
		
		TableColumn tblclmnDate = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnDate.setWidth(68);
		tblclmnDate.setText("Date");
		
		TableColumn tblclmnInsurance = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnInsurance.setWidth(73);
		tblclmnInsurance.setText("Insurance");
		
		TableColumn tblclmnWeight = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnWeight.setWidth(69);
		tblclmnWeight.setText("Weight");
		
		TableColumn tblclmnVolume = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnVolume.setWidth(62);
		tblclmnVolume.setText("Volume");
		
		TableColumn tblclmnQuantity = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnQuantity.setWidth(65);
		tblclmnQuantity.setText("Quantity");
		
		TableColumn tblclmnPrice = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnPrice.setWidth(64);
		tblclmnPrice.setText("Price");
		
		TableColumn tblclmnOtherDetails = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnOtherDetails.setWidth(89);
		tblclmnOtherDetails.setText("Other Details");
		
		Button btnVoidSelectedReceipt = new Button(shell, SWT.NONE);
		btnVoidSelectedReceipt.setBounds(10, 611, 187, 24);
		btnVoidSelectedReceipt.setText("Void Selected Receipt");
		
		Label lblLastUpdated = new Label(shell, SWT.NONE);
		lblLastUpdated.setFont(subHeaderLabelFont);
		lblLastUpdated.setBounds(671, 611, 85, 24);
		lblLastUpdated.setText("Last Updated:");
		
		lblLastUpdatedTime = new Label(shell, SWT.NONE);
		lblLastUpdatedTime.setAlignment(SWT.RIGHT);
		lblLastUpdated.setFont(subHeaderLabelFont);
		lblLastUpdatedTime.setBounds(762, 611, 128, 24);
		lblLastUpdatedTime.setText("N/A");
		
		Listener voidReceiptButtonListener = new Listener() {
			public void handleEvent(Event event) {
				// Get confirmation from user.
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
			            | SWT.YES | SWT.NO);
		        messageBox.setText("CONFIRMATION");
			    messageBox.setMessage("Do you really want to void this receipt?");
			    int response = messageBox.open();
			    if (response == SWT.YES) {
			    		// Get selected receipt in table.
					// Call server to void receipt.
					// Reflect changes in all receipts table.
					tableAllReceipts.remove(tableAllReceipts.getSelectionIndices());
				    // Update last updated time.
				    String currentTime = LocalDateTime.now().toString();
				    lblLastUpdatedTime.setText(currentTime);
			    }
			}
		};
		btnVoidSelectedReceipt.addListener(SWT.Selection, voidReceiptButtonListener);
	}
	
	/**
	 * Process issued receipt for it to be reflected in all receipts table.
	 * @param receiptMap map storing receipt data labels as keys and data values as values.
	 */
	private void processIssuedReceipt(HashMap<String, String> receiptMap) {
		// Find new mappings of values to columns in all receipts table.
		String[] dataValueIndices = new String[ALL_RECEIPT_COLUMN_COUNT];
		for (int i=0; i< ALL_RECEIPT_COLUMN_COUNT; i++) {
			String currentDataValue = receiptMap.get(ALL_RECEIPT_COLUMNS[i]);
			dataValueIndices[i] = currentDataValue;
		}
		// Create and add table item to table.
		TableItem item = new TableItem(tableAllReceipts, SWT.NONE);
	    item.setText(new String[] { dataValueIndices[0], dataValueIndices[1], dataValueIndices[2], dataValueIndices[3], dataValueIndices[4],
	    		dataValueIndices[5], dataValueIndices[6], dataValueIndices[7], dataValueIndices[8], dataValueIndices[9],
	    		dataValueIndices[10], dataValueIndices[11]});
	    
	    // Update last updated time.
	    String currentTime = LocalDateTime.now().toString();
	    lblLastUpdatedTime.setText(currentTime);
	}

	public void updateCurrentCommitment(int commitmentNumber, String string, String string2) {
		// TODO Auto-generated method stub
		
	}
}
