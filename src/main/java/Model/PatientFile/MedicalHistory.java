package Model.PatientFile;
import Model.Visitor.Visitable;
import Model.Visitor.Visitor;
import java.time.LocalDate;

public class MedicalHistory implements Visitable {
    //VARIABLES
    private String diagnosis;
    private String treatment;
    private String doctorName;
    private int doctorLicense;
    private LocalDate startDate;
    LocalDate endDate;

    //**************//
    //Public Methods//
    //**************//
    public MedicalHistory() {}

    public MedicalHistory(String diagnosis, String treatment, String doctorName, int doctorLicense,
                          LocalDate startDate, LocalDate endDate) {
        setDiagnosis(diagnosis);
        setTreatment(treatment);
        setDoctorName(doctorName);
        setDoctorLicense(doctorLicense);
        setStartDate(startDate);
        this.endDate = endDate;
    }

    public int getDoctorLicense() {
        return doctorLicense;
    }

    public void setDoctorLicense(int doctorLicense) {
        if (doctorLicense >= 10000 && doctorLicense <= 99999)
            this.doctorLicense = doctorLicense;
        else
            this.doctorLicense = 0;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";

        if (diagnosis != null) {
            if (diagnosis.matches(validFormat) && diagnosis.length() > 0)
                isValid = true;

            if (!isValid)
                this.diagnosis = null;
            else
                this.diagnosis = diagnosis;
        }
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z0-9-,.?'()\\s])*$";
        if (treatment != null) {

            if (treatment.matches(validFormat) && treatment.length() > 0)
                isValid = true;

            if (!isValid)
                this.treatment = null;
            else
                this.treatment = treatment;
        }
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        boolean isValid = false;
        String validFormat = "^([a-zA-Z-\\s])*$";

        if (doctorName.matches(validFormat) && doctorName.length() > 0)
            isValid = true;

        if (!isValid)
            this.doctorName = null;
        else
            this.doctorName = doctorName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        boolean isValid = false;

        LocalDate date = LocalDate.now();
        if (startDate.equals(date))
            isValid = true;

        if (!isValid)
            this.startDate = null;
        else
            this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        boolean isValid = false;

        LocalDate date = LocalDate.now();
        if (endDate.equals(date))
            isValid = true;

        if (!isValid)
            this.endDate = null;
        else
            this.endDate = endDate;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitMedicalHistory(this);
    }

    /**
     * Sets the diagnosis, treatment, and endDate this medical history.
     * */
    public void modifyHistory(String firstName, String lastName, int license,
                              String diagnosis, String treatment, LocalDate startDate,
                              LocalDate endDate) {
        this.doctorName = firstName + " " + lastName;
        this.doctorLicense = license;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
