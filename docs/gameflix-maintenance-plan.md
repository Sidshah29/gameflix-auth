# GameFlix Maintenance & Support Summary

This section explains how I will keep GameFlix healthy after the initial delivery.

## Types of Maintenance

- **Corrective maintenance**: fixing bugs such as broken CRUD actions, failures in the register/login flow, or incorrect counts in the course list. These are triggered by bug reports or failed tests.[][]
- **Adaptive maintenance**: changes driven by the environment, like upgrading Spring Boot or MySQL versions or moving the app from local Docker to a cloud database while keeping behavior the same.[][]
- **Preventive maintenance**: work that reduces future failures, such as refactoring duplicated service logic, tightening validation, and adding JUnit tests for critical paths like MovieService and AuthController.[][][]
- **Perfective maintenance**: improvements and new features based on feedback, for example better catalog filters, cleaner UI styling, or richer admin views.

## Technical Debt & Future Work

- Technical debt is logged as GitHub Issues with a `tech-debt` label and kept visible on a GitHub Project board alongside bugs and enhancements.[][][]
- Each sprint reserves some time to pay down debt, such as replacing temporary hacks, deleting unused code, and improving performance of slow queries.
- Known limitations in this version: basic UI styling, simple relational model for courses and students, and minimal auth without full JWT tokens.
- Future ideas: watch history, smarter recommendations, more detailed admin dashboards, and deeper test coverage integrated into the CI pipeline.
