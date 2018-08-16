package test.mavenexitcode.mail;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MailDTO {

	private final String id = UUID.randomUUID().toString();

	private String subject;

	private String from;

	private List<String> to = new LinkedList<>();

	private List<String> cc = new LinkedList<>();

	private String replyTo;

	private String content;

	private List<Attachment> attachments = new LinkedList<>();

	public MailDTO() {
	}

	public String getReplyTo() {
		return replyTo;
	}

	public MailDTO(String subject, String from, List<String> to, List<String> cc, String replyTo, String content,
			List<Attachment> attachments) {
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.replyTo = replyTo;
		this.content = content;
		this.attachments = attachments;
	}

	public MailDTO(String subject, String from, List<String> to, String content) {
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.content = content;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getId() {
		return id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "MailDTO [id=" + id + ", subject=" + subject + ", from=" + from + ", to=" + to + ", cc=" + cc
				+ ", replyTo=" + replyTo + ", content=" + content + ", attachments=" + attachments + "]";
	}

}
