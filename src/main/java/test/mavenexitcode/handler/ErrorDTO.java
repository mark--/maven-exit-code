package test.mavenexitcode.handler;

public class ErrorDTO {
	private Exception thrownException;
	private String consumerRecord;
	private String serviceName;
	private String errorTime;

	public ErrorDTO() {
		// muss da sein für JSON (ruhig leer)
	}

	public ErrorDTO(Exception thrownException, String consumerRecord) {
		this.thrownException = thrownException;
		this.consumerRecord = consumerRecord;
	}

	public Exception getThrownException() {
		return thrownException;
	}

	public void setThrownException(Exception thrownException) {
		this.thrownException = thrownException;
	}

	public String getConsumerRecord() {
		return consumerRecord;
	}

	public void setConsumerRecord(String consumerRecord) {
		this.consumerRecord = consumerRecord;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getErrorTime() {
		return errorTime;
	}

	public void setErrorTime(String errorTime) {
		this.errorTime = errorTime;
	}

}
