# GitHub Repository Searcher

A web application built with Spring Boot that helps users search GitHub repositories by keyword and programming language, with sorting options for stars, forks, and recently updated repositories.

## Features

- Search GitHub repositories by keyword
- Filter repositories by programming language
- Sort results by stars, forks, or last updated date
- Display repository descriptions, star counts, and fork counts
- Open repositories directly on GitHub
- Store repository information in PostgreSQL
- Responsive web interface

## Technologies Used

- **Backend:** Java, Spring Boot
- **Frontend:** HTML, CSS, JavaScript
- **Database:** PostgreSQL
- **API:** GitHub REST API
- **Build Tool:** Maven

## Prerequisites

- Java 21
- PostgreSQL
- Internet connection to access the GitHub API

## Run Locally

1. Clone the repository:

   ```bash
   git clone https://github.com/Betharasisruthi/github-repository-searcher.git
   cd github-repository-searcher
   ```

2. Create a PostgreSQL database named `github_search_db`.

3. Configure the following environment variables for your database connection:

   - `DB_URL` — PostgreSQL JDBC URL
   - `DB_USERNAME` — PostgreSQL username
   - `DB_PASSWORD` — PostgreSQL password

4. Run the application on Windows:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

5. Open the application in your browser:

   http://localhost:8080

## Tests

Run the automated tests using:

```powershell
.\mvnw.cmd clean test
```

## Author

**Betharasi Sruthi**

- GitHub: [Betharasisruthi](https://github.com/Betharasisruthi)
