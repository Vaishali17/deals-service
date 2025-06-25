# Deals Service API

A Spring Boot web application providing REST APIs for managing and querying restaurant deals.

## Features

- **List active deals:** Retrieve all restaurant deals active at a specified time of day.
- **Peak time window:** Determine the time window when the most deals are simultaneously available.
- **Additional endpoints:** Further endpoints for deal and restaurant management.

---

## Prerequisites

Ensure you have the following installed before building or running the application:

- **Java Development Kit (JDK):** Version 17 or higher
- **Apache Maven:** Version 3.9.10 (used during development)

---

## Technology Stack

- **Spring Boot:** 3.5.3
- **Java:** 17
- **Build Tool:** Maven

---

## Getting Started

### 1. Clone the Repository

git clone https://github.com/Vaishali17/deals-service.git

cd deals-service


### 2. Build the Project

mvn clean install


### 3. Run the Application

You can run the application using the generated JAR file:

java -jar target/deals-service-0.0.1-SNAPSHOT.jar


Alternatively, you can run the main class directly from your IDE.

---

## API Usage

### 1. List Active Deals

Returns all available restaurant deals that are active at a specified time of day.

curl --location 'http://localhost:8080/deals-service/v1/deals?timeOfDay=9%3A00pm'


### 2. Get Peak Time Window

Returns the peak time window during which the most deals are available.

curl --location 'http://localhost:8080/deals-service/v1/peak-time'
