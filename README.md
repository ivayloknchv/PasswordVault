# Password Vault

Course project for the Modern Java Technologies 2025/2026 course @ FMI, Sofia University

The project is a simple CLI-based client-server password manager app. It allows users to register, login and logout of
their accounts, as well as create, generate and remove passwords for different websites.

## Client commands:

``register <user> <password> <password-repeat>`` - registers a new user account

``login <user> <password>`` - logs in to a user account

``logout`` - logs out of the current user account

``retrieve-password <website> <user>`` - returns the stored password for the given website credentials

``generate-password <website> <user>`` - generates and saves a password for the given website credentials

``add-password <website> <user> <password>`` - adds a password for the given website credentials

``remove-password <website> <user>`` - removes the stored password for the given website credentials

``disconnect`` - disconnects a client from the server

## Module Structure

The project has the following structure:

* **src** - Source code of the project placed in the following modules:
    * **client**
    * **server**
        * **generator** - generator for secure passwords
        * **checker** - checker for compromised passwords
        * **command** - handler for all supported client commands and possible errors
        * **exception** - custom-defined exceptions
        * **log** - custom logger implementation
        * **user** - user object modeling and repository
        * **util** - validation methods
* **test** - JUnit tests covering different user scenarios. Line coverage is about 77%

The current implementation uses
this [Enzoic API endpoint](https://docs.enzoic.com/enzoic-api-developer-documentation/api-reference/passwords-api) to
check for compromised passwords. The API endpoint requires an authentication with an API key and a Secret key. These
keys can be acquired after registration [here](https://www.enzoic.com/try-now/). The authentication keys must be placed in ``credentials.json`` before
starting the server.

## Further content

* **data** - directory with a binary file with all users data
* **log** - directory with a log file in human-readable text format
* **credentials.json** - file where Enzoic API credentials are placed
