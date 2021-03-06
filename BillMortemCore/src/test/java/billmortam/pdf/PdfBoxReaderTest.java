package billmortam.pdf;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by pp00344204 on 06/06/17.
 */
public class PdfBoxReaderTest {

    @Test
    public void test_pdf_file() throws PdfReaderException, IOException {
        PdfReader pdfReader = PdfBoxReader.getReader();
        Pdf output = pdfReader.read(new File("src/main/resources/sample_pdfs/sample.pdf").getCanonicalPath());
//        System.out.println(output.getData());
        Assert.assertNotNull(output);

    }

    @Test
    public void test_not_a_file() throws PdfReaderException {
        PdfReader pdfReader = PdfBoxReader.getReader();
        Pdf output = pdfReader.read("/Users/pp00344204/Documents/misc/padhai/projects/bill_reader/src/main/java/com.billmortam.pdf");
        System.out.println(output);
        Assert.assertNull(output);
    }

    @Test
    public void test_for_password_protected_Message() throws IOException {
        PdfReader pdfReader = PdfBoxReader.getReader();
        try {
            pdfReader.read(new File("src/main/res/sample_password_protected_bill.pdf").getCanonicalPath());
        } catch (PdfReaderException e) {
            Assert.assertEquals(PdfReaderException.ExceptionType.PASSWORD_PROTECTED, e.getExceptionType());
        }

    }

    @Test
    public void test_for_password_protected_file() throws IOException {
        PdfReader pdfReader = PdfBoxReader.getReader();
        String path = new File("src/main/resources/sample_pdfs/sample_password_protected_bill.pdf").getCanonicalPath();
        Pdf output = null;
        try {
            output = pdfReader.read(new File(path).getCanonicalPath());
        } catch (PdfReaderException e) {
            try {
                output = pdfReader.read(path, "1234");
            } catch (PdfReaderException e1) {
                e1.printStackTrace();
            }
        }

//        System.out.print(output.getData());
        Assert.assertNotNull(output);
    }

    @Test
    public void test_citi_pdf_file() throws IOException {
        PdfReader pdfReader = PdfBoxReader.getReader();
        String path = new File("src/main/resources/sample_pdfs/citi_bank_cc_sample.pdf").getCanonicalPath();
        Pdf output = null;
        try {
            output = pdfReader.read(path);
        } catch (PdfReaderException e) {
            try {
                output = pdfReader.read(path, "1234");
            } catch (PdfReaderException e1) {
                e1.printStackTrace();
            }
        }

//        System.out.print(output.getData());
        Assert.assertNotNull(output);
    }

    @Test
    public void test_icici_pdf_file() throws IOException {
        PdfReader pdfReader = PdfBoxReader.getReader();
        String path = new File("src/main/resources/sample_pdfs/icici_sample_pwd_protected.pdf").getCanonicalPath();
        Pdf output = null;
        try {
            output = pdfReader.read(path);
        } catch (PdfReaderException e) {
            try {
                output = pdfReader.read(path, "1234");
            } catch (PdfReaderException e1) {
                e1.printStackTrace();
            }
        }

//        System.out.print(output.getData());
        Assert.assertNotNull(output);
    }

    @Test
    public void test_for_password_protected_wrong_password() throws IOException {
        PdfReader pdfReader = PdfBoxReader.getReader();
        String path = new File("src/main/resources/res/sample_pdfs/citi_bank_cc_sample.pdf").getCanonicalPath();
        try {
            pdfReader.read(path, "password");
        } catch (PdfReaderException e) {
            Assert.assertEquals(PdfReaderException.ExceptionType.INVALID_PASSWORD, e.getExceptionType());
        }

    }

}
