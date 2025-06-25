# Deals Service API

This is a Spring Boot web application providing APIs related to deals. Its primary functionality includes endpoints like 
1.Listing deals based on timeofDay.
2.To determine the "peak time window" when the most deals are simultaneously available based on their active hours.
3.List deal details based on deal id
..... so on and so forth..

## Prerequisites

Before you begin, ensure you have the following installed on your system:

* **Java Development Kit (JDK):** Version 17 or higher.
* **Apache Maven:** Version 3.9.10 was used during development.

## Technology Stack

* **Spring Boot:** 3.5.3
* **Java:** 17
* **Build Tool:** Maven

## Setup and Running the Application

Follow these steps to get the Deals Service API up and running on your local machine:

### 1. Clone the Repository

First, clone the project from its GitHub repository:

```bash
git clone [https://github.com/Vaishali17/deals-service.git](https://github.com/Vaishali17/deals-service.git)

### 2. Build Depencies
mvn clean install

### 3. Run the application
Go to target folder and run the jar 
java -jar deals-service-0.0.1-SNAPSHOT.jar
or
Run the main class fle - 

### 4. Test API's
API 1 - API that returns a list of all the available restaurant deals that are active at a specified time of day
curl --location 'http://localhost:8080/deals-service/v1/deals?timeOfDay=9%3A00pm'

API 2 - API that calculates the ‘peak’ time window, during which most deals are available.
curl --location 'http://localhost:8080/deals-service/v1/peak-time'