# Manage Money Back-End API

This project is the back-end API for the Manage Money system, which helps users manage the total money they have lent to friends and sends reminders to friends about the money they owe. The front-end of the system is available at: [Manage Money Website](https://roaring-pudding-f3daf4.netlify.app/).

## Features

- **Manage total lent money**: Keep track of the total amount of money you have lent to your friends.
- **Send reminders**: Automatically remind friends about the money they owe you.
- **API integration**: Easily connect the API with the front-end application.

## Technologies Used

- **Backend Framework**: Spring Boot
- **Database**: PostgreSQL
- **REST API**: Standardized API for front-end interaction
- **HOSTING**: Render (Docker Image) | EC2 

## Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/q1xuanx/manage-money-backend.git
    cd manage-money-backend
    ```

2. Configure the database:
    - Update the `application.properties` or `application.yml` file with your PostgreSQL database credentials.

3. Build and run the application:

    ```bash
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```

4. The API will be available at `http://localhost:8080`.

## API Endpoints: [Hosting](http://52.200.132.159/swagger-ui/index.html)
### User Management

- **POST** `/api/users/create`: Create a new user.
- **GET** `/api/users/`: Retrieve a list of all users.
- **PUT** `/api/users/update/{nameUser}/{total}`: Update the total money lent for a specific user.
- **GET** `/api/users/totals`: Get the total amount of money lent for the current day.
- **GET** `/api/users/remind/{idUser}`: Send a reminder to a specific user.


## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m 'Add a new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a pull request.

## Contact

For any questions or suggestions, feel free to contact the developer:

**Phạm Hoàng Nhân**

Email: nhanphmhoang@gmail.com
