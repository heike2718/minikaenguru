//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "FARBEN_WETTBEWERBE")
public class FarbenWettbewerbe {

	@Id
	@Column(name = "WETTBEWERB_UUID")
	private String wettbewerbUUID;

	@Column(name = "BACKGOUND_COLOR")
	private String backgroundColor;

	@Column(name = "BORDER_COLOR")
	private String borderColor;

	@Column(name = "POINT_BACKGOUND_COLOR")
	private String pointBackgroundColor;

	@Column(name = "POINT_BORDER_COLOR")
	private String pointBorderColor;

	@Column(name = "POINT_HOVER_BACKGOUND_COLOR")
	private String pointHoverBackgroundColor;

	@Column(name = "POINT_HOVER_BORDER_COLOR")
	private String pointHoverBorderColor;

	public String getWettbewerbUUID() {
		return wettbewerbUUID;
	}

	public void setWettbewerbUUID(String wettbewerbUUID) {
		this.wettbewerbUUID = wettbewerbUUID;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getPointBackgroundColor() {
		return pointBackgroundColor;
	}

	public void setPointBackgroundColor(String pointBackgroundColor) {
		this.pointBackgroundColor = pointBackgroundColor;
	}

	public String getPointBorderColor() {
		return pointBorderColor;
	}

	public void setPointBorderColor(String pointBorderColor) {
		this.pointBorderColor = pointBorderColor;
	}

	public String getPointHoverBackgroundColor() {
		return pointHoverBackgroundColor;
	}

	public void setPointHoverBackgroundColor(String pointHoverBackgroundColor) {
		this.pointHoverBackgroundColor = pointHoverBackgroundColor;
	}

	public String getPointHoverBorderColor() {
		return pointHoverBorderColor;
	}

	public void setPointHoverBorderColor(String pointHoverBorderColor) {
		this.pointHoverBorderColor = pointHoverBorderColor;
	}

}
