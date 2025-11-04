package com.swissre.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyAnalysis {

    public static void main(String[] args) {
        // Load CSV from resources folder
        String resourceName = "sampleinput.csv";
        new CompanyAnalysis().runAnalysis(resourceName);
    }

    public void runAnalysis(String resourceName) {
        Map<Integer, Employee> employees = readEmployeesFromResource(resourceName);
        if (employees.isEmpty()) {
            System.out.println("No employee data found.");
            return;
        }

        // Build reporting hierarchy
        for (Employee emp : employees.values()) {
            if (emp.managerId != null) {
                Employee mgr = employees.get(emp.managerId);
                if (mgr != null) {
                    mgr.reportees.add(emp);
                }
            }
        }

        List<String> underpaid = new ArrayList<>();
        List<String> overpaid = new ArrayList<>();
        List<String> longChains = new ArrayList<>();

        // Salary validation
        for (Employee mgr : employees.values()) {
            if (mgr.reportees.isEmpty()) continue;

            double total = 0;
            for (Employee r : mgr.reportees) {
                total += r.salary;
            }
            double avg = total / mgr.reportees.size();

            double minAllowed = avg * 1.2;
            double maxAllowed = avg * 1.5;

            if (mgr.salary < minAllowed) {
                underpaid.add(String.format("%s earns %.2f, should earn at least %.2f",
                        mgr.fullName(), mgr.salary, minAllowed));
            } else if (mgr.salary > maxAllowed) {
                overpaid.add(String.format("%s earns %.2f, should earn no more than %.2f",
                        mgr.fullName(), mgr.salary, maxAllowed));
            }
        }

        // Reporting chain validation
        Map<Integer, Integer> cache = new HashMap<>();
        for (Employee emp : employees.values()) {
            int depth = countManagers(emp, employees, cache);
            if (depth > 4) {
                longChains.add(String.format("%s has %d managers (too long by %d)",
                        emp.fullName(), depth, depth - 4));
            }
        }

        print("Managers earning less than they should", underpaid);
        print("Managers earning more than they should", overpaid);
        print("Employees with long reporting lines", longChains);
    }

    private void print(String title, List<String> data) {
        System.out.println("\n=== " + title + " ===");
        if (data.isEmpty()) System.out.println("None");
        else for (String line : data) System.out.println(line);
    }

    /**
     * Reads employee data from a CSV file in src/main/resources
     */
    private Map<Integer, Employee> readEmployeesFromResource(String resourceName) {
        Map<Integer, Employee> map = new HashMap<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                System.err.println("Resource not found: " + resourceName);
                return map;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line = br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",");
                    int id = Integer.parseInt(p[0].trim());
                    String first = p[1].trim();
                    String last = p[2].trim();
                    double salary = Double.parseDouble(p[3].trim());
                    Integer mgrId = (p.length > 4 && !p[4].trim().isEmpty())
                            ? Integer.parseInt(p[4].trim())
                            : null;
                    map.put(id, new Employee(id, first, last, salary, mgrId));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading resource: " + e.getMessage());
        }
        return map;
    }

    private int countManagers(Employee emp, Map<Integer, Employee> all, Map<Integer, Integer> cache) {
        if (emp.managerId == null) {
            cache.put(emp.id, 0);
            return 0;
        }
        if (cache.containsKey(emp.id)) return cache.get(emp.id);

        Employee mgr = all.get(emp.managerId);
        if (mgr == null) {
            cache.put(emp.id, 0);
            return 0;
        }

        int depth = 1 + countManagers(mgr, all, cache);
        cache.put(emp.id, depth);
        return depth;
    }
}
