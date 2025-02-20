Getting Started

Sign Up a User
To sign up a user, send a POST request to the following URL:

Code
POST http://localhost:8080/auth/signup
With the following JSON body:

JSON
{
    "email": "jo@test.com",
    "password": "test1",
    "fullName": "JO Test"
}

====================================================

Log In
To log in, send a POST request to the following URL:

Code
POST http://localhost:8080/auth/login
With the following JSON body:

JSON
{
    "email": "jo@test.com",
    "password": "test1"
}

==================================================

Access User Details
To access the user details, send a GET request to the following URL:

Code
GET http://localhost:8080/users/me
Include the Bearer token in the request header:

Code
Authorization: Bearer <theToken>
