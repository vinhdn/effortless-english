package effortlessenglish.estorm.vn.effortlessenglish.Database;

import java.io.IOException;

public class StorageUnavailableException extends IOException{
	private static final long serialVersionUID = 8167670407637529209L;

	private String environmentState;

	public StorageUnavailableException(String description, String environmentState) {
		super(description);
		this.environmentState = environmentState;
	}

	public String getEnvironmentState() {
		return environmentState;
	}

	public void setEnvironmentState(String environmentState) {
		this.environmentState = environmentState;
	}
}
