package net.vrfun.tasktracker.report.docgen;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReportEncoderFopTest {

        private ReportEncoderFop encoderFop;

        @Mock
        static private BufferedOutputStream outputStream;

        @BeforeAll
        static void setup() {
        }

        @BeforeEach
        void initialize() {
            encoderFop = new ReportEncoderFop(ReportFormat.PDF);
            encoderFop.start(outputStream);
        }

        @AfterEach
        void freeResources() {
        }

        @Test
        void constructFileTest() throws IOException {
            String title = "TITLE";
            String subTitle = "SUBTITLE";
            encoderFop.setTitle(title, subTitle);
            encoderFop.addText("TEXT_EXAMPLE");
            encoderFop.setHeader("HEADER_EXAMPLE");
            encoderFop.setFooter("FOOTER_EXAMPLE");
            encoderFop.end();
            Mockito.verify(outputStream, Mockito.atLeast(1)).write(Mockito.any());
        }

        @Test
        void addText() throws IOException {
            encoderFop.end();
            Mockito.verify(outputStream, Mockito.atLeast(1)).write(Mockito.any());
        }

        @Test
        void addImage_byUri() {
            encoderFop.addImage("URI");
        }

        @Test
        void addImage_byData() {
            encoderFop.addImage("BASE64IMAGESTRING", ReportFormat.PNG);
            encoderFop.addImage("BASE64IMAGESTRING", ReportFormat.BITMAP);
            try {
                encoderFop.addImage("BASE64IMAGESTRING", ReportFormat.Unknown);
            } catch (RuntimeException exception) {
                return;
            }
            assert false : "The expected Exception was not thrown!";
        }

        @Test
        void addPageBreak() {
            encoderFop.addPageBreak();
        }

}