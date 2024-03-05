package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.scene.control.DatePicker;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainSceneController implements Initializable {
	
	
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
	private TableView<td_data> TDTaxDec;
	@FXML
	private TableColumn<td_data, String> pinColumn;
	@FXML
    private TableColumn<td_data, String> seriesColumn;
    @FXML
    private TableColumn<td_data, String> ownerColumn;
    @FXML
    private TableColumn<td_data, String> locationColumn;

    private ObservableList<td_data> taxDecList;
    
    public MainSceneController() {
    	taxDecList = FXCollections.observableArrayList();
    }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	pinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPin()));
        seriesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnumber()));
        ownerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner()));
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        
        TDTaxDec.setItems(taxDecList);
       
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/assessor_db", "root", "admin");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tax_dec");

            while (rs.next()) {
                String pin = rs.getString("pin");
                String series = rs.getString("series_number");
                String owner = rs.getString("owner");
                String location = rs.getString("location");

                td_data taxData = new td_data(pin, series, owner, location);
                taxDecList.add(taxData);
            }

            rs.close();
            stmt.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showErrorAlert("Error loading data from the database");
        } finally {
            // Close resources
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    
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
			 pst = con.prepareStatement("INSERT INTO tax_dec (pin,series_number,owner,location) VALUES (?,?,?,?) ");
			 pst.setString(1, pin);
			 pst.setString(2, snumber);
			 pst.setString(3, owner);
			 pst.setString(4, location);
			 
			 int affectedRows = pst.executeUpdate();
			 if (affectedRows > 0) {
				 Alert alert = new Alert(Alert.AlertType.INFORMATION);
	             alert.setTitle("Success");
	             alert.setHeaderText(null);
	             alert.setContentText("Successfully Added!");
	             alert.show();
	             
	             td_data newTaxData = new td_data(pin, snumber, owner, location);
	             taxDecList.add(newTaxData);
	             TDTaxDec.setItems(taxDecList);
	             
	             clearInputFields();
			 }
			 
			
		 } catch (SQLException e) {
			e.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText(null);
	        alert.setContentText("Error occurred while adding data!");
	        alert.show();
		 } catch (ClassNotFoundException e) { 
			 e.printStackTrace();
		 } finally {
			 try {
				 if (pst != null) {
					 pst.close();
				 }
				 if (con != null) {
					 con.close();
				 }
			 } catch (SQLException ex) {
				 ex.printStackTrace();
			 }
		 }
	}
	private void clearInputFields() {
		// TODO Auto-generated method stub
		tfPIN.clear();
        tfSNUMBER.clear();
        tfOWNER.clear();
        tfLOCATION.clear();
	}
	
}
