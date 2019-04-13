package android.example.com.prescriptminder;

public class NearbyDevices {

    private String name;
    private String address;
    private boolean connected;

    public NearbyDevices(String name, String address, boolean connected) {

        this.name = name;
        this.address = address;

        if(connected == true) {
            this.connected = true;
        }
        else {
            this.connected = false;
        }
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean value) {
        connected = value;
    }
}
