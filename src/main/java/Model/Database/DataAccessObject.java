package Model.Database;
import Model.ContactInformation.ContactInformation;
import Model.ContactInformation.MedicalEstablishment;
import Model.PatientFile.Gender;
import Model.PatientFile.MedicalHistory;
import Model.PatientFile.MedicalVisit;
import Model.PatientFile.PatientFile;
import Model.User.Doctor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class handles all queries done to the database.
 * It also handles the singleton DBConnection
 */
public class DataAccessObject {

    public DataAccessObject(String databasePath) {
        this.databasePath = databasePath;
    }

    private String databasePath;

    //**************//
    //Public Methods//
    //**************//
    /**
     * This method adds to the MedicalVisits table a Medical visit
     * with the following informations
     * @param ramqCode
     * @param medicalVisit
     */
    public void addMedicalVisit(String ramqCode, MedicalVisit medicalVisit) {
        int doctorLicense = medicalVisit.getDoctorLicense();
        String visitDate = medicalVisit.getVisitDate().toString();
        String diagnosis = medicalVisit.getDiagnosis();
        String treatment = medicalVisit.getTreatment();
        String summary = medicalVisit.getSummary();
        String notes = medicalVisit.getNotes();
        String query =  "INSERT INTO MedicalVisits(patientRamqCode," +
                        " doctorLicense, visitDate, diagnosis, treatment, summary, notes) " +
                        " VALUES ('" + ramqCode + "','" +
                        doctorLicense +  "','" + visitDate + "','" + diagnosis +  "','"
                        + treatment + "','" + summary + "','" + notes +"')";
        executeQuery(query);
    }


    /**
     * This methods adds a MedicalHistory to the MedicalHistories table.
     *
     * @param ramqCode
     * @param medicalHistory
     */
    public void addMedicalHistory(String ramqCode, MedicalHistory medicalHistory) {
        int doctorLicense = medicalHistory.getDoctorLicense();
        String diagnosis = medicalHistory.getDiagnosis();
        String treatment = medicalHistory.getTreatment();
        LocalDate startDate = medicalHistory.getStartDate();
        LocalDate endDate = medicalHistory.getEndDate();
        String query =  "INSERT INTO MedicalHistories(patientRamqCode, doctorLicense," +
                        "diagnosis, treatment, startDate, endDate) VALUES ('" +
                        ramqCode + "','" + doctorLicense +  "','" + diagnosis + "','" +
                        treatment +  "','" + startDate;
        if (endDate != null)
            query = query + "','" + endDate + "')";
        else
            query = query + "', NULL)";
        executeQuery(query);
    }

    /**
     * Gets a startDate of a specific medical history where the end date is null in the database.
     * @param ramqCode The RAMQ code of the patient.
     * @param doctorLicense The doctor's license
     * @param diagnosis The Diagnosis
     * @return The Start Date
     */
    public LocalDate getStartDate(String ramqCode, int doctorLicense, String diagnosis) {
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        Statement statement;
        ResultSet resultSet = null;
        String startDate = null;
        try {
            String queryEstablishmentName = "SELECT startDate FROM MedicalHistories" +
                                            " WHERE patientRamqCode = '" + ramqCode +
                                            "' AND doctorLicense = " +
                                            doctorLicense + " AND diagnosis = '" + diagnosis +
                                            "' AND endDate IS NULL";
            statement = conn.createStatement();
            resultSet = statement.executeQuery(queryEstablishmentName);
            startDate = resultSet.getString("startDate");
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        if (startDate != null)
            return LocalDate.parse(startDate);
        else
            return null;
    }

    /**
     * This method will update the end date for a medical history
     * which end date has initially been left null.
     * @param ramqCode
     * @param doctorLicense
     * @param diagnosis
     * @param startDate
     * @param endDate
     */
    public void updateEndDate(String ramqCode, int doctorLicense, String diagnosis,
                              LocalDate startDate, LocalDate endDate) {
        String query = "UPDATE MedicalHistories SET endDate = '" +
                       endDate + "' WHERE patientRamqCode = '" +
                       ramqCode + "' AND doctorLicense = " + doctorLicense +
                       " AND diagnosis = '" + diagnosis +
                       "' AND startDate = '" + startDate + "' AND endDate IS NULL";
        executeQuery(query);
    }
    /**
     * Checks if the patient RAMQ code exists in the database.
     * @param ramqCode The patient RAMQ code
     * @return true if it exists, false otherwise.
     */
    public boolean patientExistsInDB(String ramqCode) {
        String query = "SELECT * FROM PatientFiles WHERE ramqCode = \""
                       + ramqCode + "\" LIMIT 1";
        boolean isFound = false;
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            isFound = resultSet.isBeforeFirst();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return isFound;
    }

    /**
     * This method looks in the database for the patient file
     * of a single patient
     * @param ramqCode
     * @return
     */
    public HashMap<String, String> getPatientFileInfoFromDB(String ramqCode) {
        String userQuery = "SELECT * FROM PatientFiles WHERE ramqCode = \""
                           + ramqCode + "\" LIMIT 1";
        boolean isFound = false;
        HashMap<String, String> result = null;
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(userQuery);
            isFound = resultSet.isBeforeFirst();
            if (isFound) {
                result = new HashMap<String, String>();
                result.put("ramqCode", resultSet.getString("ramqCode"));
                result.put("firstName", resultSet.getString("firstName"));
                result.put("lastName", resultSet.getString("lastName"));
                result.put("gender", resultSet.getString("gender"));
                result.put("birthCity", resultSet.getString("birthCity"));
                result.put("birthDate", resultSet.getString("birthDate"));
                result.put("parentsName", resultSet.getString("parentsName"));
                result.put("contactInfoId", resultSet.getString("contactInfoId"));
            }
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Searches the database for a username and password.
     * @param username The username
     * @param password The password
     * @return The doctor associated with that username and password.
     */
    public Doctor findUsernameAndPassword(String username, String password) {
        String userQuery = "SELECT * FROM Users WHERE username = \""
                           + username + "\" AND password = \"" + password + "\"";
        boolean isFound = false;
        Doctor doctor = null;
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(userQuery);
            isFound = resultSet.isBeforeFirst();
            if (isFound) {
                int userId = resultSet.getInt("id");
                doctor = getDoctor(userId, resultSet, conn);
            }
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return doctor;
    }

    /**
     * This method makes a query to the database in order to get all medical
     * visits of a patient file.
     * @return
     */
    public List<MedicalVisit> getMedicalVisits(String ramqCode) {
        List<MedicalVisit> medicalVisits; //
        String query = "SELECT * FROM MedicalVisits WHERE patientRamqCode = \"" + ramqCode + "\"";
        medicalVisits = getMedicalVisitDB(query);
        return medicalVisits;
    }

    /**
     * This method makes a query to the database in order to get all
     * medical histories of a patient file.
     * @return
     */
    public List<MedicalHistory> getMedicalHistories(String ramqCode) {
        List<MedicalHistory> medicalHistories;
        String query = "SELECT * FROM MedicalHistories WHERE patientRamqCode = \"" + ramqCode + "\"";
        medicalHistories = getMedicalHistoryDB(query);
        return medicalHistories;
    }

    /**
     * This method returns an ObservableList of MedicalVisit that is
     * necessary for showing the patient's visits in the front-end
     * @param ramqCode
     * @return medicalVisits
     */
    public ObservableList<MedicalVisit> getObservableVisitsList(String ramqCode) {
        ObservableList<MedicalVisit> medicalVisits = FXCollections.observableArrayList();
        String query = "SELECT * FROM MedicalVisits WHERE patientRamqCode = \"" + ramqCode + "\"";
        medicalVisits = getObservableVisitsDB(query);
        return medicalVisits;
    }
    /**
     * This method returns an ObservableList of MedicalHistory that is
     * necessary for showing the patient's history in the front-end
     */
    public ObservableList<MedicalHistory> getObservableHistoryList(String ramqCode) {
        ObservableList<MedicalHistory> medicalHistory = FXCollections.observableArrayList();
        String query = "SELECT * FROM MedicalHistories WHERE patientRamqCode = \"" + ramqCode + "\"";
        medicalHistory = getObservableHistoryDB(query);
        return medicalHistory;
    }

    /**
     * This method makes a query to the database in order to get the contact information of a patient.
     * @return
     */
    public ContactInformation getContactInformation(String ramqCode) {
        ContactInformation contactInformation = null;
        String query = "SELECT * FROM ContactInformation WHERE id = " +
                "(SELECT contactInfoId FROM PatientFiles WHERE ramqCode = \"" + ramqCode + "\")";
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            contactInformation = new ContactInformation(
                resultSet.getInt("number"),
                resultSet.getString("street"),
                resultSet.getString("city"),
                resultSet.getString("postalCode"),
                resultSet.getString("phone"),
                resultSet.getString("email"));
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return contactInformation;
    }

    //**************//
    //Private Methods//
    //**************//
    /**
     * This method queries the database to return an ObservableList of patient files
     * @param query
     * @return
     */
    private ObservableList<PatientFile> getPatientFileDB(String query) {
        Statement statement;
        ResultSet resultSet;
        ObservableList<PatientFile> patientFiles = FXCollections.observableArrayList();
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            PatientFile file;
            while(resultSet.next()) {
                file = new PatientFile(
                    resultSet.getString("ramqCode"),
                    resultSet.getString("firstName"),
                    resultSet.getString("lastName"),
                    Gender.FEMALE,
                    resultSet.getString("birthCity"),
                    LocalDate.parse(resultSet.getString("birthDate")),
                    resultSet.getString("parentsName"));
                patientFiles.add(file);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return patientFiles;
    }

    /**
     * This method queries the database to return a list of MedicalVisit
     * NOTE: Many of the following methods share a same structure, and the repeated
     * lines of codes could be refactored since they only differ in return types.
     * @param query
     * @return
     */
    private List<MedicalVisit> getMedicalVisitDB(String query) {
        Statement statement;
        ResultSet resultSet;
        List<MedicalVisit> visits = new ArrayList<>();
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            MedicalVisit visit;
            while(resultSet.next()) {
                int doctorLicense = resultSet.getInt("doctorLicense");
                ResultSet doctorNameRS = getDoctorName(doctorLicense, conn);
                String doctorName = doctorNameRS.getString("firstName")
                                    + " " + doctorNameRS.getString("lastName");
                ResultSet establishmentNameRS = getEstablishmentName(doctorLicense, conn);
                String establishmentName = establishmentNameRS.getString("name");
                visit = new MedicalVisit(
                    establishmentName,
                    doctorName,
                    doctorLicense,
                    LocalDate.parse(resultSet.getString("visitDate")),
                    resultSet.getString("diagnosis"),
                    resultSet.getString("treatment"),
                    resultSet.getString("summary"),
                    resultSet.getString("notes"));
                visits.add(visit);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return visits;
    }

    /**
     * This method queries the database to obtain a List of MedicalHistory
     * @param query
     * @return
     */
    private List<MedicalHistory> getMedicalHistoryDB(String query) {
        Statement statement;
        ResultSet resultSet;
        List<MedicalHistory> histories = new ArrayList<>();
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            MedicalHistory history;
            while(resultSet.next()) {
                int doctorLicense = resultSet.getInt("doctorLicense");
                ResultSet doctorNameRS = getDoctorName(doctorLicense, conn);
                String doctorName = doctorNameRS.getString("firstName")
                                    + " " + doctorNameRS.getString("lastName");
                String startDate = resultSet.getString(("startDate"));
                LocalDate localStartDate = null;
                if (startDate != null)
                    localStartDate = LocalDate.parse(startDate);
                String endDate = resultSet.getString(("endDate"));
                LocalDate localEndDate = null;
                if (endDate != null) {
                    localEndDate = LocalDate.parse(endDate);
                }
                history = new MedicalHistory(
                    resultSet.getString("diagnosis"),
                    resultSet.getString("treatment"),
                    doctorName,
                    doctorLicense,
                    localStartDate,
                    localEndDate);
                histories.add(history);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return histories;
    }

    /**
     * Gets a doctor from a license number.
     * @param userId
     * @return
     */
    private Doctor getDoctor(int userId, ResultSet userResultSet,Connection conn) {
        Statement statement;
        ResultSet resultSet = null;
        Doctor doctor = null;
        try {
            String queryDoctorName = "SELECT * FROM Doctors WHERE userId = " + userId;
            statement = conn.createStatement();
            resultSet = statement.executeQuery(queryDoctorName);
            MedicalEstablishment medicalEstablishment =
                getMedicalEstablishment(resultSet.getInt("medicalEstablishmentId"),
                                        conn);
            doctor = new Doctor(
                userId,
                userResultSet.getString("firstName"),
                userResultSet.getString("lastName"),
                userResultSet.getString("username"),
                userResultSet.getString("password"),
                resultSet.getInt("license"),
                resultSet.getString("specialty"),
                medicalEstablishment
            );
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return doctor;
    }

    /**
     * This method queries the database to obtain an ObservableList of MedicalVisit
     * @param query
     * @return
     */
    private ObservableList<MedicalVisit> getObservableVisitsDB(String query) {
        Statement statement;
        ResultSet resultSet;
        ObservableList<MedicalVisit> visits = FXCollections.observableArrayList();
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            MedicalVisit visit;
            while(resultSet.next()) {
                int doctorLicense = resultSet.getInt("doctorLicense");
                ResultSet doctorNameRS = getDoctorName(doctorLicense, conn);
                String doctorName = doctorNameRS.getString("firstName")
                                    + " " + doctorNameRS.getString("lastName");
                ResultSet establishmentNameRS = getEstablishmentName(doctorLicense, conn);
                String establishmentName = establishmentNameRS.getString("name");
                visit = new MedicalVisit(
                    establishmentName,
                    doctorName,
                    doctorLicense,
                    LocalDate.parse(resultSet.getString("visitDate")),
                    resultSet.getString("diagnosis"),
                    resultSet.getString("treatment"),
                    resultSet.getString("summary"),
                    resultSet.getString("notes"));
                visits.add(visit);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return visits;
    }

    /**
     * This method queries the database to obtain an ObservableList of MedicalHistory
     * @param query
     * @return
     */
    private ObservableList<MedicalHistory> getObservableHistoryDB(String query) {
        Statement statement;
        ResultSet resultSet;
        ObservableList<MedicalHistory> historyObservableList = FXCollections.observableArrayList();
        Connection conn = DBConnection.getInstance(databasePath).getConnection();
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            MedicalHistory history;
            while(resultSet.next()) {
                int doctorLicense = resultSet.getInt("doctorLicense");
                ResultSet doctorNameRS = getDoctorName(doctorLicense, conn);
                String doctorName = doctorNameRS.getString("firstName")
                                    + " " + doctorNameRS.getString("lastName");
                String endDate = resultSet.getString(("endDate"));
                LocalDate localEndDate = null;
                if (endDate != null) {
                    localEndDate = LocalDate.parse(endDate);
                }
                history = new MedicalHistory(
                    resultSet.getString("diagnosis"),
                    resultSet.getString("treatment"),
                    doctorName,
                    doctorLicense,
                    LocalDate.parse(resultSet.getString("startDate")),
                    localEndDate);
                historyObservableList.add(history);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return historyObservableList;
    }

    /**
     * Gets a MedicalEstablishment from an ID.
     * @param id
     * @return
     */
    private MedicalEstablishment getMedicalEstablishment(int id, Connection conn) {
        Statement statement;
        ResultSet resultSet = null;
        MedicalEstablishment medicalEstablishment = null;
        try {
            String query = "SELECT * FROM MedicalEstablishments WHERE id = " + id;
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            ContactInformation contactInformation =
                getContactInformation(resultSet.getInt("contactInfoId"), conn);
            medicalEstablishment = new MedicalEstablishment(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                contactInformation
            );
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return medicalEstablishment;
    }

    /**
     * Gets a ContactInformation from an ID.
     * @param id
     * @return
     */
    private ContactInformation getContactInformation(int id, Connection conn) {
        Statement statement;
        ResultSet resultSet;
        ContactInformation contactInformation = null;
        try {
            String query = "SELECT * FROM ContactInformation WHERE id = " + id;
            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            contactInformation = new ContactInformation(
                resultSet.getInt("number"),
                resultSet.getString("street"),
                resultSet.getString("city"),
                resultSet.getString("postalCode"),
                resultSet.getString("phone"),
                resultSet.getString("email")
            );
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return contactInformation;
    }

    /**
     * Gets the doctor's first and last name from his license number.
     * @param license
     * @return
     */
    private ResultSet getDoctorName(int license, Connection conn) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            String queryDoctorName = "SELECT firstName, lastName FROM Users WHERE id = " +
                                     "(SELECT userId FROM Doctors WHERE license = " + license + ")";
            statement = conn.createStatement();
            resultSet = statement.executeQuery(queryDoctorName);
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return resultSet;
    }

    /**
     * Gets the establishment's name from a doctor's license.
     * @param license
     * @return
     */
    private ResultSet getEstablishmentName(int license, Connection conn) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            String queryEstablishmentName = "SELECT Name FROM MedicalEstablishments WHERE id = " +
                                            "(SELECT medicalEstablishmentId FROM Doctors" +
                                            " WHERE license = " + license + ")";
            statement = conn.createStatement();
            resultSet = statement.executeQuery(queryEstablishmentName);
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return resultSet;
    }

    /**
     * This method will execute a query to the database
     * @param query which needs to be submitted to the database
     */
    private void executeQuery(String query) {
        Connection connection = DBConnection.getInstance(databasePath).getConnection();
        Statement st;
        try {
            st = connection.createStatement();
            st.executeUpdate(query);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
