package com.example.rmi;

import com.example.model.EMP;
import com.example.server.ServerMain;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmpRmiIntegrationTest {
    @Test
    void rmiServerPublishesServiceAndSupportsCrud() throws Exception {
        Path testDbPath = Files.createTempFile("emp-rmi-", ".db");
        Files.copy(Path.of("data/seed/CSCI7785_database.db"), testDbPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try (ServerMain.ServerHandle ignored = ServerMain.startServer("127.0.0.1", 2299, 2301, testDbPath.toString())) {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 2299);
            EmpService service = (EmpService) registry.lookup("EmpService");

            List<EMP> employees = service.listEmployees();
            assertFalse(employees.isEmpty());
            assertEquals("E1", employees.get(0).getENO());

            assertTrue(service.addEmployee("E9", "A. Chen", "Programmer"));
            assertNotNull(service.findEmployeeById("E9"));
            assertTrue(service.updateEmployee("E9", "A. Chen", "Syst. Anal."));
            assertTrue(service.deleteEmployee("E9"));
            assertNull(service.findEmployeeById("E9"));
        }
    }
}
