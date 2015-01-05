package nl.ictu.isd.art.services.excel2test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.ictu.isd.art.meta.TestScriptFactory;
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
    private final static TestScriptFactory FACTORY = new TestScriptFactory("");

    public static void main(String[] args) {
        get("/sheet/:sheet/file/*", (Request request, Response response) -> {
            try {
                String fileName = "/" + request.splat()[0];
                LOG.log(Level.INFO, "File to open {0}", fileName);
                return FACTORY.createFromExcelSheet(new FileInputStream(fileName), request.params(":sheet")).toString();
            } catch (IOException | InvalidFormatException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                return "Error! " + ex.toString();
            }
        });

        post("/:sheet", (Request request, Response response) -> {
            List<FileItem> items = null;
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload fu = new ServletFileUpload(factory);
            try {
                items = fu.parseRequest(request.raw());
                InputStream content = items.get(0).getInputStream();
                return FACTORY.createFromExcelSheet(content, request.params(":sheet")).toString();
            } catch (IOException | InvalidFormatException | FileUploadException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, "Unable to parse file", ex);
            }
            halt(200);
            return null;
        });
    }
}
