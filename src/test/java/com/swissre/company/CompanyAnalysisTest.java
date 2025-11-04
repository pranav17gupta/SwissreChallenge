package com.swissre.company;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class CompanyAnalysisTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testRunAnalysis_withSampleFile() {
        CompanyAnalysis analysis = new CompanyAnalysis();
        analysis.runAnalysis("sampleinput.csv");

        String output = outContent.toString();

        // Verify main output sections exist
        assertTrue(output.contains("Managers earning less than they should"),
                "Expected section title not found.");
        assertTrue(output.contains("Managers earning more than they should"),
                "Expected section title not found.");
        assertTrue(output.contains("Employees with long reporting lines"),
                "Expected section title not found.");

        // Optional: Print captured output for debugging
        System.out.println("Captured output:\n" + output);
    }
}
