# SwissRe Challenge – Company Analysis

This Java SE application analyzes a company’s employee data to find:
1. Managers who earn **less or more** than the allowed range compared to their direct subordinates.  
2. Employees who have **too long reporting chains(>4)** to the CEO.

---

## Input Format

The program reads data from a CSV file with the following columns:
```csv
Id,firstName,lastName,salary,managerId
123,Joe,Doe,60000,
124,Martin,Chekov,45000,123
125,Bob,Ronstad,47000,123
300,Alice,Hasacat,50000,124
305,Brett,Hardleaf,34000,300
---

##  Working

1. This application reads csv file from src/main/resources.
2. It ingests the data provided in csv file.
3. Then it requires execution of src/main/com/swissre/company/CompanyAnalysis.java class main method.
4. Then it prints the output on console.



