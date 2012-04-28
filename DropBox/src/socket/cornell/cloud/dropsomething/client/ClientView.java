package cornell.cloud.dropsomething.client;


import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;


public class ClientView {

	protected Shell shell;
	private Text uNameTxt;
	private Text pwdtext;
	private Label dirPathLbl;
	ClientHandler handler = null;
	private Group group;
	private Button existingRdBtn;
	private Button newUserRdBtn;
	String selectedDir;
	
	public ClientView(ClientHandler ch){
		this.handler = ch;
	}
	public ClientView(){
		
	}
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ClientView window = new ClientView();
			
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
		shell.setText("Drop Something!!");
		
		Button synchBtn = new Button(shell, SWT.NONE);
		synchBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(handler != null){
					System.out
							.println("ClientView.open().new SelectionAdapter() {...}.widgetSelected()");
					startSynch();
				}
			}
		});
		synchBtn.setBounds(248, 219, 94, 28);
		synchBtn.setText("Synch");
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	private void startSynch(){
		if(existingRdBtn.getSelection()){
			System.out.println("ClientView.startSynch() Exiting");
			if(validateEntry()){
				handler.authenticateUser();
			}
		}else if(newUserRdBtn.getSelection()){
			System.out.println("ClientView.startSynch() New");
			handler.createNewUser();
		}else{
			System.out.println("ClientView.createContents() Select one");
		}
	}
	private boolean validateEntry(){
		return getClientName() != null && getPassword() != null && getDirectoryPath() != null;
				
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(663, 300);
		shell.setText("SWT Application");
		
		dirPathLbl = new Label(shell, SWT.BORDER);
		dirPathLbl.setFont(SWTResourceManager.getFont("Consolas", 13, SWT.BOLD));
		dirPathLbl.setBounds(24, 157, 512, 28);
		dirPathLbl.setText("Please select a directory");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(20, 13, 123, 28);
		lblNewLabel.setText("User Name");
		
		uNameTxt = new Text(shell, SWT.BORDER);
		uNameTxt.setBounds(149, 10, 196, 31);
		
		Label lblPassword = new Label(shell, SWT.NONE);
		lblPassword.setBounds(20, 64, 82, 19);
		lblPassword.setText("Password");
		
		pwdtext = new Text(shell, SWT.BORDER);
		pwdtext.setBounds(149, 61, 193, 28);
		
		DragSource dragSource = new DragSource(shell, DND.DROP_MOVE);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				 DirectoryDialog directoryDialog = new DirectoryDialog(shell);
				 directoryDialog.setFilterPath("/users/ankitsingh/");
			        directoryDialog.setMessage("Please select a directory and click OK");
			        String dir = directoryDialog.open();
			        if(dir != null) {
			         System.out
							.println("Selected dir: " + dir);
			          selectedDir = dir;
			          dirPathLbl.setText("Current Path: "+dir);
			        }
			}
		});
		btnNewButton.setBounds(542, 157, 94, 28);
		btnNewButton.setText("Browse");
		
		group = new Group(shell, SWT.NONE);
		group.setBounds(120, 95, 324, 43);
		
		existingRdBtn = new Button(group, SWT.RADIO);
		existingRdBtn.setBounds(10, 10, 141, 18);
		existingRdBtn.setText("Registered user");
		
		newUserRdBtn = new Button(group, SWT.RADIO);
		newUserRdBtn.setBounds(168, 10, 91, 18);
		newUserRdBtn.setText("New User");

	}
	public String getClientName() {
		return uNameTxt.getText();
	}
	public String getPassword() {
			return pwdtext.getText();
	}
	public String getDirectoryPath() {
		System.out.println("ClientView.getDirectoryPath()"+selectedDir);
		return selectedDir;
	}
}

