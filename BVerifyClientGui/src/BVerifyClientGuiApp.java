import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.custom.TableCursor;

public class BVerifyClientGuiApp {

	protected Shell shell;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text txtPubkey;
	private Text txtUnits;
	private Table table;
	private TableItem tableItem_1;
	private Button btnGetBalancesButton;
	private Label lblLastUpdateTextLabel;
	private Label lblLastUpdateDetailsLabel;
	private Label lblAllUserBalances;
	private Label lblUserConfigInfo;
	private Label lblUserPublicKeyLabel;
	private Label lblUserBalanceLabel;
	private Label lblUserPublicKey;
	private Label lblUserBalance;
	private Label lblOutgoingTransferlabel;
	private Table table_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BVerifyClientGuiApp window = new BVerifyClientGuiApp();
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
		shell.setSize(500, 600);
		shell.setText("B_Verify Client Application");
		
		Label lblRecipient = new Label(shell, SWT.NONE);
		lblRecipient.setBounds(67, 125, 139, 19);
		formToolkit.adapt(lblRecipient, true, true);
		lblRecipient.setText("Recipient (PubKey):");
		
		Label lblAmount = new Label(shell, SWT.NONE);
		lblAmount.setBounds(67, 150, 139, 18);
		formToolkit.adapt(lblAmount, true, true);
		lblAmount.setText("Transfer Amount (Units):");
		
		txtPubkey = new Text(shell, SWT.BORDER);
		txtPubkey.setText("recipientpublickey");
		txtPubkey.setBounds(212, 125, 226, 19);
		formToolkit.adapt(txtPubkey, true, true);
		
		txtUnits = new Text(shell, SWT.BORDER);
		txtUnits.setText("1000");
		txtUnits.setBounds(212, 149, 226, 19);
		formToolkit.adapt(txtUnits, true, true);
		
		Button btnTransferButton = new Button(shell, SWT.NONE);
		btnTransferButton.setBounds(67, 174, 371, 19);
		formToolkit.adapt(btnTransferButton, true, true);
		btnTransferButton.setText("Request Transfer");
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(67, 365, 371, 153);
		formToolkit.adapt(table);
		formToolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tableItem_1 = new TableItem(table, SWT.NONE);
		tableItem_1.setText(new String[] {});
		tableItem_1.setText("anotheruserpublickey");
		
		TableColumn tblclmnUsers = new TableColumn(table, SWT.NONE);
		tblclmnUsers.setWidth(263);
		tblclmnUsers.setText("User Public Key");
		
		TableColumn tblclmnBalance = new TableColumn(table, SWT.NONE);
		tblclmnBalance.setWidth(106);
		tblclmnBalance.setText("Balance");
		
		TableCursor tableCursor = new TableCursor(table, SWT.NONE);
		formToolkit.adapt(tableCursor);
		formToolkit.paintBordersFor(tableCursor);
		
		btnGetBalancesButton = new Button(shell, SWT.NONE);
		btnGetBalancesButton.setBounds(67, 549, 371, 19);
		formToolkit.adapt(btnGetBalancesButton, true, true);
		btnGetBalancesButton.setText("Update Balances");
		
		lblLastUpdateTextLabel = new Label(shell, SWT.NONE);
		lblLastUpdateTextLabel.setBounds(67, 524, 139, 19);
		formToolkit.adapt(lblLastUpdateTextLabel, true, true);
		lblLastUpdateTextLabel.setText("Last Verified Update:");
		
		lblLastUpdateDetailsLabel = new Label(shell, SWT.NONE);
		lblLastUpdateDetailsLabel.setBounds(212, 524, 226, 19);
		formToolkit.adapt(lblLastUpdateDetailsLabel, true, true);
		lblLastUpdateDetailsLabel.setText("March 18, 2018 3:00:00 PM EST");
		
		lblAllUserBalances = new Label(shell, SWT.NONE);
		lblAllUserBalances.setAlignment(SWT.CENTER);
		lblAllUserBalances.setBounds(67, 341, 371, 18);
		formToolkit.adapt(lblAllUserBalances, true, true);
		lblAllUserBalances.setText("All User Balances");
		
		lblUserConfigInfo = new Label(shell, SWT.NONE);
		lblUserConfigInfo.setAlignment(SWT.CENTER);
		lblUserConfigInfo.setBounds(67, 10, 371, 19);
		formToolkit.adapt(lblUserConfigInfo, true, true);
		lblUserConfigInfo.setText("User Configuration Information");
		
		lblUserPublicKeyLabel = new Label(shell, SWT.NONE);
		lblUserPublicKeyLabel.setBounds(67, 35, 139, 19);
		formToolkit.adapt(lblUserPublicKeyLabel, true, true);
		lblUserPublicKeyLabel.setText("User Public Key:");
		
		lblUserBalanceLabel = new Label(shell, SWT.NONE);
		lblUserBalanceLabel.setBounds(67, 60, 139, 19);
		formToolkit.adapt(lblUserBalanceLabel, true, true);
		lblUserBalanceLabel.setText("User Balance (Units):");
		
		lblUserPublicKey = new Label(shell, SWT.NONE);
		lblUserPublicKey.setBounds(212, 35, 226, 19);
		formToolkit.adapt(lblUserPublicKey, true, true);
		lblUserPublicKey.setText("userpublickey");
		
		lblUserBalance = new Label(shell, SWT.NONE);
		lblUserBalance.setBounds(212, 60, 226, 19);
		formToolkit.adapt(lblUserBalance, true, true);
		lblUserBalance.setText("1000");
		
		lblOutgoingTransferlabel = new Label(shell, SWT.NONE);
		lblOutgoingTransferlabel.setAlignment(SWT.CENTER);
		lblOutgoingTransferlabel.setBounds(67, 100, 371, 19);
		formToolkit.adapt(lblOutgoingTransferlabel, true, true);
		lblOutgoingTransferlabel.setText("Outgoing Transfer Information");
		
		Label lblIncomingTransferLabel = new Label(shell, SWT.NONE);
		lblIncomingTransferLabel.setAlignment(SWT.CENTER);
		lblIncomingTransferLabel.setBounds(67, 210, 371, 19);
		formToolkit.adapt(lblIncomingTransferLabel, true, true);
		lblIncomingTransferLabel.setText("Incoming Transfer Information");
		
		table_1 = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBounds(67, 235, 371, 88);
		formToolkit.adapt(table_1);
		formToolkit.paintBordersFor(table_1);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		
		TableColumn tblclmnSender = new TableColumn(table_1, SWT.NONE);
		tblclmnSender.setWidth(219);
		tblclmnSender.setText("Sender Public Key");
		
		TableColumn tblclmnAmountReceived = new TableColumn(table_1, SWT.NONE);
		tblclmnAmountReceived.setWidth(91);
		tblclmnAmountReceived.setText("Amount (Units)");
		
		TableColumn tblclmnConfirm = new TableColumn(table_1, SWT.NONE);
		tblclmnConfirm.setWidth(59);
		tblclmnConfirm.setText("Confirm?");
		
		TableItem tableItemSender = new TableItem(table_1, SWT.NONE);
		tableItemSender.setText("senderpublickey");
		
		Button button = new Button(shell, SWT.NONE);
		button.setBounds(98, 258, 94, 28);
		formToolkit.adapt(button, true, true);
		button.setText("New Button");
	}
}
