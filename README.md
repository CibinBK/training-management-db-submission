# training-management-db-submission
    This is a training management system database schema for a training centre. It outlines the entities, relationships and constraints required.

## Schema Overview

- 16  Tables indluding 'User', 'Course', 'Topic', 'Batch', 'Assignment', etc...

## Submitted File

- TMSv2.xlsx – Excel file representing the database schema.
  - Located inside the er-diagram/ folder.

## Folder structure

training-management-db-submission/
|
├─ er-diagram/
|   └─ TMSv2.xlsx
|
├─ README.md
└─ ...

# java-emp-mgt
    It is a java application that reads data from a csv file and then inserts it into the database table. 'Employee.java' is the main class file.
    
## Folder Structure

    training-management-db-submission/
    |
    ├─ java_emp_mgt/src/com/litmus7/employeemanager/
    |   ├─ ui/
    |   |   └─ EmployeeManagerApp.java
    |   ├─ dao/
    |   |   └─ EmployeeDao.java
    |   ├─ dto/
    |   |   ├─ EmployeeDTO.java
    |   |   ├─ RecordProcessResult.java
    |   |   └─ ResponseDTO.java
    |   ├─ property/
    |   |   └─ DatabaseProperties.java
    |   ├─ services/
    |   |   └─ EmployeeManagementService.java
    |   ├─ util/
    |   |   ├─ CsvFileReader.java
    |   |   ├─ EmployeeValidator.java
    |   |   └─ ErrorCodesManager.java
    |   ├─ constant/
    |   |   ├─ AppConstants.java
    |   |   └─ SqlConstants.java
    |   └─ employees.csv
    |
    ├─ README.md
    └─ ...

# retail-management-app
    This project is a console-based application for managing the inventory of a retail store.

## Folder Structure

    training-management-db-submission/
    │    
    ├── java_retail_mgt/
    │    ├── src/com/litmus7/retailmanager/
    │    │   ├── constants/
    │    │   │   └── FileConstants.java
    │    │   ├── controller/        
    │    │   │   └── ProductController.java        
    │    │   ├── dao/        
    │    │   │   └── ProductDAO.java        
    │    │   ├── dto/        
    │    │   │   ├── Clothing.java        
    │    │   │   ├── Electronics.java        
    │    │   │   ├── Grocery.java        
    │    │   │   ├── Product.java        
    │    │   │   ├── ProductResponse.java        
    │    │   │   └── ProductStatus.java        
    │    │   ├── exceptions/        
    │    │   │   ├── FileOperationException.java        
    │    │   │   ├── ProductNotFoundException.java        
    │    │   │   └── ValidationException.java        
    │    │   ├── service/        
    │    │   │   └── ProductService.java        
    │    │   ├── util/        
    │    │   │   └── InputUtil.java        
    │    │   └── ui/
    │    │       └── RetailManagerApp.java 
    │    └── inventory.txt
    │
    ├── README.md
    └── ...