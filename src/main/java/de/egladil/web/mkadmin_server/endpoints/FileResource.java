// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server.endpoints;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import de.egladil.web.mkadmin_server.util.PathFileMapper;

/**
 * FileResource
 */
@RequestScoped
@Path("/files")
@Consumes(MediaType.APPLICATION_JSON)
public class FileResource {

	private static final String PATH_DOWNLOAD_DIR = "/home/heike/Downloads/mkv-junit/";

	private static final String PATH_UPLOAD_DIR = "/home/heike/Downloads/mkv-junit/";

	@GET
	@Path("/csv")
	@Produces({ "text/csv" })
	public Response downloadCSV() {

		File file = new PathFileMapper().apply(PATH_DOWNLOAD_DIR + "test.csv");

		if (file == null) {

			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=\"test.csv\"");
		return response.build();
	}

	@GET
	@Path("/pdf")
	@Produces({ "application/pdf" })
	public Response downloadPDF() {

		File file = new PathFileMapper().apply(PATH_DOWNLOAD_DIR + "test.pdf");

		if (file == null) {

			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=\"test.pdf\"");
		return response.build();
	}

	@GET
	@Path("/excel")
	@Produces({ "application/vnd.ms-excel" })
	public Response downloadExcel() {

		File file = new PathFileMapper().apply(PATH_DOWNLOAD_DIR + "test.xls");

		if (file == null) {

			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=\"test.xls\"");
		return response.build();
	}

	@POST
	@Path("/file")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFile(final MultipartFormDataInput input) {

		String fileName = "";

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadedFile");

		for (InputPart inputPart : inputParts) {

			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file path
				fileName = PATH_UPLOAD_DIR + fileName;

				writeFile(bytes, fileName);

				System.out.println("Done");

			} catch (IOException e) {

				e.printStackTrace();
				return Response.serverError().build();
			}

		}

		return Response.ok().build();
	}

	private void writeFile(final byte[] content, final String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {

			file.createNewFile();
		}

		try (FileOutputStream fop = new FileOutputStream(file)) {

			fop.write(content);
			fop.flush();
		}
	}

	private String getFileName(final MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {

			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}
}
