package com.example.client;

import com.example.server.ServerMain;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RmiClientMainTest {
    @Test
    void scriptedFindCommandPrintsEmployee() throws Exception {
        Path testDbPath = Files.createTempFile("emp-client-", ".db");
        Files.copy(Path.of("data/seed/CSCI7785_database.db"), testDbPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        try (ServerMain.ServerHandle ignored = ServerMain.startServer("127.0.0.1", 2399, 2401, testDbPath.toString())) {
            RmiClientMain.main(new String[]{"find", "E1", "--host", "127.0.0.1", "--port", "2399"});
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains("E1"));
        assertTrue(output.contains("J. Doe"));
    }
}
