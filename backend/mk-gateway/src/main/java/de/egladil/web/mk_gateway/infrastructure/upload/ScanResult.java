// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.upload;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ScanResult
 */
public class ScanResult {

	private static final String KEY_USER_ID = "userID";

	private static final String KEY_UPLOAD_NAME = "uploadName";

	private static final String KEY_MEDIA_TYPE = "mediaType";

	private static final String KEY_VIRUS_DETECTION = "virusDetection";

	private static final String KEY_THREAD_DETECTION = "threadDetection";

	private static final String KEY_CHARSET = "charset";

	@JsonProperty
	private String userID;

	@JsonProperty
	private String uploadName;

	@JsonProperty
	private String mediaType;

	@JsonProperty
	private VirusDetection virusDetection;

	@JsonProperty
	private ThreadDetection threadDetection;

	@JsonProperty
	private String charset;

	@SuppressWarnings("unchecked")
	public static ScanResult withAttributes(final Map<String, Object> keyValueMap) {

		ScanResult result = new ScanResult();

		if (keyValueMap.get(KEY_USER_ID) != null) {

			result.userID = (String) keyValueMap.get(KEY_USER_ID);
		}

		if (keyValueMap.get(KEY_UPLOAD_NAME) != null) {

			result.uploadName = (String) keyValueMap.get(KEY_UPLOAD_NAME);
		}

		if (keyValueMap.get(KEY_MEDIA_TYPE) != null) {

			result.mediaType = (String) keyValueMap.get(KEY_MEDIA_TYPE);
		}

		if (keyValueMap.get(KEY_VIRUS_DETECTION) != null) {

			@SuppressWarnings("rawtypes")
			Map data = (Map) keyValueMap.get(KEY_VIRUS_DETECTION);

			result.virusDetection = VirusDetection.withAttributes(data);
		}

		if (keyValueMap.get(KEY_THREAD_DETECTION) != null) {

			@SuppressWarnings("rawtypes")
			Map data = (Map) keyValueMap.get(KEY_THREAD_DETECTION);

			result.threadDetection = ThreadDetection.withAttributes(data);
		}

		if (keyValueMap.get(KEY_CHARSET) != null) {

			result.charset = (String) keyValueMap.get(KEY_CHARSET);
		}

		return result;
	}

	public String getUserID() {

		return userID;
	}

	public String getUploadName() {

		return uploadName;
	}

	public String getMediaType() {

		return mediaType;
	}

	public VirusDetection getVirusDetection() {

		return virusDetection;
	}

	public ThreadDetection getThreadDetection() {

		return threadDetection;
	}

	public String getCharset() {

		return charset;
	}

}
