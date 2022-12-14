package Model.User;
import Model.ContactInformation.MedicalEstablishment;
import Model.PatientFile.MedicalHistory;
import Model.PatientFile.MedicalVisit;
import Model.Visitor.Visitor;
import java.time.LocalDate;

public class Doctor extends User implements Visitor {
    //VARIABLES
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
        setSpecialty(specialty);
        this.medicalEstablishment = medicalEstablishment;
    }

    public void setHistoryDiagnosis(String historyDiagnosis) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";
        if (historyDiagnosis != null) {
            if (historyDiagnosis.matches(validFormat) && historyDiagnosis.length() > 0)
                isValid = true;

            if (!isValid)
                this.historyDiagnosis = null;
            else
                this.historyDiagnosis = historyDiagnosis;
        }
    }

    public void setHistoryTreatment(String historyTreatment) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";
        if (historyTreatment != null) {

            if (historyTreatment.matches(validFormat) && historyTreatment.length() > 0)
                isValid = true;

            if (!isValid)
                this.historyTreatment = null;
            else
                this.historyTreatment = historyTreatment;
        }
    }

    public void setHistoryEndDate(LocalDate historyEndDate) {
        this.historyEndDate = historyEndDate;
    }

    public void setHistoryStartDate(LocalDate historyStartDate) {
        boolean isValid = false;

        LocalDate date = LocalDate.now();
        if (historyStartDate.equals(date))
            isValid = true;

        if (!isValid)
            this.historyStartDate = null;
        else
            this.historyStartDate = historyStartDate;
    }

    //SETTERS AND GETTERS
    public void setVisitDiagnosis(String visitDiagnosis) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";
        if (visitDiagnosis != null) {
            if (visitDiagnosis.matches(validFormat) && visitDiagnosis.length() > 0)
                isValid = true;

            if (!isValid)
                this.visitDiagnosis = null;
            else
                this.visitDiagnosis = visitDiagnosis;
        }
    }
    public void setVisitTreatment(String visitTreatment) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";
        if (visitTreatment != null) {

            if (visitTreatment.matches(validFormat) && visitTreatment.length() > 0)

                isValid = true;

            if (!isValid)
                this.visitTreatment = null;
            else
                this.visitTreatment = visitTreatment;
        }
    }
    public void setVisitSummary(String visitSummary) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";
        if (visitSummary != null) {

            if (visitSummary.matches(validFormat) && visitSummary.length() > 0)
                isValid = true;

            if (!isValid)
                this.visitSummary = null;
            else
                this.visitSummary = visitSummary;
        }
    }
    public void setVisitNotes(String visitNotes) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";
        if (visitNotes != null) {

            if (visitNotes.matches(validFormat) && visitNotes.length() > 0)
                isValid = true;
            if (!isValid)
                this.visitNotes = null;
            else
                this.visitNotes = visitNotes;
        }
    }
    public int getLicense() {
        return license;
    }

    public void setLicense(int license) {
        if (license >= 10000 && license <= 99999)
            this.license = license;
        else
            this.license = 0;
    }
    public String getSpecialty() {
        return specialty;
    }
    public void setSpecialty(String specialty) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";
        if (specialty != null) {
            if (specialty.matches(validFormat) && specialty.length() > 0)
                isValid = true;

            if (!isValid)
                this.specialty = null;
            else
                this.specialty = specialty;
        }
    }

    //***************//
    //PRIVATE METHODS//
    //***************//
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
    /**
     * Creates a new MedicalVisit, filling in the information about the Doctor and the Date.
     * The rest of the information (diagnosis, treatment, etc.) is initially empty and is set
     * through the user interface text fields.
     *
     * @return The new medical visit.
     */
    public MedicalVisit createVisit() {
        MedicalVisit mv = new MedicalVisit(this.medicalEstablishment.getName(),
                                           this.firstName + " " + this.lastName, this.license, LocalDate.now(),
                                           null, null, null, null);

        return mv;
    }

    /**
     * Creates a new MedicalHistory, filling in the information about the Doctor and the Date.
     * The rest of the information (diagnosis, treatment, etc.) is initially empty and is set
     * through the user interface text fields.
     *
     * @return The new medical history.
     */
    public MedicalHistory createHistory() {
        MedicalHistory mh = new MedicalHistory(null, null,
                                               this.firstName + " " + this.lastName, this.license,
                                               LocalDate.now(), null);
        return mh;
    }

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
