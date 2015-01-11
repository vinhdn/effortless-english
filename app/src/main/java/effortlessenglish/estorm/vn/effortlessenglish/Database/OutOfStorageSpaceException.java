package effortlessenglish.estorm.vn.effortlessenglish.Database;

public class OutOfStorageSpaceException extends StorageUnavailableException {
	public OutOfStorageSpaceException(String description) {
		super(description, "");
	}
}
