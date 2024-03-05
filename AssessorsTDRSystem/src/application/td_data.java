package application;

public class td_data {
    private String pin;
    private String snumber;
    private String owner;
    private String location;


    public td_data(String pin, String snumber, String owner, String location) {
		// TODO Auto-generated constructor stub
    	this.pin = pin;
        this.snumber = snumber;
        this.owner = owner;
        this.location = location;
	}

	public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSnumber() {
        return snumber;
    }

    public void setSnumber(String snumber) {
        this.snumber = snumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "td_data{" +
                "pin='" + pin + '\'' +
                ", snumber='" + snumber + '\'' +
                ", owner='" + owner + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
