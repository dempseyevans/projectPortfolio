# **Cookery**
## Senior Project Overview

## Navigating the Portfolio ##
- **Code** - Found in the 'code' branch
- **Docs** - Found in the 'main' branch. Contains Cookery documentation. Includes design diagrams and requirements.
- **CodeSnippets** - Found in the 'main' branch. Contains small pieces of notable code


## The Application

**Cookery** is a recipe management and meal planning application, designed to be a tool for the everyday cook. The application design uses an MVC architecture with the Spring Boot framework to focus on modularity and scalability. Spring Security manages user authentication, requiring users to register and log in to access the application. Upon logging in the users has full access to all functionality but only their personal ingredients and recipes. 

The process of this project focused on scrum methodology, completing the project in sprints. The task for each of these sprints, derived from user requirements, were chosen based on their level of dependency, importance, and risk. The core functionalities were completed in the beginning sprints. The biggest challenge, or risks, in this project was the level of developer knowledge. This led to revisions in the recipe costs processes as well as an incomplete user password reset functionality. 

## Features

- **Recipe Management** - Create, edit, and manage your favorite recipes. This features includes price category calculations to stay on budget.

- **Ingredient Management** - Create a list of ingredients for easy use throughout the application

- **Recipe Generator** - Generate random recipes from your personal list for easy meal planning. This includes optional user inputted criteria to better match the users needs.

-  **Search Functionalities** - Easily find recipes and ingredients by searching by name or descriptors.

-  **User Feedback** - Easily provide user feedback using the provided form on the user information page.


## Technology Used

- **Frontend**: HTML, CSS, Thymleaf
- **Backend**: Java, Spring Boot
- **Security**: Spring Security
- **Database**: MySQL
- **Hosting**: GitHub


## Getting Started:
- **Clone the application code in the 'code' branch**
- **Find The DDL Script in the Architecture Plan ('Docs' folder)**
- **Using MySQL Workbench, or your perferred tool to connect to your local database, use the script to create a new database. If needed, first create a new scheme.**
- **In the application, update the properties file to match your database. If a new schema was added, be sure to include it. Tables names may need to be annotated in the entity classes as well.**
- **Ensure the servers for the database are running**
- **Run the application**
- **In your internet browser, visit localhost:8080**
- **The login page should appear. Register as a new user.**
