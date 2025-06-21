//=====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.minikaenguru_statistik.domain.wettbewerbe;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
@Schema(description = "die Fahrbe, mit der der Wettbewerb im Liniekdiagramm dargestellt werden soll")
public class WettbewerbColors {

	@JsonProperty
	private String backgroundColor;

	@JsonProperty
	private String borderColor;

	@JsonProperty
	private String pointBackgroundColor;

	@JsonProperty
	private String pointBorderColor;

	@JsonProperty
	private String pointHoverBackgroundColor;

	@JsonProperty
	private String pointHoverBorderColor;

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
