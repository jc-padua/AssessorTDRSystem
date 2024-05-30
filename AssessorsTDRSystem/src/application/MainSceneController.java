package application;

import java.sql.Connection;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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
	private Label lblFILEPATH;
	@FXML
	private Button btnUPLOAD;
	@FXML
	private Button btnUPDATE;
	@FXML
	private Button btnADD;
	@FXML
	private Button btnDELETE;
	@FXML
	private TableView<td_data> TDTaxDec;
	@FXML
	private TableView<atd_data> ATDTaxDec;
	@FXML
	private TableColumn<td_data, String> pinColumn;
	@FXML
    private TableColumn<td_data, String> seriesColumn;
    @FXML
    private TableColumn<td_data, String> ownerColumn;
    @FXML
    private TableColumn<td_data, String> locationColumn;
    @FXML
    private TableColumn<td_data, String> fileColumn;
    @FXML
	private TableColumn<atd_data, String> apinColumn;
	@FXML
    private TableColumn<atd_data, String> aseriesColumn;
    @FXML
    private TableColumn<atd_data, String> aownerColumn;
    @FXML
    private TableColumn<atd_data, String> alocationColumn;
    @FXML
    private TextField tfSEARCH;
    
    @FXML
    private ImageView pdfImageView;
    
    private byte[] fileData;
    
    private ObservableList<td_data> taxDecList;
    
    private ObservableList<atd_data> ataxDecList;
    
    
    
    public MainSceneController() {
    	taxDecList = FXCollections.observableArrayList();
    	ataxDecList = FXCollections.observableArrayList();
    }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	pinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPin()));
        seriesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnumber()));
        ownerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner()));
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        
        fileColumn.setCellValueFactory(cellData -> {
            byte[] fileData = cellData.getValue().getFileData();
            return new SimpleStringProperty(fileData != null ? "File Present" : "No File");
        });
        
        apinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPin()));
        aseriesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnumber()));
        aownerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner()));
        alocationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        
        
        TDTaxDec.setItems(taxDecList);
        ATDTaxDec.setItems(ataxDecList);
        
        loadDataFromDatabase();
        loadArchiveDataFromDatabase();
        contextMenu();
        checkDB();
        setupSearch();
        
        btnUPDATE.setDisable(true);
       
        
    } 
     
    private void populateTextFields(td_data data) {
        tfPIN.setText(data.getPin());
        tfSNUMBER.setText(data.getSnumber());
        tfOWNER.setText(data.getOwner());
        tfLOCATION.setText(data.getLocation());
        Image pdfImage = renderPdfToImage(data.getFileData());
        pdfImageView.setImage(pdfImage);
    }

    
    @FXML
    void updateData(ActionEvent event) {
        String pin = tfPIN.getText();
        String snumber = tfSNUMBER.getText();
        String owner = tfOWNER.getText();
        String location = tfLOCATION.getText();

        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
            pst = con.prepareStatement("UPDATE taxDec SET series_number = ?, owner = ?, location = ? WHERE pin = ?");
            pst.setString(1, snumber);
            pst.setString(2, owner);
            pst.setString(3, location);
            pst.setString(4, pin);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Updated!");
                alert.show();

                loadDataFromDatabase();
                clearInputFields();
                btnUPDATE.setDisable(true);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showErrorAlert("Error occurred while updating data!");
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

    private void setupSearch () {
    	FilteredList<td_data> filteredData = new FilteredList<>(taxDecList, b -> true);
    	FilteredList<atd_data> afilteredData = new FilteredList<>(ataxDecList, b -> true);
    		
    	tfSEARCH.textProperty().addListener((observable, oldValue, newValue) -> {
    		filteredData.setPredicate(td_data -> {
    			if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (td_data.getPin().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (td_data.getSnumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (td_data.getOwner().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (td_data.getLocation().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    	
    	tfSEARCH.textProperty().addListener((observable, oldValue, newValue) -> {
    		afilteredData.setPredicate(atd_data -> {
    			if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (atd_data.getPin().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (atd_data.getSnumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (atd_data.getOwner().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (atd_data.getLocation().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        
        SortedList<td_data> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TDTaxDec.comparatorProperty());
        TDTaxDec.setItems(sortedData);
        
        SortedList<atd_data> asortedData = new SortedList<>(afilteredData);
        asortedData.comparatorProperty().bind(ATDTaxDec.comparatorProperty());
        ATDTaxDec.setItems(asortedData);
    }

	public void contextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuDelete = new MenuItem("Delete");
		MenuItem menuArchive = new MenuItem("Archive");
		contextMenu.getItems().addAll(menuDelete, menuArchive);
		
		
		TDTaxDec.setRowFactory(tv -> {
            TableRow<td_data> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
                    td_data rowData = row.getItem();
                    if (rowData != null) {
                        System.out.println("Selected item: " + rowData);
                    }
                } else if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                	td_data rowData = row.getItem();
                    if (rowData != null) {
                        populateTextFields(rowData);
                        btnUPDATE.setDisable(false);
                    }
                }
            });
            
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
		
		menuDelete.setOnAction(event -> {
			td_data selectedItem = TDTaxDec.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				TDTaxDec.getItems().remove(selectedItem);
				deleteFromDatabase(selectedItem);
			}
		});
		
		menuArchive.setOnAction(event -> {
			td_data selectedItem = TDTaxDec.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				archiveItem(selectedItem);
				
			}
		});
		
		
		ContextMenu acontextMenu = new ContextMenu();
		MenuItem amenuArchive = new MenuItem("Restore");
		acontextMenu.getItems().addAll(amenuArchive);
		
		ATDTaxDec.setRowFactory(tv -> {
            TableRow<atd_data> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
                    atd_data rowData = row.getItem();
                    if (rowData != null) {
                        System.out.println("Selected item: " + rowData);
                    }
                }
            });
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(acontextMenu)
            );
            return row;
        });
		
		amenuArchive.setOnAction(event -> {
			atd_data selectedItem = ATDTaxDec.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				restoreItem(selectedItem);
			}
		});
		
	}

	private void archiveItem(td_data selectedItem) {
		try {
	        Class.forName("org.sqlite.JDBC");
	        con = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
	        pst = con.prepareStatement("UPDATE taxDec SET archive = 1 WHERE pin = ?");
	        pst.setString(1, selectedItem.getPin());	
	        pst.executeUpdate();
	        
	        taxDecList.remove(selectedItem);
	        loadArchiveDataFromDatabase();
	        
	        System.out.println("Successfuly Modified");
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	        showErrorAlert("Error deleting data from the database");
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

	private void restoreItem(atd_data selectedItem) {
		try {
	        Class.forName("org.sqlite.JDBC");
	        con = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
	        pst = con.prepareStatement("UPDATE taxDec SET archive = 0 WHERE pin = ?");
	        pst.setString(1, selectedItem.getPin());	
	        pst.executeUpdate();
	        
	        ataxDecList.remove(selectedItem);
	        loadDataFromDatabase();

	        System.out.println("Successfuly Modified");
	        
	        setupSearch();
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	        showErrorAlert("Error deleting data from the database");
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
	
	private void deleteFromDatabase(td_data selectedItem) {
		try {
	        Class.forName("org.sqlite.JDBC");
	        con = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
	        pst = con.prepareStatement("DELETE FROM taxDec WHERE pin = ?");
	        pst.setString(1, selectedItem.getPin());
	        pst.executeUpdate();
	        System.out.println(selectedItem.getPin());
	        
	        loadDataFromDatabase();
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	        showErrorAlert("Error deleting data from the database");
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
	
	private void loadDataFromDatabase() {
	    taxDecList.clear();
	    try {
	        Class.forName("org.sqlite.JDBC");
	        con = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
	        Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM taxDec WHERE archive = 0");

	        while (rs.next()) {
	            String pin = rs.getString("pin");
	            String series = rs.getString("series_number");
	            String owner = rs.getString("owner");
	            String location = rs.getString("location");
	            Boolean archive = rs.getBoolean("archive");
	            byte[] fileData = rs.getBytes("file_data");

	            td_data taxData = new td_data(pin, series, owner, location, archive, fileData);
	            taxDecList.add(taxData);
	        }

	        TDTaxDec.setItems(taxDecList);
	        rs.close();
	        stmt.close();
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	        showErrorAlert("Error loading data from the database");
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	}

    
    private void loadArchiveDataFromDatabase() {
    	ataxDecList.clear();
        try {
        	Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM taxDec WHERE archive = 1");

            while (rs.next()) {
                String pin = rs.getString("pin");
                String series = rs.getString("series_number");
                String owner = rs.getString("owner");
                String location = rs.getString("location");
                Boolean archive = rs.getBoolean("archive");

                atd_data taxData = new atd_data(pin, series, owner, location, archive);
                ataxDecList.add(taxData);
            }
            
           
            ATDTaxDec.setItems(ataxDecList);
            rs.close();
            stmt.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showErrorAlert("Error loading data from the database");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    
    @FXML
    void uploadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileData = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                lblFILEPATH.setText(selectedFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Error occurred while reading the file!");
            }
        }
    }

    private Image renderPdfToImage(byte[] fileData) {
        if (fileData == null || fileData.length == 0) {
            return null;
        }

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileData))) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 300, ImageType.RGB);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error rendering PDF to image");
            return null;
        }
    }
    
    



    @FXML
    void addData(ActionEvent event) {
        String pin = tfPIN.getText();
        String snumber = tfSNUMBER.getText();
        String owner = tfOWNER.getText();
        String location = tfLOCATION.getText();
        Boolean archive = false;

        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
            pst = con.prepareStatement("INSERT INTO taxDec (pin, series_number, owner, location, archive, file_data) VALUES (?, ?, ?, ?, ?, ?)");
            pst.setString(1, pin);
            pst.setString(2, snumber);
            pst.setString(3, owner);
            pst.setString(4, location);
            pst.setBoolean(5, archive);
            pst.setBytes(6, fileData); // Bind fileData to the statement

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Added!");
                alert.show();

                td_data newTaxData = new td_data(pin, snumber, owner, location, archive, fileData);
                taxDecList.add(newTaxData);
                TDTaxDec.setItems(taxDecList);
                
                // Update search functionality
                setupSearch();
                
                clearInputFields();
                lblFILEPATH.setText(""); // Clear the file path label after successful upload
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



    
	public void clearInputFields() {
		tfPIN.clear();
        tfSNUMBER.clear();
        tfOWNER.clear();
        tfLOCATION.clear();
        pdfImageView.setImage(null);
	}
	
	public void checkDB() {
	    try {
	        Class.forName("org.sqlite.JDBC");
	        con = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
	        System.out.println("Connected to the database");
	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	        showErrorAlert("Error connecting to the database");
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	}

	  private void closeConnection() {
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
