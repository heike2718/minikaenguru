// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.itextpdf.text.BaseColor;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * Urkundenmotiv
 */
public class Urkundenmotiv {

	private final byte[] data;

	private BaseColor baseColor;

	public static Urkundenmotiv createFromFarbschema(final Farbschema farbschema) {

		try (InputStream in = Urkundenmotiv.class.getResourceAsStream(farbschema.getBackgroundClasspathResource())) {

			byte[] data = IOUtils.toByteArray(in);

			Ueberschriftfarbe ueberschriftfarbe = farbschema.getUeberschriftfarbe();

			BaseColor baseColor = new BaseColor(ueberschriftfarbe.rgbRed(), ueberschriftfarbe.rgbGreen(),
				ueberschriftfarbe.rgbBlue());

			return new Urkundenmotiv(data, baseColor);

		} catch (IOException e) {

			throw new MkGatewayRuntimeException(
				"Kann Farbschema-Resourcen nicht finden [Farbschema=" + farbschema + "]: " + e.getMessage(), e);
		}

	}

	private Urkundenmotiv(final byte[] data, final BaseColor baseColor) {

		this.data = data;
		this.baseColor = baseColor;
	}

	public byte[] data() {

		return data;
	}

	public BaseColor baseColor() {

		return baseColor;
	}
}
