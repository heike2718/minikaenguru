// =====================================================
// Projekt: de.egladil.email.storage
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.enums.MailqueueItemStatus;

/**
 * Mail nur das Zeug zum Senden. Versendet wird über den email-service mit der dortigen Konfiguration.
 */
@Entity
@Table(name = "mailqueue")
public class MailqueueItem implements IMkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Size(max = 2000)
	@Column(name = "RECIPIENTS")
	private String recipients;

	@NotNull
	@Size(max = 100)
	@Column(name = "SUBJECT")
	private String subject;

	@NotNull
	@Size(max = 4000)
	@Column(name = "BODY")
	private String mailBody;

	@Size(max = 2000)
	@Column(name = "STATUSMESSAGE")
	private String statusmessage;

	@NotNull
	@Enumerated(EnumType.STRING)
	private MailqueueItemStatus status = MailqueueItemStatus.WAITING;

	@Version
	@Column(name = "VERSION")
	private int version;

	@Override
	public String toString() {

		return "MailqueueItem [id=" + id + ", subject=" + subject + ", status=" + status + ", recipients=" + recipients + "]";
	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public String getRecipients() {

		return recipients;
	}

	public void setRecipients(final String to) {

		this.recipients = to;
	}

	public String getSubject() {

		return subject;
	}

	public void setSubject(final String subject) {

		this.subject = subject;
	}

	public String getStatusmessage() {

		return statusmessage;
	}

	public void setStatusmessage(final String statusmessage) {

		this.statusmessage = statusmessage;
	}

	public String getMailBody() {

		return mailBody;
	}

	public void setMailBody(final String mailBody) {

		this.mailBody = mailBody;
	}

	public MailqueueItemStatus getStatus() {

		return status;
	}

	public void setStatus(final MailqueueItemStatus status) {

		this.status = status;
	}

}
