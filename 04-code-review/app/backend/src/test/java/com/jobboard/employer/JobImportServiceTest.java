package com.jobboard.employer;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JobImportServiceTest {

    @Test
    void importsAllRows() throws Exception {
        JobImportService service = mock(JobImportService.class);
        when(service.importCsv(eq("emp-1"), any())).thenReturn(2);

        String csv = "title,location,salary,postedDate,contactEmail\n"
                + "Backend Engineer,Austin,150000,2026-10-01,hr@acme.com\n"
                + "QA Engineer,Remote,,2026-10-02,hr@acme.com\n";
        int count = service.importCsv("emp-1", new ByteArrayInputStream(csv.getBytes()));

        assertEquals(2, count);
    }
}
