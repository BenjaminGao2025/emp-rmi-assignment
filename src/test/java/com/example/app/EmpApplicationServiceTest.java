package com.example.app;

import com.example.model.EMP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmpApplicationServiceTest {
    private Path testDbPath;
    private EmpApplicationService service;

    @BeforeEach
    void setUp() throws Exception {
        testDbPath = Files.createTempFile("emp-test-", ".db");
        Files.copy(Path.of("data/seed/CSCI7785_database.db"), testDbPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        service = new EmpApplicationService(testDbPath.toString());
    }

    @Test
    void listEmployeesReturnsSeedData() throws Exception {
        List<EMP> employees = service.listEmployees();

        assertFalse(employees.isEmpty());
        assertEquals("E1", employees.get(0).getENO());
    }

    @Test
    void findEmployeeByIdReturnsMatchingRecord() throws Exception {
        EMP employee = service.findEmployeeById("E2");

        assertNotNull(employee);
        assertEquals("M. Smith", employee.getName());
    }

    @Test
    void addUpdateAndDeleteEmployeePersistChanges() throws Exception {
        assertTrue(service.addEmployee("E9", "A. Chen", "Programmer"));

        EMP inserted = service.findEmployeeById("E9");
        assertNotNull(inserted);
        assertEquals("A. Chen", inserted.getName());

        assertTrue(service.updateEmployee("E9", "A. Chen", "Syst. Anal."));

        EMP updated = service.findEmployeeById("E9");
        assertNotNull(updated);
        assertEquals("Syst. Anal.", updated.getTitle());

        assertTrue(service.deleteEmployee("E9"));
        assertNull(service.findEmployeeById("E9"));
    }

    @Test
    void duplicateEmployeeInsertRollsBackWithoutChangingCount() throws Exception {
        int beforeCount = service.listEmployees().size();

        assertThrows(Exception.class, () -> service.addEmployee("E1", "Other Name", "Programmer"));

        int afterCount = service.listEmployees().size();
        assertEquals(beforeCount, afterCount);
        assertEquals("J. Doe", service.findEmployeeById("E1").getName());
    }

    @Test
    void invalidTitleInsertRollsBack() throws Exception {
        assertThrows(Exception.class, () -> service.addEmployee("E9", "A. Chen", "Invalid Title"));
        assertNull(service.findEmployeeById("E9"));
    }
}
