// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain;

import de.egladil.web.mk_commons.domain.impl.Downloaddaten;

/**
 * IDownload
 */
public interface IDownload {

	Downloaddaten getDownloaddaten();

	void setDownloaddaten(Downloaddaten downloaddaten);
}
