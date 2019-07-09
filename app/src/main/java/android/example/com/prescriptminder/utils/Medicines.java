package android.example.com.prescriptminder.utils;

public class Medicines {

    private String name;
    private String note;
    private String details;

    public Medicines(String name, String note, String details) {
        this.name = name;
        this.note = note;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public String getDetails() {
        return details;
    }
}
