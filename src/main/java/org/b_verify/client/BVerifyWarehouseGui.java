package org.b_verify.client;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
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

public class BVerifyWarehouseGui {

	protected Shell shell;
	private Text textDataLabel;
	private Text textData;
	private Table tableCurrentData;
	private Table tableAllReceipts;
	private Label lblLastUpdatedTime;
	
	private static final int DATA_LABEL_INDEX = 0;
	private static final int DATA_VALUE_INDEX = 1;
	
	private static final String[] ALL_RECEIPT_COLUMNS = {"client", "category", "amount", "units", "date"};
	private static final int ALL_RECEIPT_COLUMN_COUNT = ALL_RECEIPT_COLUMNS.length;
	private static final HashMap<String, Integer> ALL_RECEIPT_COLUMN_INDEX_MAP = createAllReceiptColumnIndexMap();
	private Text textClient;
	private Text textCategory;
	private Text textAmount;
	private Text textUnits;
	private Text textDate;
	private static HashMap<String, Integer> createAllReceiptColumnIndexMap()
    {
        HashMap<String, Integer> columnIndexMap = new HashMap<String, Integer>();
        for (int i=0; i< ALL_RECEIPT_COLUMN_COUNT; i++) {
            columnIndexMap.put(ALL_RECEIPT_COLUMNS[i], i);

        }
        return columnIndexMap;
    }
	
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
		shell.setSize(450, 550);
		shell.setText("b_verify Warehouse Application");
		
		// Create Process New Receipt Section
		createNewReceiptSection();
		
		// Create All Warehouse Receipts Section
		createAllReceiptsSection();
	}
	
	/**
	 * Creates contents of new receipt section.
	 */
	private void createNewReceiptSection() {
		Label lblProcessNewReceipt = new Label(shell, SWT.NONE);
		Font newReceiptFont = new Font(lblProcessNewReceipt.getDisplay(), new FontData(".AppleSystemUIFont", 14, SWT.BOLD));
		lblProcessNewReceipt.setFont(newReceiptFont);
		lblProcessNewReceipt.setBounds(10, 10, 174, 24);
		lblProcessNewReceipt.setText("PROCESS NEW RECEIPT");
		
//		Label lblDataLabel = new Label(shell, SWT.NONE);
//		Font dataLabelFont = new Font(lblDataLabel.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
//		lblDataLabel.setFont(dataLabelFont);
//		lblDataLabel.setAlignment(SWT.RIGHT);
//		lblDataLabel.setBounds(10, 38, 77, 24);
//		lblDataLabel.setText("Data Label:");
//		
//		Label lblDataValue = new Label(shell, SWT.NONE);
//		Font dataValueFont = new Font(lblDataValue.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
//		lblDataValue.setFont(dataValueFont);
//		lblDataValue.setAlignment(SWT.RIGHT);
//		lblDataValue.setBounds(10, 68, 77, 24);
//		lblDataValue.setText("Data Value:");
//		
//		textDataLabel = new Text(shell, SWT.BORDER);
//		textDataLabel.setText("");
//		textDataLabel.setBounds(93, 38, 247, 24);
//		
//		textData = new Text(shell, SWT.BORDER);
//		textData.setText("");
//		textData.setBounds(93, 68, 247, 24);
//		
//		Button btnAddData = new Button(shell, SWT.NONE);
//		btnAddData.setBounds(10, 98, 187, 24);
//		btnAddData.setText("Add Data to Receipt");
//		
//		Listener addDataButtonListener = new Listener() {
//			public void handleEvent(Event event) {
//				String dataLabelText = textDataLabel.getText();
//				String dataValueText = textData.getText();
//				if (!dataLabelText.equals("") && !dataValueText.equals("")) {
//					// Check for duplicate data row. (should do later)
//					
//					// Add it to table of current data.
//					TableItem item = new TableItem(tableCurrentData, SWT.NONE);
//				    item.setText(new String[] { dataLabelText, dataValueText});
//					// Clear text fields.
//				    textDataLabel.setText("");
//					textData.setText("");	
//				} else {
//					// Display error message indicating missing fields.
//					int style = SWT.ICON_ERROR;				    
//				    MessageBox messageBox = new MessageBox(shell, style);
//				    messageBox.setText("WARNING");
//				    messageBox.setMessage("Missing data label and/or data value.");
//				    messageBox.open();
//				}
//			}
//		};
//		btnAddData.addListener(SWT.Selection, addDataButtonListener);
//		
//		tableCurrentData = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
//		tableCurrentData.setBounds(10, 138, 430, 100);
//		tableCurrentData.setHeaderVisible(true);
//		tableCurrentData.setLinesVisible(true);
//		
//		TableColumn tblclmnDataLabel = new TableColumn(tableCurrentData, SWT.CENTER);
//		tblclmnDataLabel.setWidth(218);
//		tblclmnDataLabel.setText("Data Label");
//		
//		TableColumn tblclmnDataValue = new TableColumn(tableCurrentData, SWT.CENTER);
//		tblclmnDataValue.setWidth(210);
//		tblclmnDataValue.setText("Data Value");
//		
//		Button btnRemoveData = new Button(shell, SWT.NONE);
//		btnRemoveData.setBounds(253, 244, 187, 24);
//		btnRemoveData.setText("Remove Selected Data");
//		
//		Listener removeDataButtonListener = new Listener() {
//			public void handleEvent(Event event) {
//				// Remove selected data rows.
//				tableCurrentData.remove(tableCurrentData.getSelectionIndices());
//			}
//		};
//		btnRemoveData.addListener(SWT.Selection, removeDataButtonListener);
		
		Label lblClient = new Label(shell, SWT.NONE);
		Font clientLabelFont = new Font(lblClient.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
		lblClient.setFont(clientLabelFont);
		lblClient.setBounds(30, 40, 55, 24);
		lblClient.setText("Client:");
		
		textClient = new Text(shell, SWT.BORDER);
		textClient.setBounds(111, 40, 329, 24);
		
		Label lblCategory = new Label(shell, SWT.NONE);
		Font categoryLabelFont = new Font(lblCategory.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
		lblCategory.setFont(categoryLabelFont);
		lblCategory.setBounds(30, 70, 55, 24);
		lblCategory.setText("Category:");
		
		textCategory = new Text(shell, SWT.BORDER);
		textCategory.setBounds(111, 70, 329, 24);
		
		Label lblAmount = new Label(shell, SWT.NONE);
		Font amountLabelFont = new Font(lblAmount.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
		lblAmount.setFont(amountLabelFont);
		lblAmount.setBounds(30, 100, 55, 24);
		lblAmount.setText("Amount:");
		
		textAmount = new Text(shell, SWT.BORDER);
		textAmount.setBounds(111, 100, 329, 24);
		
		Label lblUnits = new Label(shell, SWT.NONE);
		Font unitsLabelFont = new Font(lblUnits.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
		lblUnits.setFont(unitsLabelFont);
		lblUnits.setBounds(30, 130, 61, 24);
		lblUnits.setText("Units:");
		
		textUnits = new Text(shell, SWT.BORDER);
		textUnits.setBounds(111, 130, 329, 24);
		
		Label lblDate = new Label(shell, SWT.NONE);
		Font dateLabelFont = new Font(lblDate.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
		lblDate.setFont(dateLabelFont);
		lblDate.setBounds(30, 160, 75, 24);
		lblDate.setText("Date:");
		
		textDate = new Text(shell, SWT.BORDER);
		textDate.setBounds(111, 160, 329, 23);
		
		Button btnIssueNewReceipt = new Button(shell, SWT.NONE);
		btnIssueNewReceipt.setBounds(10, 190, 187, 24);
		btnIssueNewReceipt.setText("Issue New Receipt");
		
		Listener issueReceiptButtonListener = new Listener() {
			public void handleEvent(Event event) {
				/*
				// Call in server to issue receipt.
				HashMap<String, String> receiptMap = new HashMap<String, String>();
				for (int i=0; i< tableCurrentData.getItemCount(); i++) {
					TableItem currentItem = tableCurrentData.getItem(i);
					receiptMap.put(currentItem.getText(DATA_LABEL_INDEX), currentItem.getText(DATA_VALUE_INDEX));
				}
				processIssuedReceipt(receiptMap);
				// Clear current data table.
				tableCurrentData.removeAll();
				// Reflect changes in all receipts table.
				*/
				
				// Method with preset data labels.
				if (!textClient.getText().equals("") && !textCategory.getText().equals("") 
						&& !textAmount.getText().equals("") && !textUnits.getText().equals("")
						&& !textDate.getText().equals("")) {
					HashMap<String, String> receiptMap = new HashMap<String, String>();
					receiptMap.put("client", textClient.getText());
					receiptMap.put("category", textCategory.getText());
					receiptMap.put("amount", textAmount.getText());
					receiptMap.put("units", textUnits.getText());
					receiptMap.put("date", textDate.getText());
					textClient.setText("");
					textCategory.setText("");
					textAmount.setText("");
					textUnits.setText("");
					textDate.setText("");
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
		Font allReceiptsFont = new Font(lblAllWarehouseReceipts.getDisplay(), new FontData(".AppleSystemUIFont", 14, SWT.BOLD));
		lblAllWarehouseReceipts.setFont(allReceiptsFont);
		lblAllWarehouseReceipts.setBounds(10, 238, 174, 24);
		lblAllWarehouseReceipts.setText("WAREHOUSE RECEIPTS");
		
		tableAllReceipts = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableAllReceipts.setBounds(10, 268, 430, 202);
		tableAllReceipts.setHeaderVisible(true);
		tableAllReceipts.setLinesVisible(true);
		
		TableColumn tblclmnClient = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnClient.setWidth(96);
		tblclmnClient.setText("client");
		
		TableColumn tblclmnCategory = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnCategory.setWidth(88);
		tblclmnCategory.setText("category");
		
		TableColumn tblclmnAmount = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnAmount.setWidth(85);
		tblclmnAmount.setText("amount");
		
		TableColumn tblclmnUnits = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnUnits.setWidth(86);
		tblclmnUnits.setText("units");
		
		TableColumn tblclmnDate = new TableColumn(tableAllReceipts, SWT.CENTER);
		tblclmnDate.setWidth(73);
		tblclmnDate.setText("date");
		
		Button btnVoidSelectedReceipt = new Button(shell, SWT.NONE);
		btnVoidSelectedReceipt.setBounds(10, 476, 187, 24);
		btnVoidSelectedReceipt.setText("Void Selected Receipt");
		
		Label lblLastUpdated = new Label(shell, SWT.NONE);
		Font updateLabelFont = new Font(lblLastUpdated.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
		lblLastUpdated.setFont(updateLabelFont);
		lblLastUpdated.setBounds(221, 476, 85, 24);
		lblLastUpdated.setText("Last Updated:");
		
		lblLastUpdatedTime = new Label(shell, SWT.NONE);
		lblLastUpdatedTime.setAlignment(SWT.RIGHT);
		Font updateTimeLabelFont = new Font(lblLastUpdatedTime.getDisplay(), new FontData(".AppleSystemUIFont", 12, SWT.NORMAL));
		lblLastUpdated.setFont(updateTimeLabelFont);
		lblLastUpdatedTime.setBounds(312, 476, 128, 24);
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
	    item.setText(new String[] { dataValueIndices[0], dataValueIndices[1], dataValueIndices[2], dataValueIndices[3], dataValueIndices[4]});
	    
	    // Update last updated time.
	    String currentTime = LocalDateTime.now().toString();
	    lblLastUpdatedTime.setText(currentTime);
	}
}
