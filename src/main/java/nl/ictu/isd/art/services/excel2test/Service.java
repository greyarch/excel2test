package nl.ictu.isd.art.services.excel2test;

import com.google.common.io.Files;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.ictu.isd.art.services.excel2test.model.TestScriptFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import spark.Request;
import spark.Response;
import static spark.Spark.*;

/**
 *
 * @author gantcho
 */
public class Service {

    private static final Logger LOG = Logger.getLogger("Excel to JSON service");
    private final static TestScriptFactory TEST_SCRIPT_FACTORY = new TestScriptFactory("");
    private final static FileItemFactory FILE_FACTORY = new DiskFileItemFactory(10485760, Files.createTempDir()); //10MB thereshold
    private final static ServletFileUpload UPLOAD = new ServletFileUpload(FILE_FACTORY);

    public static void main(String[] args) {
        get("/sheet/:sheet/file/*", (Request request, Response response) -> {
            try {
                String fileName = "/" + request.splat()[0];
                LOG.log(Level.INFO, "File to open {0}", fileName);
                return TEST_SCRIPT_FACTORY.createFromExcelSheet(new FileInputStream(fileName), request.params(":sheet")).toString();
            } catch (IOException | InvalidFormatException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                return "Error! " + ex.toString();
            }
        });

        post("/:sheet", (Request request, Response response) -> {
            List<FileItem> items = null;
            try {
                items = UPLOAD.parseRequest(request.raw());
                InputStream content = items.get(0).getInputStream();
                return TEST_SCRIPT_FACTORY.createFromExcelSheet(content, request.params(":sheet")).toString();
            } catch (IOException | InvalidFormatException | FileUploadException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, "Unable to parse file", ex);
                halt(500, "I was unable to parse the file you sent me! Are you sure you are sending an Excel file?");
            }
            halt(200);
            return null;
        });
    }
}
