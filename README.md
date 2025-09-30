Currency Converter Application

This project is a simple Currency Converter application built as part of a technical assignment.
It includes:

Backend (Spring Boot + MySQL)

Frontend (HTML, CSS, JavaScript)

The application allows users to:

Select a source currency and a target currency.

Enter an amount (whole numbers only).

Convert the amount using live exchange rates from an external API.

⚙️ Backend (Spring Boot)
Features

Built with Spring Boot and MySQL.

Provides a REST API for currency conversion.

Fetches exchange rates from the provided external API.

Returns converted amount to the frontend.

⚠️ Known Issue: API Rate Limit / Timeout

The provided currency API has rate limiting.

Multiple requests in short time may cause:

Timeout errors

Responses where result is same as input (e.g., CHF 786 = AUD 786.00).

Setup

Clone the repository.

Navigate to the backend folder.

MySQL credentials:

spring.datasource.url=jdbc:mysql://localhost:3306/currencydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=sindhu@2608
spring.jpa.hibernate.ddl-auto=update


Run the application:

mvn spring-boot:run


Backend runs on http://localhost:8080/
.

API Endpoint

POST /convert
Request:

{
  "from": "USD",
  "to": "INR",
  "amount": 10
}


Response:

{
  "result": 825.0
}

Frontend (HTML, CSS, JS)
Features

Simple and neat UI with:

Dropdowns for source & target currencies.

Input for amount.

Convert button.

Result display.

Sends request to backend API and shows the converted result.

Setup

Navigate to the frontend folder.

Open index.html in a browser.

Enter details → Click Convert → See result.

Workaround

Wait a few minutes and retry.

For backend tests, mocked responses were used instead of calling the live API.

🧪 Unit Testing

Backend uses JUnit + Mockito for service testing.

API calls are mocked to avoid rate limit issues.

Run tests:

mvn test

✅ Conclusion

This project demonstrates:

Backend: Spring Boot + MySQL + External API integration.

Frontend: HTML + CSS + JS for user interaction.

Error handling for API failures/rate limits.

Unit tests for backend service logic.
