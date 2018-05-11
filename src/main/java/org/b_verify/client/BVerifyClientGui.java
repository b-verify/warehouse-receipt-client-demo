package org.b_verify.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

import io.grpc.bverify.Receipt;

/**
 * Handles the layout of the b_verify desktop client gui where clients can
 * find last commitment information, process new receipts, browse all receipts,
 * and redeem receipts.
 * 
 * @author Binh
 */
public class BVerifyClientGui {

	protected static Shell shell;
	private static Display display;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private final Font sectionHeaderLabelFont = new Font(display, new FontData(".AppleSystemUIFont", 14, SWT.BOLD));
	private final Font subHeaderLabelFont = new Font(display, new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));

	private BVerifyClientApp bverifyclientapp;
	private BVerifyClientReadScale bverifyclientreadscale;

	// last commitment section variables
	private Label labelClientIdValue;
	private static Label labelNumberValue;
	private static Label labelCommitmentValue;
	private static Label labelCommitDateTimeValue;

	// process new receipt section variables
	private Text textIssuer;
	private Text textAccountant;
	private Text textDepositor;
	private Combo categorySelector;
	private Label lblDateTime;
	private static final String[] CATEGORIES = new String[] { "corn", "soy", "wheat" };
	private Combo insuranceSelector;
	private static final String[] INSURANCES = new String[] { "full coverage", "against fire", "against theft", "not covered" };
	private static Text textWeight;
	private Text textVolume;
	private Text textHumidity;
	private Text textPrice;
	private Text textOtherDetails;

	// all receipts section variables
	private static ArrayList<JSONObject> receiptsToAdd = new ArrayList<JSONObject>();
	private static Table tableAllReceipts;
	private static Label lblLastUpdatedTime;
	private static final String[] ALL_RECEIPT_COLUMNS = {"issuer", "accountant", "depositor", 
			"category", "date", "insurance", "weight", "volume", "humidity", "price", "details"};
	private static final int ALL_RECEIPT_COLUMN_COUNT = ALL_RECEIPT_COLUMNS.length;
	private static HashMap<String, Integer> receiptIndices = new HashMap<>();

	/**
	 * 
	 * @param bverifyclientapp
	 */
	public BVerifyClientGui(BVerifyClientApp bverifyclientapp, BVerifyClientReadScale bverifyclientreadscale) {
		this.bverifyclientapp = bverifyclientapp;
		this.bverifyclientreadscale = bverifyclientreadscale;
	}

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void openWindow() {
		display = Display.getDefault();
		createContents();
		// Process existing receipts
		processIssuedReceipt();
		bverifyclientreadscale.setReadingValueFound(false);

		shell.open();
		shell.layout();		

		setClientAddress(bverifyclientapp.getClientIdString());

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
		shell.setSize(1200, 630);
		shell.setText("B_verify Desktop Client");

		// Create Last Commitment Section
		createLastCommitmentSection();

		// Create Process New Receipt Section
		createNewReceiptSection();

		// Create All Receipts Section
		createAllReceiptsSection();
	}

	/**
	 * Sets the client address in the desktop client gui.
	 * 
	 * @param clientAddress
	 */
	private void setClientAddress(String clientAddress) {
		labelClientIdValue.setText(clientAddress);
	}

	// all updates to GUI must be scheduled via the GUI thread 
	// this is an example
	public static void updateCurrentCommitment(final int newCommitmentNumber, final byte[] newCommitment, final String commitmentTime) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				labelNumberValue.setText(Integer.toString(newCommitmentNumber).toString());
				labelCommitmentValue.setText(newCommitment.toString());
				labelCommitDateTimeValue.setText(commitmentTime);
				processIssuedReceipt();
			}
		});
	}

	/**
	 * Creates contents of last commitment section.
	 */
	private void createLastCommitmentSection() {

		Label lblLastVerifiedCommitment = new Label(shell, SWT.NONE);
		lblLastVerifiedCommitment.setAlignment(SWT.CENTER);
		lblLastVerifiedCommitment.setFont(sectionHeaderLabelFont);
		lblLastVerifiedCommitment.setBounds(10, 10, 435, 24);
		formToolkit.adapt(lblLastVerifiedCommitment, true, true);
		lblLastVerifiedCommitment.setText("LAST VERIFIED COMMITMENT");

		Label labelClientId = new Label(shell, SWT.NONE);
		labelClientId.setFont(subHeaderLabelFont);
		labelClientId.setBounds(30, 40, 90, 24);
		formToolkit.adapt(labelClientId, true, true);
		labelClientId.setText("Client Id:");

		Label labelNumber = new Label(shell, SWT.NONE);
		labelNumber.setText("Number:");
		labelNumber.setFont(subHeaderLabelFont);
		labelNumber.setBounds(30, 70, 90, 24);
		formToolkit.adapt(labelNumber, true, true);

		Label labelCommitment = new Label(shell, SWT.NONE);
		labelCommitment.setText("Commitment");
		labelCommitment.setFont(subHeaderLabelFont);
		labelCommitment.setBounds(30, 100, 90, 24);
		formToolkit.adapt(labelCommitment, true, true);

		Label labelCommitDateTime = new Label(shell, SWT.NONE);
		labelCommitDateTime.setText("Time:");
		labelCommitDateTime.setFont(subHeaderLabelFont);
		labelCommitDateTime.setBounds(30, 130, 90, 24);
		formToolkit.adapt(labelCommitDateTime, true, true);

		labelClientIdValue = new Label(shell, SWT.NONE);
		labelClientIdValue.setText("N/A");
		labelClientIdValue.setBounds(126, 40, 314, 24);
		formToolkit.adapt(labelClientIdValue, true, true);

		labelNumberValue = new Label(shell, SWT.NONE);
		labelNumberValue.setText("N/A");
		labelNumberValue.setBounds(126, 70, 314, 24);
		formToolkit.adapt(labelNumberValue, true, true);

		labelCommitmentValue = new Label(shell, SWT.NONE);
		labelCommitmentValue.setText("N/A");
		labelCommitmentValue.setBounds(126, 100, 314, 24);
		formToolkit.adapt(labelCommitmentValue, true, true);

		labelCommitDateTimeValue = new Label(shell, SWT.NONE);
		labelCommitDateTimeValue.setText("N/A");
		labelCommitDateTimeValue.setBounds(126, 130, 314, 24);
		formToolkit.adapt(labelCommitDateTimeValue, true, true);
	}

	/**
	 * Creates contents of new receipt section.
	 */
	private void createNewReceiptSection() {

		Label lblProcessNewReceipt = new Label(shell, SWT.NONE);
		lblProcessNewReceipt.setAlignment(SWT.CENTER);
		lblProcessNewReceipt.setFont(sectionHeaderLabelFont);
		lblProcessNewReceipt.setBounds(10, 206, 430, 24);
		formToolkit.adapt(lblProcessNewReceipt, true, true);
		lblProcessNewReceipt.setText("PROCESS NEW RECEIPT");

		Label labelIssuer = new Label(shell, SWT.NONE);
		labelIssuer.setText("Issuer:");
		labelIssuer.setFont(subHeaderLabelFont);
		labelIssuer.setBounds(30, 236, 90, 24);
		formToolkit.adapt(labelIssuer, true, true);

		textIssuer = new Text(shell, SWT.BORDER);
		textIssuer.setBounds(131, 236, 314, 24);
		formToolkit.adapt(textIssuer, true, true);

		Label labelAccountant = new Label(shell, SWT.NONE);
		labelAccountant.setText("Accountant:");
		labelAccountant.setFont(subHeaderLabelFont);
		labelAccountant.setBounds(30, 266, 90, 24);
		formToolkit.adapt(labelAccountant, true, true);

		textAccountant = new Text(shell, SWT.BORDER);
		textAccountant.setBounds(131, 266, 314, 24);
		formToolkit.adapt(textAccountant, true, true);

		Label labelDepositor = new Label(shell, SWT.NONE);
		labelDepositor.setText("Depositor:");
		labelDepositor.setFont(subHeaderLabelFont);
		labelDepositor.setBounds(30, 296, 90, 24);
		formToolkit.adapt(labelDepositor, true, true);

		textDepositor = new Text(shell, SWT.BORDER);
		textDepositor.setBounds(131, 296, 314, 23);
		formToolkit.adapt(textDepositor, true, true);

		Label labelCategory = new Label(shell, SWT.NONE);
		labelCategory.setText("Category:");
		labelCategory.setFont(subHeaderLabelFont);
		labelCategory.setBounds(30, 326, 90, 24);
		formToolkit.adapt(labelCategory, true, true);

		categorySelector = new Combo(shell, SWT.DROP_DOWN);
		categorySelector.setBounds(131, 325, 314, 24);
		categorySelector.setItems(CATEGORIES);
		formToolkit.adapt(categorySelector);

		Label labelDateTime = new Label(shell, SWT.NONE);
		labelDateTime.setText("Date/Time:");
		labelDateTime.setFont(subHeaderLabelFont);
		labelDateTime.setBounds(30, 356, 90, 24);
		formToolkit.adapt(labelDateTime, true, true);

		lblDateTime = new Label(shell, SWT.NONE);
		lblDateTime.setBounds(131, 356, 314, 23);
		formToolkit.adapt(lblDateTime, true, true);
		lblDateTime.setText("<-- Date/Time of Depositor Receipt Approval -->");

		Label labelInsurance = new Label(shell, SWT.NONE);
		labelInsurance.setText("Insurance:");
		labelInsurance.setFont(subHeaderLabelFont);
		labelInsurance.setBounds(30, 386, 90, 24);
		formToolkit.adapt(labelInsurance, true, true);

		insuranceSelector = new Combo(shell, SWT.DROP_DOWN);
		insuranceSelector.setBounds(131, 386, 314, 24);
		insuranceSelector.setItems(INSURANCES);
		formToolkit.adapt(insuranceSelector);

		Label labelWeight = new Label(shell, SWT.NONE);
		labelWeight.setText("Weight (kg):");
		labelWeight.setFont(subHeaderLabelFont);
		labelWeight.setBounds(30, 416, 90, 24);
		formToolkit.adapt(labelWeight, true, true);

		textWeight = new Text(shell, SWT.BORDER);
		textWeight.setBounds(131, 416, 314, 24);

		Label labelVolume = new Label(shell, SWT.NONE);
		labelVolume.setText("Volume (m^3):");
		labelVolume.setFont(subHeaderLabelFont);
		labelVolume.setBounds(30, 446, 90, 24);
		formToolkit.adapt(labelVolume, true, true);

		textVolume = new Text(shell, SWT.BORDER);
		textVolume.setBounds(131, 446, 314, 24);

		Label labelHumidity = new Label(shell, SWT.NONE);
		labelHumidity.setText("Humidity (%):");
		labelHumidity.setFont(subHeaderLabelFont);
		labelHumidity.setBounds(30, 476, 90, 24);
		formToolkit.adapt(labelHumidity, true, true);

		textHumidity = new Text(shell, SWT.BORDER);
		textHumidity.setBounds(131, 476, 314, 24);
		formToolkit.adapt(textHumidity, true, true);

		Label labelPrice = new Label(shell, SWT.NONE);
		labelPrice.setText("Price (USD):");
		labelPrice.setFont(subHeaderLabelFont);
		labelPrice.setBounds(30, 506, 90, 24);
		formToolkit.adapt(labelPrice, true, true);

		textPrice = new Text(shell, SWT.BORDER);
		textPrice.setBounds(131, 506, 314, 24);
		formToolkit.adapt(textPrice, true, true);

		Label labelOtherDetails = new Label(shell, SWT.NONE);
		labelOtherDetails.setText("Other Details:");
		labelOtherDetails.setFont(subHeaderLabelFont);
		labelOtherDetails.setBounds(30, 536, 90, 24);
		formToolkit.adapt(labelOtherDetails, true, true);

		textOtherDetails = new Text(shell, SWT.BORDER);
		textOtherDetails.setBounds(131, 536, 314, 24);
		textOtherDetails.setText("N/A");
		formToolkit.adapt(textOtherDetails, true, true);

		Button btnIssueNewReceipt = new Button(shell, SWT.NONE);
		btnIssueNewReceipt.setFont(subHeaderLabelFont);
		btnIssueNewReceipt.setBounds(10, 574, 187, 24);
		btnIssueNewReceipt.setText("Issue New Receipt");

		Listener issueReceiptButtonListener = new Listener() {
			public void handleEvent(Event event) {

				JSONObject receiptJSON = createJsonFromReceiptFields();
				resetProcessNewReceiptFields();
				receiptsToAdd.add(receiptJSON);
				bverifyclientapp.initIssueReceipt(receiptJSON);

				int style = SWT.ICON_INFORMATION;
				MessageBox messageBox = new MessageBox(shell, style);
				messageBox.setText("NOTICE");
				messageBox.setMessage("An Issue Receipt Request was sent to the Depositor. It will appear in the ALL RECEIPTS table when the Depositor approves the Request.");
				messageBox.open();
				bverifyclientreadscale.setReadingValueFound(false);
			}
		};
		btnIssueNewReceipt.addListener(SWT.Selection, issueReceiptButtonListener);
	}

	/**
	 * Creates JSONObject from the entries in the process new receipt fields.
	 * @return JSONObject with data from receipt fields.
	 */
	private JSONObject createJsonFromReceiptFields() {
		Date currentDate = new Date();

		JSONObject obj = new JSONObject();
		obj.put("issuer", textIssuer.getText());
		obj.put("accountant", textAccountant.getText());
		obj.put("depositor", textDepositor.getText());
		obj.put("category", categorySelector.getText());
		obj.put("date", currentDate.toString());
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
		textIssuer.setText("");
		textAccountant.setText("");
		textDepositor.setText("");
		categorySelector.setText("");
		insuranceSelector.setText("");
		textWeight.setText("");
		textVolume.setText("");
		textHumidity.setText("");
		textPrice.setText("");
		textOtherDetails.setText("N/A");
	}

	/**
	 * Create contents of all receipts section.
	 */
	private void createAllReceiptsSection() {

		Label lblAllReceipts = new Label(shell, SWT.NONE);
		lblAllReceipts.setAlignment(SWT.CENTER);
		lblAllReceipts.setFont(sectionHeaderLabelFont);
		lblAllReceipts.setBounds(500, 10, 690, 24);
		formToolkit.adapt(lblAllReceipts, true, true);
		lblAllReceipts.setText("ALL RECEIPTS");

		tableAllReceipts = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableAllReceipts.setBounds(500, 40, 690, 520);
		tableAllReceipts.setHeaderVisible(true);
		tableAllReceipts.setLinesVisible(true);

		TableColumn tblclmnIssuer = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnIssuer.setWidth(100);
		tblclmnIssuer.setText("Issuer");

		TableColumn tblclmnAccountant = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnAccountant.setWidth(100);
		tblclmnAccountant.setText("Accountant");

		TableColumn tblclmnDepositor = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnDepositor.setWidth(100);
		tblclmnDepositor.setText("Depositor");

		TableColumn tblclmnCategory = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnCategory.setWidth(100);
		tblclmnCategory.setText("Category");

		TableColumn tblclmnDate = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnDate.setWidth(100);
		tblclmnDate.setText("Date");

		TableColumn tblclmnInsurance = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnInsurance.setWidth(100);
		tblclmnInsurance.setText("Insurance");

		TableColumn tblclmnWeight = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnWeight.setWidth(100);
		tblclmnWeight.setText("Weight");

		TableColumn tblclmnVolume = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnVolume.setWidth(100);
		tblclmnVolume.setText("Volume");

		TableColumn tblclmnHumidity = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnHumidity.setWidth(100);
		tblclmnHumidity.setText("Humidity");

		TableColumn tblclmnPrice = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnPrice.setWidth(100);
		tblclmnPrice.setText("Price");

		TableColumn tblclmnOtherDetails = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnOtherDetails.setWidth(100);
		tblclmnOtherDetails.setText("Other Details");

		Button btnRedeemSelectedReceipt = new Button(shell, SWT.NONE);
		btnRedeemSelectedReceipt.setFont(subHeaderLabelFont);
		btnRedeemSelectedReceipt.setBounds(500, 574, 187, 24);
		btnRedeemSelectedReceipt.setText("Redeem Selected Receipt");

		Label lblLastUpdated = new Label(shell, SWT.NONE);
		lblLastUpdated.setFont(subHeaderLabelFont);
		lblLastUpdated.setBounds(789, 577, 85, 24);
		lblLastUpdated.setText("Last Updated:");

		lblLastUpdatedTime = new Label(shell, SWT.NONE);
		lblLastUpdated.setFont(subHeaderLabelFont);
		lblLastUpdatedTime.setBounds(880, 578, 310, 24);
		lblLastUpdatedTime.setText("N/A");

		Listener redeemReceiptButtonListener = new Listener() {
			public void handleEvent(Event event) {
				// Get confirmation from user.
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
						| SWT.YES | SWT.NO);
				messageBox.setText("CONFIRMATION");
				messageBox.setMessage("Are you sure you want to redeem this receipt?");
				int response = messageBox.open();
				if (response == SWT.YES) {
					// Get selected receiptJSON from table.
					TableItem selectedItem = tableAllReceipts.getSelection()[0];
					JSONObject receiptJSON = new JSONObject();
					for (int i=0; i< ALL_RECEIPT_COLUMNS.length; i++) {
						receiptJSON.put(ALL_RECEIPT_COLUMNS[i], selectedItem.getText(i));
					}

					// Call server to void receipt.
					//		    			try {
					//		    				bverifyclientapp.initRedeemReceipt(receiptJSON);
					//		    			} catch (UnsupportedEncodingException e) {
					//		    				// TODO Auto-generated catch block
					//		    				e.printStackTrace();
					//		    			} catch (RemoteException e) {
					//		    				// TODO Auto-generated catch block
					//		    				e.printStackTrace();
					//		    			}

					// Reflect changes in all receipts table. Should not actually do this until gets approved.
					tableAllReceipts.remove(tableAllReceipts.getSelectionIndices());

					// Update last updated time.
					Date currentDate = new Date();
					lblLastUpdatedTime.setText(currentDate.toString());
				}
			}
		};
		btnRedeemSelectedReceipt.addListener(SWT.Selection, redeemReceiptButtonListener);
	}

	/**
	 * Process issued receipt for it to be reflected in all receipts table.
	 */
	public static void processIssuedReceipt() {
		// Find new mappings of values to columns in all receipts table.
		String[] dataValueIndices = new String[ALL_RECEIPT_COLUMN_COUNT];
		for (int j=0; j< receiptsToAdd.size(); j++) {
			JSONObject receiptJSON = receiptsToAdd.get(j);
			for (int i=0; i< ALL_RECEIPT_COLUMN_COUNT; i++) {
				String currentDataValue = receiptJSON.getString(ALL_RECEIPT_COLUMNS[i]);
				dataValueIndices[i] = currentDataValue;
			}
			// Create and add table item to table.
			receiptIndices.put(receiptJSON.toString(), tableAllReceipts.getItems().length);
			TableItem item = new TableItem(tableAllReceipts, SWT.NONE);
			item.setText(new String[] { dataValueIndices[0], dataValueIndices[1], dataValueIndices[2], dataValueIndices[3], dataValueIndices[4],
					dataValueIndices[5], dataValueIndices[6], dataValueIndices[7], dataValueIndices[8], dataValueIndices[9],
					dataValueIndices[10]});
		}
		receiptsToAdd.clear();
		Date currentDate = new Date();
		lblLastUpdatedTime.setText(currentDate.toString());	
	}

	/**
	 * Get the existing receipts from the ADS before client gui opens.
	 * @param adsKeyToADSData
	 */
	public static void getExistingReceipts(Map<String, Set<Receipt>> adsKeyToADSData) {
		for (Set<Receipt> receiptSet : adsKeyToADSData.values()) {
			for (Receipt receipt: receiptSet) {
				JSONObject jsonReceipt = convertReceiptToJson(receipt);
				receiptsToAdd.add(jsonReceipt);
			}
		}
	}
	
	/**
	 * Convert Receipt into JSONObject
	 * @param Receipt receipt
	 * @return JSONObject receipt
	 */
	private static JSONObject convertReceiptToJson(Receipt receipt) {
		String warehouseName = "Warehouse";
		String depositorName = "";
		if (receipt.getDepositorId().equals("df3b507b-31c7-4b07-bea2-4256144c2c41")) {
			depositorName = "Alice";
		}
		if (receipt.getDepositorId().equals("e5985074-99c1-4fa6-80bc-dca299b5b12f")) {
			depositorName = "Bob";
		}
		JSONObject jsonReceipt = new JSONObject();
		jsonReceipt.put("issuer", warehouseName);
		jsonReceipt.put("accountant", receipt.getAccountant());
		jsonReceipt.put("depositor", depositorName);
		jsonReceipt.put("category", receipt.getCategory());
		jsonReceipt.put("date", receipt.getDate());
		jsonReceipt.put("insurance", receipt.getInsurance());
		jsonReceipt.put("weight", Double.toString(receipt.getWeight()));
		jsonReceipt.put("volume", Double.toString(receipt.getVolume()));
		jsonReceipt.put("humidity", Double.toString(receipt.getHumidity()));
		jsonReceipt.put("price", Double.toString(receipt.getPrice()));
		jsonReceipt.put("details", receipt.getDetails());
		return jsonReceipt;
	}

	/**
	 * Gets the reading from dymo scale.
	 * @param scaleReading
	 */
	public static void updateScaleReading(String scaleReading) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				textWeight.setText(scaleReading);
			}
		});
	}

	/**
	 * Updates receipt transfer on gui.
	 * @param receipt
	 * @param currentOwner
	 * @param newOwner
	 */
	public static void updateReceiptTransfer(Receipt receipt, String currentOwner, String newOwner) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				JSONObject receiptJson = convertReceiptToJson(receipt);
				int transferReceiptIndex = receiptIndices.get(receiptJson.toString());
				TableItem transferItem = tableAllReceipts.getItem(transferReceiptIndex);
				System.out.println(receiptJson);
				receiptIndices.remove(receiptJson.toString());
				receiptJson.put("depositor", newOwner);
				receiptIndices.put(receiptJson.toString(), transferReceiptIndex);
				System.out.println(receiptJson);
				String[] dataValueIndices = new String[ALL_RECEIPT_COLUMN_COUNT];
				for (int i=0; i< ALL_RECEIPT_COLUMN_COUNT; i++) {
					String currentDataValue = receiptJson.getString(ALL_RECEIPT_COLUMNS[i]);
					dataValueIndices[i] = currentDataValue;
				}
				transferItem.setText(new String[] { dataValueIndices[0], dataValueIndices[1], dataValueIndices[2], dataValueIndices[3], dataValueIndices[4],
						dataValueIndices[5], dataValueIndices[6], dataValueIndices[7], dataValueIndices[8], dataValueIndices[9],
						dataValueIndices[10]});			}
		});
		
	}
}
