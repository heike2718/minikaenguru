// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.pdfutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * PdfMerger
 */
public class PdfMerger {

	private static final Logger LOGGER = LoggerFactory.getLogger(PdfMerger.class);

	/**
	 * Fasst die List von pdfs zu einem pdf zusammen.
	 *
	 * @param  pdfs
	 * @return      byte[]
	 */
	public byte[] concatPdf(final List<byte[]> pdfs) {

		if (pdfs == null || pdfs.isEmpty()) {

			throw new IllegalArgumentException("pdfs sind null oder leer");
		}

		if (pdfs.size() == 1) {

			return pdfs.get(0);
		}
		final Document document = new Document();
		LOGGER.info("merge: {} Seiten", pdfs.size());

		PdfReader first = null;
		PdfReader second = null;

		try {

			first = new PdfReader(pdfs.get(0));
			second = new PdfReader(pdfs.get(1));

			byte[] firstChunk = concat(first, second, document);

			for (int i = 2; i < pdfs.size(); i++) {

				first = new PdfReader(firstChunk);
				second = new PdfReader(pdfs.get(i));
				firstChunk = concat(first, second, document);

				if (LOGGER.isDebugEnabled()) {

					printMemoryUsage();
				}
			}

			return firstChunk;
		} catch (final IOException | DocumentException e) {

			throw new MkGatewayRuntimeException("Unerwartete Exception beim pipen von PDFs: " + e.getMessage(), e);
		} finally {

			close(first);
			close(second);

			// FIXME: I0407
			// LOGGER.info("calling gc");
			// System.gc();
		}
	}

	private byte[] concat(final PdfReader first, final PdfReader second, final Document document) throws DocumentException, IOException {

		try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			final PdfCopy copy = new PdfCopy(document, out);
			document.open();
			copy.addDocument(first);
			copy.addDocument(second);
			document.close();
			first.close();
			second.close();
			final byte[] data = out.toByteArray();
			return data;
		}
	}

	private void close(final PdfReader reader) {

		if (reader != null) {

			reader.close();
		}
	}

	private void printMemoryUsage() {

		Runtime runtime = Runtime.getRuntime();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		LOGGER.debug("Used memory is megabytes: " + bytesToMegabytes(memory));

	}

	/**
	 * @param  memory
	 * @return
	 */
	private long bytesToMegabytes(final long memory) {

		return memory / 1024;
	}
}
