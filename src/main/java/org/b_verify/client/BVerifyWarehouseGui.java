package org.b_verify.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

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
import org.json.*;

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
	private BVerifyWarehouseApp bverifywarehouseapp;

	// process new receipt section variables
	private Text textWarehouse;
	private Text textAccountant;
	private Text textOwner;
	private Text textDepositor;
	private Combo categorySelector;
	private static final String[] CATEGORIES = new String[] { "corn", "soy", "wheat" };
	private Text textDate;
	private Combo insuranceSelector;
	private static final String[] INSURANCES = new String[] { "full coverage", "against fire", "against theft", "not covered" };
	private Text textWeight;
	private Text textVolume;
	private Text textHumidity;
	private Text textPrice;
	private Text textOtherDetails;
	
	// all receipts section variables
	private Table tableAllReceipts;
	private Label lblLastUpdatedTime;
	private static final String[] ALL_RECEIPT_COLUMNS = {"warehouse", "accountant", "owner", "depositor", 
			"category", "date", "insurance", "weight", "volume", "humidity", "price", "details"};
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
	
	// all updates to GUI must be scheduled via the GUI thread 
	public void updateCurrentCommitment(int newCommitmentNumber, String newCommitmentData, String newCommitmentTxnHash) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				// Add new receipt or redeem receipt
			}
		});
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

		// creates a catena wallet and starts syncing!
		BVerifyWarehouseGui gui = this;
				
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
					bverifywarehouseapp = new BVerifyWarehouseApp(address, txid, network, gui);
					String clientName = bverifywarehouseapp.getClientName();
					// update client name
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							textClientAddress.setText(clientName);
						}
					});
							
					// start the client app asynchronously 
					Thread tr = new Thread(bverifywarehouseapp);
					tr.start();
				} catch (IOException | AlreadyBoundException | NotBoundException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		};
		buttonSync.addListener(SWT.Selection, startSyncListener);
	}
	
	/**
	 * Creates contents of new receipt section.
	 */
	private void createNewReceiptSection() {
		Label lblProcessNewReceipt = new Label(shell, SWT.NONE);
		lblProcessNewReceipt.setFont(sectionHeaderLabelFont);
		lblProcessNewReceipt.setBounds(10, 127, 174, 24);
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
		
		categorySelector = new Combo(shell, SWT.DROP_DOWN);
		categorySelector.setBounds(126, 280, 314, 24);
		categorySelector.setItems(CATEGORIES);
		formToolkit.adapt(categorySelector);
		
		Label labelDate = new Label(shell, SWT.NONE);
		labelDate.setText("Date:");
		labelDate.setFont(subHeaderLabelFont);
		labelDate.setBounds(30, 310, 90, 24);
		formToolkit.adapt(labelDate, true, true);
		
		textDate = new Text(shell, SWT.BORDER);
		textDate.setText("dd/mm/yyyy");
		textDate.setBounds(126, 310, 314, 23);
		
		Label labelInsurance = new Label(shell, SWT.NONE);
		labelInsurance.setText("Insurance:");
		labelInsurance.setFont(subHeaderLabelFont);
		labelInsurance.setBounds(480, 158, 90, 24);
		formToolkit.adapt(labelInsurance, true, true);
		
		insuranceSelector = new Combo(shell, SWT.DROP_DOWN);
		insuranceSelector.setBounds(576, 158, 314, 24);
		insuranceSelector.setItems(INSURANCES);
		formToolkit.adapt(insuranceSelector);
		
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
		
		Label labelHumidity = new Label(shell, SWT.NONE);
		labelHumidity.setText("Humidity (%):");
		labelHumidity.setFont(subHeaderLabelFont);
		labelHumidity.setBounds(480, 248, 90, 24);
		formToolkit.adapt(labelHumidity, true, true);
		
		textHumidity = new Text(shell, SWT.BORDER);
		textHumidity.setBounds(576, 248, 314, 24);
		formToolkit.adapt(textHumidity, true, true);
		
		Label labelPrice = new Label(shell, SWT.NONE);
		labelPrice.setText("Price (USD):");
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
						&& !textDepositor.getText().equals("") && !categorySelector.getText().equals("") && !textDate.getText().equals("")
						&& !insuranceSelector.getText().equals("") && !textWeight.getText().equals("") && !textVolume.getText().equals("") 
						&& !textHumidity.getText().equals("") && !textPrice.getText().equals("") && !textOtherDetails.getText().equals("")) {
					
					JSONObject receiptJSON = createJsonFromReceiptFields();
					resetProcessNewReceiptFields();
					
					try {
						bverifywarehouseapp.initIssueReceipt(receiptJSON);
						// Adds receipt to GUI, should not actually do this until request is approved.
						processIssuedReceipt(receiptJSON);
	    				} catch (UnsupportedEncodingException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
	 * Creates JSONObject from the entries in the process new receipt fields.
	 * @return JSONObject with data from receipt fields.
	 */
	private JSONObject createJsonFromReceiptFields() {
		JSONObject obj = new JSONObject();
        obj.put("warehouse", textWarehouse.getText());
        obj.put("accountant", textAccountant.getText());
        obj.put("owner", textOwner.getText());
        obj.put("depositor", textDepositor.getText());
        obj.put("category", categorySelector.getText());
        obj.put("date", textDate.getText());
        obj.put("insurance", insuranceSelector.getText());
        obj.put("weight", textWeight.getText());
        obj.put("volume", textVolume.getText());
        obj.put("humidity", textHumidity.getText());
        obj.put("price", textPrice.getText());
        obj.put("details", textOtherDetails.getText());
        return obj;
	}
	/**
	 * Resets the entries in the process new receipt fields.
	 */
	private void resetProcessNewReceiptFields() {
		textWarehouse.setText("");
		textAccountant.setText("");
		textOwner.setText("");
		textDepositor.setText("");
		categorySelector.setText("");
		textDate.setText("dd/mm/yyyy");
		insuranceSelector.setText("");
		textWeight.setText("");
		textVolume.setText("");
		textHumidity.setText("");
		textPrice.setText("");
		textOtherDetails.setText("n/a");
	}
	
	/**
	 * Create contents of all receipts section.
	 */
	private void createAllReceiptsSection() {
		Label lblAllWarehouseReceipts = new Label(shell, SWT.NONE);
		lblAllWarehouseReceipts.setFont(sectionHeaderLabelFont);
		lblAllWarehouseReceipts.setBounds(10, 375, 174, 24);
		lblAllWarehouseReceipts.setText("WAREHOUSE RECEIPTS");
		
		tableAllReceipts = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableAllReceipts.setBounds(10, 405, 880, 200);
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
		
		TableColumn tblclmnHumidity = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnHumidity.setWidth(65);
		tblclmnHumidity.setText("Humidity");
		
		TableColumn tblclmnPrice = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnPrice.setWidth(64);
		tblclmnPrice.setText("Price");
		
		TableColumn tblclmnOtherDetails = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnOtherDetails.setWidth(89);
		tblclmnOtherDetails.setText("Other Details");
		
		Button btnRedeemSelectedReceipt = new Button(shell, SWT.NONE);
		btnRedeemSelectedReceipt.setBounds(10, 611, 187, 24);
		btnRedeemSelectedReceipt.setText("Redeem Selected Receipt");
		
		Label lblLastUpdated = new Label(shell, SWT.NONE);
		lblLastUpdated.setFont(subHeaderLabelFont);
		lblLastUpdated.setBounds(524, 611, 85, 24);
		lblLastUpdated.setText("Last Updated:");
		
		lblLastUpdatedTime = new Label(shell, SWT.NONE);
		lblLastUpdated.setFont(subHeaderLabelFont);
		lblLastUpdatedTime.setBounds(615, 611, 275, 24);
		lblLastUpdatedTime.setText("N/A");
		
		Listener redeemReceiptButtonListener = new Listener() {
			public void handleEvent(Event event) {
				// Get confirmation from user.
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
			            | SWT.YES | SWT.NO);
		        messageBox.setText("CONFIRMATION");
			    messageBox.setMessage("Do you really want to void this receipt?");
			    int response = messageBox.open();
			    if (response == SWT.YES) {
			    		// Get selected receiptJSON from table.
			    		TableItem selectedItem = tableAllReceipts.getSelection()[0];
					JSONObject receiptJSON = new JSONObject();
			    		for (int i=0; i< ALL_RECEIPT_COLUMNS.length; i++) {
			    			receiptJSON.put(ALL_RECEIPT_COLUMNS[i], selectedItem.getText(i));
			    		}
			    	
					// Call server to void receipt.
		    			try {
		    				bverifywarehouseapp.initRedeemReceipt(receiptJSON);
		    			} catch (UnsupportedEncodingException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			} catch (RemoteException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
		    			
					// Reflect changes in all receipts table. Should not actually do this until gets approved.
					tableAllReceipts.remove(tableAllReceipts.getSelectionIndices());
					
				    // Update last updated time.
				    String currentTime = LocalDateTime.now().toString();
				    lblLastUpdatedTime.setText(currentTime);
			    }
			}
		};
		btnRedeemSelectedReceipt.addListener(SWT.Selection, redeemReceiptButtonListener);
	}
	
	/**
	 * Process issued receipt for it to be reflected in all receipts table.
	 * @param receiptMap map storing receipt data labels as keys and data values as values.
	 */
	private void processIssuedReceipt(JSONObject receiptJSON) {
		// Find new mappings of values to columns in all receipts table.
		String[] dataValueIndices = new String[ALL_RECEIPT_COLUMN_COUNT];
		for (int i=0; i< ALL_RECEIPT_COLUMN_COUNT; i++) {
			String currentDataValue = receiptJSON.getString(ALL_RECEIPT_COLUMNS[i]);
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
}
