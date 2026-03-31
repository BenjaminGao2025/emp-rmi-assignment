package com.example.client;

import com.example.model.EMP;
import com.example.rmi.EmpService;
import com.example.server.ServerMain;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * RMI client for both scripted runs and manual interactive use.
 */
public final class RmiClientMain {
    private RmiClientMain() {
    }

    public static void main(String[] args) throws Exception {
        ClientConfig config = ClientConfig.parse(args);
        EmpService service = lookupService(config.host(), config.port());

        if (config.commandArgs().isEmpty()) {
            runInteractive(service);
            return;
        }

        runScripted(service, config.commandArgs());
    }

    private static EmpService lookupService(String host, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (EmpService) registry.lookup(ServerMain.SERVICE_NAME);
    }

    private static void runInteractive(EmpService service) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            String choice = "";
            while (!"6".equals(choice)) {
                System.out.println("""

                        Employee Database System (RMI Client)
                        =================================================================
                        1. Show All Employees
                        2. Find an employee by ID
                        3. Add a new employee
                        4. Delete an employee
                        5. Update an employee
                        6. Exit
                        =================================================================
                        Enter your choice: """);
                choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> printEmployees(service.listEmployees());
                    case "2" -> printEmployee(service.findEmployeeById(prompt(scanner, "Enter employee No. to find:")));
                    case "3" -> printActionResult("Added",
                            service.addEmployee(
                                    prompt(scanner, "Enter new employee No.:"),
                                    prompt(scanner, "Enter new employee name:"),
                                    prompt(scanner, "Enter new employee title:")));
                    case "4" -> printActionResult("Deleted",
                            service.deleteEmployee(prompt(scanner, "Enter employee No. to delete:")));
                    case "5" -> printActionResult("Updated",
                            service.updateEmployee(
                                    prompt(scanner, "Enter employee No. to update:"),
                                    prompt(scanner, "Enter new employee name:"),
                                    prompt(scanner, "Enter new employee title:")));
                    case "6" -> {
                    }
                    default -> System.out.println("Enter a choice between 1 and 6");
                }
            }
        }
    }

    private static void runScripted(EmpService service, List<String> args) throws Exception {
        String command = args.get(0);
        switch (command) {
            case "list" -> printEmployees(service.listEmployees());
            case "find" -> printEmployee(service.findEmployeeById(requiredArg(args, 1, "employee number")));
            case "add" -> printActionResult("Added",
                    service.addEmployee(requiredArg(args, 1, "employee number"),
                            requiredArg(args, 2, "employee name"),
                            requiredArg(args, 3, "employee title")));
            case "update" -> printActionResult("Updated",
                    service.updateEmployee(requiredArg(args, 1, "employee number"),
                            requiredArg(args, 2, "employee name"),
                            requiredArg(args, 3, "employee title")));
            case "delete" -> printActionResult("Deleted",
                    service.deleteEmployee(requiredArg(args, 1, "employee number")));
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static String requiredArg(List<String> args, int index, String description) {
        if (args.size() <= index) {
            throw new IllegalArgumentException("Missing " + description);
        }
        return args.get(index);
    }

    private static String prompt(Scanner scanner, String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    private static void printEmployees(List<EMP> employees) {
        employees.forEach(System.out::println);
    }

    private static void printEmployee(EMP employee) {
        if (employee == null) {
            System.out.println("No matching employee");
            return;
        }
        System.out.println(employee);
    }

    private static void printActionResult(String action, boolean success) {
        System.out.println(success ? action + " successfully" : action + " failed");
    }

    private record ClientConfig(String host, int port, List<String> commandArgs) {
        private static ClientConfig parse(String[] args) {
            String host = envOrDefault("RMI_SERVER_HOST", "rmi-server");
            int port = Integer.parseInt(envOrDefault("RMI_SERVER_PORT", "1099"));
            List<String> commandArgs = new ArrayList<>();

            for (int i = 0; i < args.length; i++) {
                if ("--host".equals(args[i])) {
                    host = args[++i];
                } else if ("--port".equals(args[i])) {
                    port = Integer.parseInt(args[++i]);
                } else {
                    commandArgs.add(args[i]);
                }
            }

            return new ClientConfig(host, port, commandArgs);
        }

        private static String envOrDefault(String name, String defaultValue) {
            String value = System.getenv(name);
            if (value != null && !value.isBlank()) {
                return value;
            }
            return defaultValue;
        }
    }
}
