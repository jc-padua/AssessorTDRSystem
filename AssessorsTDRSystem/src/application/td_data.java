package application;

public class td_data {
    private String pin;
    private String snumber;
    private String owner;
    private String location;
    private Boolean archive;
    private byte[] fileData;

    public td_data(String pin, String snumber, String owner, String location, Boolean archive, byte[] fileData) {
    	this.pin = pin;
        this.snumber = snumber;
        this.owner = owner;
        this.location = location;
        this.archive = archive;
        this.fileData = fileData;
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

    public Boolean getArchive() {
    	return archive;
    }
    
    public void setArchive(Boolean archive) {
    	this.archive = archive;
    }
    
    public byte[] getFileData() {
    	return fileData;
    }
    
    public void setFileData(byte[] fileData) {
    	this.fileData = fileData;
    }
    
    @Override
    public String toString() {
        return "td_data{" +
                "pin='" + pin + '\'' +
                ", snumber='" + snumber + '\'' +
                ", owner='" + owner + '\'' +
                ", location='" + location + '\'' +
                ", archive='" + archive + '\'' +
                ", fileData=" + (fileData != null ? "Present" : "Absent") +
                '}';
    }
}
