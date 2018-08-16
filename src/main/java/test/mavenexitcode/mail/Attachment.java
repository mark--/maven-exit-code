package test.mavenexitcode.mail;

public class Attachment {

	private String name;

	private String contentType;

	private String content;

	private boolean binary = false;

	public Attachment() {
	}

	public Attachment(String name, String contentType, String content) {
		this.name = name;
		this.contentType = contentType;
		this.content = content;
	}

	public Attachment(String name, String contentType, String content, boolean binary) {
		this.name = name;
		this.contentType = contentType;
		this.content = content;
		this.binary = binary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String mimeType) {
		contentType = mimeType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	@Override
	public String toString() {
		return "Attachment [name=" + name + ", mimeType=" + contentType + ", content=" + content + ", binary=" + binary
				+ "]";
	}

}
