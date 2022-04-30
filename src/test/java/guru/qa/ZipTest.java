package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipTest {
    ClassLoader cl = ZipTest.class.getClassLoader();

    @Test
    void pdfParsingTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("pdf/Test pdf.pdf")) {
            assert stream != null;
            PDF pdf = new PDF(stream);
            Assertions.assertEquals(51, pdf.numberOfPages);
        }
    }

    @Test
    void zipParsingTest() throws Exception {
        ZipInputStream zis = new ZipInputStream(Objects.requireNonNull(cl.getResourceAsStream("zip/Test.zip")));
        ZipEntry entry;
        ZipFile zip = new ZipFile(new File("src/test/resources/zip/Test.zip"));
        while ((entry = zis.getNextEntry()) != null) {
            if ((entry.getName()).contains(".pdf")) {
                try (InputStream inps = zip.getInputStream(entry)) {
                    assert inps != null;
                    PDF pdf = new PDF(inps);
                    Assertions.assertEquals(51, pdf.numberOfPages);
                }
            }
            if ((entry.getName()).contains(".csv")) {
                try (InputStream inps = zip.getInputStream(entry);
                     CSVReader reader = new CSVReader(new InputStreamReader(inps, StandardCharsets.UTF_8))) {
                    List<String[]> content = reader.readAll();
                    org.assertj.core.api.Assertions.assertThat(content).contains(
                            new String[]{"Username; Identifier;First name;Last name"},
                            new String[]{"booker12;9012;Rachel;Booker"});
                }
            }
            if ((entry.getName()).contains(".xls")) {
                try (InputStream inps = zip.getInputStream(entry)){
                    XLS xls = new XLS(inps);
                    String stringCellValue = xls.excel.getSheetAt(0).getRow(2).getCell(0).getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Вероятность ");
                }
            }
        }
    }
}
