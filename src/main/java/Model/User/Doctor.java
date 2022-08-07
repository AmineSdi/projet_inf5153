package Model.User;
import Model.ContactInformation.MedicalEstablishment;
import Model.PatientFile.MedicalHistory;
import Model.PatientFile.MedicalVisit;
import Model.Visitor.Visitor;
import java.time.LocalDate;

public class Doctor extends User implements Visitor {
    //VARIABLES
    /** Note : Pour faire fonctionner le patron Visiteur, on doit garder certaines informations
     * dans la classe Doctor : diagnosis, treatment, visitSummary et notes pour le
     * MedicalVisit, et diagnosis, treatment, endDate pour le medicalHistory... Alors on va
     * voir si c'est ok de garder ces attributs ici en attendant une meilleure solution...
     * Ces infos vont venir des textfields à partir du MainController...
     * */
    private int license;
    private String specialty;
    private MedicalEstablishment medicalEstablishment;
    private String visitDiagnosis;
    private String visitTreatment;
    private String visitSummary;
    private String visitNotes;
    private String historyDiagnosis;
    private String historyTreatment;
    private LocalDate historyStartDate;
    private LocalDate historyEndDate;

    //**************//
    //Public Methods//
    //**************//
    //CONSTRUCTEUR
    public Doctor(int userId, String firstName, String lastName, String userName, String password,
                  int license, String specialty,
                  MedicalEstablishment medicalEstablishment) {
        super(userId, firstName, lastName, userName, password);
        validateLicense(license);
        this.specialty = specialty;
        this.medicalEstablishment = medicalEstablishment;
    }

    public void setHistoryDiagnosis(String historyDiagnosis) {
        this.historyDiagnosis = historyDiagnosis;
    }

    public void setHistoryTreatment(String historyTreatment) {
        this.historyTreatment = historyTreatment;
    }

    public void setHistoryEndDate(LocalDate historyEndDate) {
        this.historyEndDate = historyEndDate;
    }

    public void setHistoryStartDate(LocalDate historyStartDate) {
        this.historyStartDate = historyStartDate;
    }

    //SETTERS AND GETTERS
    public void setVisitDiagnosis(String visitDiagnosis) {
        this.visitDiagnosis = visitDiagnosis;
    }
    public void setVisitTreatment(String visitTreatment) {
        this.visitTreatment = visitTreatment;
    }
    public void setVisitSummary(String visitSummary) {
        this.visitSummary = visitSummary;
    }
    public void setVisitNotes(String visitNotes) {
        this.visitNotes = visitNotes;
    }
    public int getLicense() {
        return license;
    }

    //PRIVATE METHODS
    /**
     * Validates that the license number of the Doctor.
     * If invalid, sets the license to 0, which prevents the Doctor from using the application.
     *
     * @param license The license number to validate.
     */
    private void validateLicense(int license) {
        if (license >= 10000 && license <= 99999)
            this.license = license;
        else
            this.license = 0;
    }

    //PUBLIC METHODS
    @Override
    public void visitMedicalHistory(MedicalHistory history) {
        history.modifyHistory(firstName, lastName, license, historyDiagnosis, historyTreatment,
                              historyStartDate, historyEndDate);
    }

    @Override
    public void visitMedicalVisit(MedicalVisit visit) {
        visit.modifyVisit(medicalEstablishment.getName(), firstName, lastName, license, LocalDate.now(),
                          visitDiagnosis, visitTreatment, visitSummary, visitNotes);
    }
}
