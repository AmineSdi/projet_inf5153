package Controller;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import Model.Database.DataAccessObject;
import Model.PatientFile.MedicalHistory;
import Model.PatientFile.MedicalVisit;
import Model.PatientFile.PatientFile;
import Model.User.Doctor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * This class handles ''WHAT'' and will provide information
 */
public class AddHistoryController implements Initializable {
    //Private variables
    private Stage stage;
    private Scene scene;
    private Parent root;
    private DataAccessObject dataAccessObject;
    private Doctor doctor;
    private PatientFile patientFile;
    private MedicalVisit medicalVisit;
    private MedicalHistory medicalHistory;
    private Timeline automaticUpdate;

    @FXML
    private AnchorPane AnchorPane;
    //*************************//
    // FXML TextField variables//
    //*************************//
    @FXML
    private TextField tfDiagnosis;
    @FXML
    private TextField tfTreatment;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;


    //*************************//
    // FXML Label variables (error messages)//
    //*************************//
    @FXML
    private Label errorCompleteMH;
    @FXML
    private Label errorDate;

    //*************************//
    // FXML imageView variables (error messages)//
    //*************************//
    @FXML
    private ImageView errorCaution;


    //*********************//
    //Handle Button Methods//
    //*********************//
    @FXML
    public void handleBtnSaveMH(ActionEvent event) throws Exception {
        updateMedicalHistory();
        if(tfDiagnosis.getText().isEmpty()) {
            tfDiagnosis.setStyle("-fx-border-color: red");
        } else {
            tfDiagnosis.setStyle("-fx-border-color: #66adff");
        }
        if(tfTreatment.getText().isEmpty()) {
            tfTreatment.setStyle("-fx-border-color: red");
        } else {
            tfTreatment.setStyle("-fx-border-color: #66adff");
        }
        if (dpStartDate.getValue() == null) {
            dpStartDate.setStyle("-fx-border-color: red");
            dpEndDate.setStyle("-fx-border-color: red");
        } else {
            dpStartDate.setStyle("-fx-border-color: transparent");
            dpEndDate.setStyle("-fx-border-color: transaprent");
        }
        if (tfDiagnosis.getText().isEmpty() || tfTreatment.getText().isEmpty()
                || dpStartDate.getValue() == null) {
            errorDate.setVisible( false);
            errorCompleteMH.setVisible(true);
            errorCaution.setVisible(true);
        } else if (dpEndDate.getValue() != null &&
                   (!dpStartDate.getValue().isBefore(dpEndDate.getValue())
                    && !dpStartDate.getValue().isEqual(dpEndDate.getValue()))) {
            errorCompleteMH.setVisible(false);
            errorDate.setVisible(true);
            errorCaution.setVisible(true);
            dpStartDate.setStyle("-fx-border-color: red");
            dpEndDate.setStyle("-fx-border-color: red");
        }
        else {
            goToSearchResultsPage(event);
        }
    }
    @FXML
    public void handleBtnCancelAddMH(ActionEvent event) throws Exception {
        // pop-up window.
        if (confirmClear()) {
            this.medicalHistory = null;
            goToSearchResultsPage(event);
        }
    }

    //**************//
    //Public Methods//
    //**************//
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setDateFormat(dpEndDate);
        setDateFormat(dpStartDate);
        dpStartDate.getEditor().setDisable(true);
        dpEndDate.getEditor().setDisable(true);
    }

    public void setResources(Doctor doctor, PatientFile patientFile, MedicalVisit medicalVisit,
                             MedicalHistory medicalHistory, DataAccessObject dataAccessObject) {
        this.doctor = doctor;
        this.patientFile = patientFile;
        this.medicalVisit = medicalVisit;
        this.medicalHistory = medicalHistory;
        this.dataAccessObject = dataAccessObject;
        if(medicalHistory != null) {
            tfDiagnosis.setText(medicalHistory.getDiagnosis());
            tfTreatment.setText(medicalHistory.getTreatment());
            dpStartDate.setValue(medicalHistory.getStartDate());
            dpEndDate.setValue(medicalHistory.getEndDate());
        } else {
            dpStartDate.setValue(LocalDate.now());
        }
        setAllowedDates();
        automaticUpdate = new Timeline(
            new KeyFrame(Duration.seconds(3),
                         event -> updateMedicalHistory()));
        automaticUpdate.setCycleCount(Timeline.INDEFINITE);
        automaticUpdate.play();
    }

    //***************//
    //Private Methods//
    //***************//
    /**
     * This function creates a pop-up box asking if the user is certain of wanting to clear the
     * local changes to the medical history.
     * @return A boolean.
     */
    private boolean confirmClear() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Medical program");
        alert.setHeaderText("Erase all local changes to this medical history?");
        alert.setContentText("Click OK to erase, click cancel to return.");
        if(alert.showAndWait().get() == ButtonType.OK) {
            return true;
        }
        return false;
    }

    /**
     * Updates the local MedicalHistory Object
     */
    private void updateMedicalHistory() {
        medicalHistory = new MedicalHistory();
        doctor.setHistoryDiagnosis(tfDiagnosis.getText());
        doctor.setHistoryTreatment(tfTreatment.getText());
        doctor.setHistoryStartDate(dpStartDate.getValue());
        doctor.setHistoryEndDate(dpEndDate.getValue());
        medicalHistory.accept(doctor);
    }

    private void setAllowedDates() {
        LocalDate maxDate = LocalDate.now();
        dpStartDate.setDayCellFactory(d ->
        new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isAfter(maxDate));
            }
        });
        dpEndDate.setDayCellFactory(d ->
        new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
            }
        });
    }

    /**
     * Ensures that the displayed format of the date is yyyy-MM-dd.
     */
    private void setDateFormat(DatePicker date) {
        date.setConverter(new StringConverter<LocalDate>() {
            String format = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
            {
                date.setPromptText(format.toLowerCase());
            }
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * Goes to the search results page, passing any required data.
     *
     * @param event The ActionEvent
     * @throws IOException
     */
    private void goToSearchResultsPage(ActionEvent event) throws IOException {
        automaticUpdate.stop();
        automaticUpdate = null;
        FXMLLoader loader = new FXMLLoader(getClass()
                                           .getResource("/Application/searchResults.fxml"));
        root = loader.load();
        SearchResultsController searchResultsController = loader.getController();
        searchResultsController.setResources(doctor, patientFile, medicalVisit,
                                             medicalHistory, dataAccessObject);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Search Results");
        stage.show();
    }
}
