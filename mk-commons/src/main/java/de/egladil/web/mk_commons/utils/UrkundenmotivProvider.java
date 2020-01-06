// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.utils;

import java.io.InputStream;

import com.itextpdf.text.BaseColor;

/**
 * UrkundenmotivProvider
 */
// TODO MK-Refactor - gehört nach mkv-server
public interface UrkundenmotivProvider {

	InputStream getBackgroundImage();

	BaseColor getHeadlineColor();
}
