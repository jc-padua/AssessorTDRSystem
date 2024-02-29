package application;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
	    void addData(ActionEvent event) {
		 
		 String pin = tfPIN.getText();
		 String snumber = tfSNUMBER.getText();
		 String owner = tfOWNER.getText();
		 String location = tfLOCATION.getText();
		 
		 Alert alert = new Alert(Alert.AlertType.ERROR);
		 try {
			 if (pin.isEmpty() && snumber.isEmpty() && owner.isEmpty() && location.isEmpty()) {
				 alert.setTitle("Input Validation");
				 alert.setHeaderText("Please fill the input field");
				 alert.show();
				 return;
			 } else {
				 System.out.println(pin);
				 System.out.println(snumber);
				 System.out.println(owner);
				 System.out.println(location);
			 }
			
		} catch (Exception e) {
			System.out.println(e);
		}
	    }
	
}
