package android.example.com.prescriptminder.helperclasses;

public class Medicine
{
    private String medicineName;
    private String note;
    private String details;

    public Medicine() {
    }

    public Medicine(String medicineName, String note, String details) {
        this.medicineName = medicineName;
        this.note = note;
        this.details = details;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getNote() {
        return note;
    }

    public String getDetails() { return details; }

    public void setNote(String note) {
        this.note = note;
    }
}
