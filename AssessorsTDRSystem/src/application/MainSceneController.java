package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import javafx.scene.control.DatePicker;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MainSceneController {
	
	
	Connection con;
	PreparedStatement pst;
	
	@FXML
	private TextField tfPIN;
	@FXML
	private TextField tfSNUMBER;
	@FXML
	private TextField tfOWNER;
	@FXML
	private TextField tfLOCATION;
	@FXML
	private Button btnUPLOAD;
	@FXML
	private Button btnADD;
	@FXML
	private DatePicker dpDATE;

	 @FXML
	    void addData(ActionEvent event) {
		 
		 String pin = tfPIN.getText();
		 String snumber = tfSNUMBER.getText();
		 String owner = tfOWNER.getText();
		 String location = tfLOCATION.getText();
		 
		 try {
//			 if (pin.isEmpty() && snumber.isEmpty() && owner.isEmpty() && location.isEmpty()) {
//				 Alert alert = new Alert(Alert.AlertType.ERROR);
//				 alert.setTitle("Input Validation");
//				 alert.setHeaderText("Please fill the input field");
//				 alert.show();
//				 return;
//			 } else {
//				 Alert alert = new Alert(Alert.AlertType.INFORMATION);
//				 alert.setTitle("Sample");
//				 alert.setHeaderText("Sample");
//				 alert.setContentText(pin + "\n" + snumber + "\n" + owner + "\n" + location);
//				 alert.show();
//				 return;
//			 }
			 
			 Class.forName("com.mysql.cj.jdbc.Driver");
			 con = DriverManager.getConnection("jdbc:mysql://localhost/assessor_db", "root", "admin");
			 pst = con.prepareStatement("INSERT INTO tax_dec (pin,serial_number,owner,location) VALUES (?,?,?,?) ");
			 pst.setString(1, pin);
			 pst.setString(2, snumber);
			 pst.setString(3, owner);
			 pst.setString(4, location);
			 
			 int affectedRows = pst.executeUpdate();
			 if (affectedRows > 0) {
				 JOptionPane.showMessageDialog(null, "Successfully Added!");
			 }
			
		 } catch (SQLException e) {
			e.printStackTrace();
		 } catch (ClassNotFoundException e) { 
			 e.printStackTrace();
		 }
	}
	
}
