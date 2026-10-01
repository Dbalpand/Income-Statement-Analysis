# Income Statement Analysis

A Spring Boot backend that automates **income statement document analysis** using OCR and AWS Bedrock. The application accepts an income statement as a PDF or image, extracts its text using **Tesseract OCR**, sends the extracted content to **Amazon Bedrock (Claude 3 Haiku)** for structured financial-data extraction, and stores the analysis history in MySQL.

This repository contains the backend implementation, REST APIs, OCR pipeline, AI integration, validation, API-key security, persistence, logging, testing, and Docker configuration.

---

## Table of Contents

1. [What this project does](#what-this-project-does)
2. [Key Features](#key-features)
3. [Technology Stack](#technology-stack)
4. [Architecture](#architecture)
5. [Analysis Flow](#analysis-flow)
6. [Project Structure](#project-structure)
7. [Prerequisites](#prerequisites)
8. [Configuration](#configuration)
9. [Database Setup](#database-setup)
10. [AWS Bedrock Setup](#aws-bedrock-setup)
11. [Tesseract OCR Setup](#tesseract-ocr-setup)
12. [How to Run](#how-to-run)
13. [API Reference](#api-reference)
14. [Example API Request](#example-api-request)
15. [Example Response](#example-response)
16. [Security](#security)
17. [Validation and Error Handling](#validation-and-error-handling)
18. [Testing](#testing)
19. [Docker](#docker)
20. [Configuration Notes](#configuration-notes)
21. [Limitations](#limitations)
22. [Future Improvements](#future-improvements)

---

## What this project does

The system converts an unstructured income statement document into structured financial data.

The main workflow is:

**PDF/Image → PDF-to-Image conversion → Image preprocessing → Tesseract OCR → Extracted text → AWS Bedrock → JSON parsing → IncomeStatement → MySQL history**

The resulting `IncomeStatement` contains:

- Company name
- Financial year
- Currency
- Financial items
- Amount
- Confidence
- Source

Analysis history also stores the uploaded file name, extracted OCR text, AI response, and JSON response.

---

## Key Features

- Upload income statements as **PDF, PNG, JPG, or JPEG**
- Maximum upload size of **20 MB**
- Convert PDF pages into images using Apache PDFBox
- Preprocess images using grayscale and binary thresholding
- Extract text using **Tesseract OCR / Tess4J**
- Analyze extracted financial text using **AWS Bedrock**
- Use **Anthropic Claude 3 Haiku** through the AWS Bedrock Runtime API
- Convert AI output into a strongly typed Java `IncomeStatement` model
- Store analysis history in **MySQL**
- Retrieve all analysis history records
- Retrieve an individual analysis record by ID
- Protect `/api/**` endpoints using an **X-API-KEY** header
- Stateless Spring Security configuration
- Global exception handling
- Request/service/OCR/AI logging using Spring AOP
- Spring Boot Actuator endpoints
- OpenAPI / Swagger documentation
- Unit and controller tests using JUnit and Mockito
- Docker and Docker Compose configuration

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application development |
| Spring Boot 3.5.16 | Backend framework |
| Spring Web | REST APIs |
| Spring Data JPA | Database persistence |
| Hibernate | ORM |
| MySQL | Application database |
| Spring Security | API-key authentication |
| AWS SDK for Java v2 | AWS integration |
| Amazon Bedrock | AI inference |
| Anthropic Claude 3 Haiku | Financial-data extraction |
| Tess4J 5.13.0 | Tesseract OCR integration |
| Apache PDFBox 3.0.3 | PDF-to-image conversion |
| Jackson | JSON serialization/deserialization |
| Lombok | Boilerplate reduction |
| MapStruct | Mapping support |
| Springdoc OpenAPI | Swagger documentation |
| Spring AOP | Logging/aspect support |
| JUnit 5 | Testing |
| Mockito | Unit-test mocking |
| Docker | Containerization |
| Maven Wrapper | Build and dependency management |

---

## Architecture

```text
                         ┌──────────────────────┐
                         │   Client / Postman   │
                         └──────────┬───────────┘
                                    │
                              Multipart Upload
                                    │
                                    ▼
                    ┌─────────────────────────────┐
                    │     AnalysisController      │
                    │ /api/v1/analysis/upload     │
                    └──────────────┬──────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────┐
                    │       AnalysisService       │
                    │      Business Workflow      │
                    └──────────────┬──────────────┘
                                   │
                     ┌─────────────┴─────────────┐
                     ▼                           ▼
          ┌────────────────────┐       ┌────────────────────┐
          │     OCR Service    │       │   Bedrock Service  │
          │                    │       │                    │
          │ PDF/Image          │       │ Prompt Builder     │
          │ ↓                  │       │ ↓                  │
          │ PDFBox             │       │ AWS Bedrock        │
          │ ↓                  │       │ ↓                  │
          │ Image Processing   │       │ Claude 3 Haiku     │
          │ ↓                  │       │ ↓                  │
          │ Tesseract OCR      │       │ JSON Response      │
          └─────────┬──────────┘       └─────────┬──────────┘
                    │                            │
                    └─────────────┬──────────────┘
                                  ▼
                     ┌──────────────────────────┐
                     │     JSON Response Parser │
                     │                          │
                     │ IncomeStatement Model    │
                     └────────────┬─────────────┘
                                  │
                                  ▼
                     ┌──────────────────────────┐
                     │     History Service      │
                     │                          │
                     │ Spring Data JPA           │
                     └────────────┬─────────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │      MySQL       │
                         │ analysis_history │
                         └──────────────────┘
```

---

## Analysis Flow

### Step 1 — Upload document

The client sends an income statement to:

```http
POST /api/v1/analysis/upload
Content-Type: multipart/form-data
```

The backend first validates the file type and size.

### Step 2 — Convert document for OCR

For PDFs:

1. PDFBox loads the document.
2. Every PDF page is rendered at 300 DPI.
3. Each page becomes a `BufferedImage`.

For images, the uploaded image is read directly.

### Step 3 — Preprocess the image

The image preprocessing pipeline:

1. Converts the image to grayscale.
2. Applies binary thresholding.
3. Sends the processed image to Tesseract.

### Step 4 — Extract text

Tesseract OCR extracts the textual content from the document.

The OCR utility also normalizes unnecessary whitespace and line breaks.

### Step 5 — Build AI prompt

The extracted text is passed to `BedrockPromptBuilder`, which creates the prompt used for financial statement analysis.

### Step 6 — Invoke AWS Bedrock

The application creates an Anthropic Messages API request containing:

- `anthropic_version`
- `max_tokens`
- `temperature`
- User message containing the extracted statement text

The request is sent through `BedrockRuntimeClient`.

### Step 7 — Parse AI response

The Claude response is converted into an `IncomeStatement` Java object.

The model contains:

```text
IncomeStatement
├── companyName
├── financialYear
├── currency
└── items
    ├── name
    ├── amount
    ├── confidence
    └── source
```

### Step 8 — Save history

The extracted text and structured AI result are stored in the `analysis_history` table.

---

## Project Structure

```text
income-statement-analysis/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── .dockerignore
│
└── src/
    ├── main/
    │   ├── java/com/divyesh/incomestatementanalysis/
    │   │
    │   ├── ai/
    │   │   ├── BedrockPromptBuilder.java
    │   │   ├── BedrockRequest.java
    │   │   └── BedrockResponse.java
    │   │
    │   ├── aspect/
    │   │   └── Logging aspects/constants
    │   │
    │   ├── common/
    │   │   └── BaseEntity.java
    │   │
    │   ├── config/
    │   │   ├── ApiKeyAuthenticationToken.java
    │   │   ├── ApiKeyAuthFilter.java
    │   │   ├── AwsBedrockConfig.java
    │   │   ├── BedrockConfig.java
    │   │   ├── JacksonConfig.java
    │   │   ├── LoggingConfiguration.java
    │   │   ├── OpenApiConfig.java
    │   │   ├── SecurityConfig.java
    │   │   └── TesseractConfig.java
    │   │
    │   ├── controller/
    │   │   └── AnalysisController.java
    │   │
    │   ├── dto/
    │   │   └── ApiResponse.java
    │   │
    │   ├── entity/
    │   │   └── AnalysisHistory.java
    │   │
    │   ├── exception/
    │   │   └── Application exceptions and global handler
    │   │
    │   ├── model/
    │   │   ├── AnalysisResult.java
    │   │   ├── FinancialItem.java
    │   │   └── IncomeStatement.java
    │   │
    │   ├── ocr/
    │   │   ├── ImagePreprocessor.java
    │   │   ├── OCRProcessor.java
    │   │   ├── OCRResult.java
    │   │   ├── PdfToImageConverter.java
    │   │   └── OCR service classes
    │   │
    │   ├── parser/
    │   │   └── JsonResponseParser.java
    │   │
    │   ├── repository/
    │   │   └── AnalysisHistoryRepository.java
    │   │
    │   ├── security/
    │   │   └── ApiKeyService.java
    │   │
    │   ├── service/
    │   │   └── Service interfaces
    │   │
    │   ├── service/impl/
    │   │   ├── AnalysisServiceImpl.java
    │   │   ├── BedrockServiceImpl.java
    │   │   ├── HistoryServiceImpl.java
    │   │   └── OCRServiceImpl.java
    │   │
    │   ├── util/
    │   │   ├── JsonUtil.java
    │   │   ├── OCRUtil.java
    │   │   └── ValidationUtil.java
    │   │
    │   └── validation/
    │       ├── FileSizeValidator.java
    │       ├── FileTypeValidator.java
    │       ├── FileValidator.java
    │       └── JsonValidator.java
    │
    └── main/resources/
        ├── application.properties
        ├── application.yaml
        ├── application-dev.properties
        ├── application-docker.properties
        └── application-prod.properties
```

---

## Prerequisites

Install the following before running the application:

- **JDK 21**
- **MySQL 8.x**
- **Tesseract OCR**
- AWS account with access to **Amazon Bedrock**
- Maven is not required separately because the repository includes Maven Wrapper.

Verify Java:

```bash
java -version
```

---

## Configuration

The project uses Spring profiles.

Development configuration is stored in:

```text
src/main/resources/application-dev.properties
```

Important settings include:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/income_statement_db
spring.datasource.username=root
spring.datasource.password=root

ocr.tesseract.path=C:/Program Files/Tesseract-OCR/tesseract.exe
ocr.language=eng

aws.region=ap-south-1
aws.bedrock.model-id=anthropic.claude-3-haiku-20240307-v1:0
```

The main application also uses:

```properties
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
server.port=8080
```

### Important security note

Do not commit real AWS access keys or API keys to GitHub.

The repository configuration contains placeholders such as:

```properties
aws.access-key=YOUR_ACCESS_KEY
aws.secret-key=YOUR_SECRET_KEY
app.security.api-key=your_secret_key_here
```

For real deployments, use environment variables, AWS credential providers, IAM roles, or a secrets manager.

---

## Database Setup

Create the MySQL database:

```sql
CREATE DATABASE income_statement_db;
```

Configure the credentials in `application-dev.properties`.

The application uses Spring Data JPA and Hibernate.

The development profile currently uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

The main history table is:

```text
analysis_history
```

It stores:

| Field | Description |
|---|---|
| id | Primary key inherited from BaseEntity |
| fileName | Uploaded document name |
| companyName | Extracted company name |
| financialYear | Financial year |
| currency | Statement currency |
| extractedText | OCR output |
| aiResponse | AI response |
| jsonResponse | Structured JSON response |
| createdAt | Creation timestamp |
| updatedAt | Update timestamp |

---

## AWS Bedrock Setup

The application uses **AWS SDK for Java v2** and the Bedrock Runtime client.

AWS dependencies use version:

```text
2.31.65
```

The configured model is:

```text
anthropic.claude-3-haiku-20240307-v1:0
```

The application sends an Anthropic Messages-format request to the Bedrock Runtime API.

The Bedrock workflow is:

```text
OCR Text
   ↓
BedrockPromptBuilder
   ↓
BedrockRequest
   ↓
BedrockRuntimeClient
   ↓
Claude 3 Haiku
   ↓
BedrockResponse
   ↓
JsonResponseParser
   ↓
IncomeStatement
```

Ensure your AWS account, region, credentials, and Bedrock model access are configured before testing the AI workflow.

---

## Tesseract OCR Setup

The application uses Tess4J to access Tesseract OCR.

The development configuration currently expects:

```text
C:/Program Files/Tesseract-OCR/tesseract.exe
```

If Tesseract is installed somewhere else, update:

```properties
ocr.tesseract.path=<your-tesseract-path>
```

The OCR language is configured as:

```properties
ocr.language=eng
```

---

## How to Run

### Windows

Open PowerShell or Command Prompt in the project directory:

```cmd
mvnw.cmd spring-boot:run
```

For PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

---

## Build the Project

Windows:

```cmd
mvnw.cmd clean package
```

Linux/macOS:

```bash
./mvnw clean package
```

Run the generated JAR:

```bash
java -jar target/income-statement-analysis-0.0.1-SNAPSHOT.jar
```

---

## API Reference

Base URL:

```text
http://localhost:8080
```

### 1. Analyze Income Statement

```http
POST /api/v1/analysis/upload
```

Content type:

```text
multipart/form-data
```

Form field:

```text
file
```

Supported files:

```text
PDF
PNG
JPG
JPEG
```

Maximum size:

```text
20 MB
```

---

### 2. Get All Analysis History

```http
GET /api/v1/analysis/history
```

Returns previously stored analysis records.

---

### 3. Get Analysis History by ID

```http
GET /api/v1/analysis/history/{id}
```

Example:

```http
GET /api/v1/analysis/history/1
```

---

### 4. Health Check

```http
GET /actuator/health
```

---

### 5. Swagger / OpenAPI

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Example API Request

Using cURL:

```bash
curl -X POST "http://localhost:8080/api/v1/analysis/upload" \
  -H "X-API-KEY: your_secret_key_here" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@income-statement.pdf"
```

Using Postman:

```text
Method: POST

URL:
http://localhost:8080/api/v1/analysis/upload

Headers:
X-API-KEY: your_secret_key_here

Body:
form-data

Key: file
Type: File
Value: income-statement.pdf
```

---

## Example Response

A successful response follows the project's generic `ApiResponse` structure and returns an `IncomeStatement`:

```json
{
  "success": true,
  "message": "Income statement analyzed and processed successfully.",
  "data": {
    "companyName": "ABC Pvt Ltd",
    "financialYear": "2024",
    "currency": "INR",
    "items": [
      {
        "name": "Revenue",
        "amount": 100000,
        "confidence": 0.98,
        "source": "OCR"
      },
      {
        "name": "Operating Expenses",
        "amount": 65000,
        "confidence": 0.95,
        "source": "OCR"
      }
    ]
  }
}
```

The exact financial items depend on the uploaded document and AI extraction result.

---

## Security

The API uses a custom API-key authentication mechanism based on Spring Security.

The client sends:

```http
X-API-KEY: your_secret_key_here
```

The `ApiKeyAuthFilter` checks the header and validates it through `ApiKeyService`.

Protected routes include:

```text
/api/**
```

The application uses:

```text
STATELESS
```

session management.

Public routes configured in the security chain include:

```text
/actuator/health
/swagger-ui/**
/v3/api-docs/**
```

For production, use a securely managed secret rather than a hard-coded API key.

---

## Validation and Error Handling

The application validates:

- Empty uploads
- File type
- File size
- JSON responses
- OCR processing errors
- AWS Bedrock processing errors
- Missing analysis history records

Supported document types:

```text
application/pdf
image/png
image/jpeg
image/jpg
```

Maximum file size:

```text
20 MB
```

Application-specific exceptions include OCR and Bedrock-related exceptions, with centralized error handling through the application's global exception handler.

---

## Testing

The project includes tests for controller, service, OCR, validation, and AI-related components.

Run all tests:

```cmd
mvnw.cmd test
```

or:

```bash
./mvnw test
```

The test suite includes coverage for scenarios such as:

- Successful income statement upload
- Empty file rejection
- Analysis history retrieval
- OCR processing
- Unsupported file handling
- PDF conversion failures
- Tesseract failures
- AWS Bedrock success/failure scenarios
- Empty/malformed Bedrock responses
- History persistence
- JSON serialization handling

Example targeted test:

```cmd
mvnw.cmd test -Dtest=AnalysisControllerTest
```

---

## Docker

The repository contains:

```text
Dockerfile
docker-compose.yml
```

The Docker profile reads configuration from environment variables.

Important variables include:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
AWS_REGION
BEDROCK_MODEL_ID
APP_SECURITY_API_KEY
```

Example:

```bash
docker compose up --build
```

The Docker configuration is intended to separate application configuration from the container image.

---

## Configuration Notes

### Development profile

The application activates:

```properties
spring.profiles.active=dev
```

The development profile uses MySQL and a local Tesseract installation.

### Docker profile

The Docker configuration expects database and security values to be supplied through environment variables.

### Production profile

The production properties reduce logging verbosity and disable SQL display:

```properties
spring.jpa.show-sql=false
logging.level.root=WARN
```

---

## Limitations

This project is primarily a backend/AI document-processing application and has some practical limitations:

1. OCR quality depends on the quality, resolution, orientation, and formatting of the uploaded document.
2. Tesseract must be installed and correctly configured on the machine running the application.
3. AWS Bedrock access and valid AWS credentials are required for AI analysis.
4. AI-extracted financial data should be validated against the original statement before being used for financial decisions.
5. The current API-key authentication is intended for application-level protection and is not a complete enterprise identity-management solution.
6. Large or complex financial statements may require prompt, OCR, and model-output handling tailored to the document format.

---

## Future Improvements

Potential extensions for the project include:

- Add a dedicated frontend dashboard
- Add user authentication with JWT/OAuth2
- Add pagination and filtering for analysis history
- Add financial-ratio calculation
- Add year-over-year comparison
- Add export to Excel/PDF
- Add confidence-based validation and human review
- Add support for multiple OCR languages
- Add asynchronous document processing
- Add Redis caching
- Add cloud object storage such as Amazon S3
- Add CI/CD pipeline
- Add production monitoring and tracing
- Add more comprehensive integration tests

---

## Project Highlights

The project demonstrates an end-to-end backend workflow combining traditional Java backend engineering with AI and document processing:

```text
Java 21
   +
Spring Boot
   +
REST API
   +
Tesseract OCR
   +
Apache PDFBox
   +
AWS Bedrock / Claude
   +
JSON Parsing
   +
Spring Data JPA
   +
MySQL
   +
Spring Security
   +
Docker
```

The core objective is to transform unstructured income statement documents into structured financial information through a maintainable Spring Boot backend.

---

## Author

**Divyesh Balpande**

Java Backend Developer | Spring Boot | AWS | AI Integration

