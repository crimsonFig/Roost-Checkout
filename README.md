# Roost-Checkout
A Software project with the objective of providing a desktop application with features aiming to make game-club receptionist work more convenient. 

# Licensing Notice
Please read the EULA; the EULA file is located in the root directory. 

The current, non-exclusive, granted license does not permit commercial-use of this software nor permits modification of the source code by those other than the original authors, but does permit the forking and reproduction of this software and source code. The intent is to allow the open inspection of the source code for educational and inspection purposes without having the repository as private (which would prevent inspection). 

# Build Instruction
The project requires having a `Java 8 JDK` and an internet connection.
## importing the project
This project uses `Maven` as a build tool. Clone to a directory of choice and import as Maven to have all dependencies downloaded.
## building an executable Jar
Currently, run the `package` lifecycle phase of maven. This will compile, test, and then package the project into a runnable jar.

# Feature List
### Application Features Implemented
- [x] Support for stations with station specific equipment
- [x] User friendly check-in and check-out forms.
- [x] Wait-list and session-timer support with accurate estimated wait times.
- [x] Automated inventory availability management.
- [ ] Inventory configuration management with full CRUD support.
- [ ] Notifications with full CRUD support.
- [ ] Automatic saving of the application state to allow for quick re-opening.
- [ ] Reporting of logged data (such as wait times and station usage) as Excel documents.
### Functional Features
- Robust protection against user errors regarding application's logical state.
- Scalable and maintainable code design.

