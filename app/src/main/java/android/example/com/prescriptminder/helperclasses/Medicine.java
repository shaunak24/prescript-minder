package android.example.com.prescriptminder.helperclasses;

public class Medicine
{
    private String medicineName;
    private String note;

    public Medicine() {
    }

    public Medicine(String medicineName, String note) {
        this.medicineName = medicineName;
        this.note = note;
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

    public void setNote(String note) {
        this.note = note;
    }
}
