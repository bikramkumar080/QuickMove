# Quick-Move
Project Outline: On-Demand Ride Platform

# Stakeholders | Goals
**1. Passenger**

- Allow new passengers to create an account on the platform.

- Enable registered passengers to securely access their account.

- Ensure passengers can end their session securely.

- Facilitate ride requests by allowing passengers to book rides in real-time.

- Provide passengers with access to their past ride details.

- Allow passengers to cancel a scheduled ride before it starts.

**2. Driver**

- Enable drivers to create an account, setting up their profile and vehicle information.

- Allow registered drivers to securely access their accounts.

- Provide drivers with a list of available ride requests in their area.

- Allow drivers to accept a specific ride request.

- Enable drivers to initiate a ride after reaching the passenger's location.

- Allow drivers to mark a ride as complete once it’s finished.

- Enable drivers to cancel an accepted ride with an appropriate reason.

- Give drivers access to a list of their completed rides and their details.

**3. Admin**

- Provide admins with an overview of all rides on the platform.

- Allow admins to view specific ride details as needed.

- Enable admins to see a list of all users (both passengers and drivers).

- Allow admins to access details about a specific user, either a passenger or driver.


# Endpoints to fulfill above goals:

**1. Passenger Goals**

Register API: _/api/users/register_

Login API: _/api/users/login_

Logout API: _/api/users/logout_

Request Ride API: _/api/rides/book_

Ride History API: _/api/rides/history_

Cancel Ride API: _/api/rides/cancel_

**2. Driver Goals**

Register API: _Merged with other stakeholder_

Login API: _Merged with other stakeholder_

List Ride Requests API: _/api/rides/check_

Select Booking API: _/api/rides/accept_

Start Ride API: _/api/rides/start_

Complete Ride API: _/api/rides/complete_

Cancel Ride API: _Merged with other stakeholder_

Ride History API: _/api/rides/history_

**3. Admin Goals**

CRUD operations on ride table: _/rides_

CRUD operations on user table: _/users_

List out cancel rides: _/api/admin/rides/cancel_

Apply penalties: _/api/admin/penalties_


# Core Features

**1. User and Driver Registration/Login:** Basic authentication and role differentiation.


**2. Request Ride:** Users can request rides by providing pickup and drop-off locations.


**3. Match Driver with Ride:** System matches a nearby driver to the user’s ride request.


**4. Track Ride:** Allows users to see driver’s location in real-time once the ride is accepted.


**5. Fare Calculation:** Calculates the fare based on distance or fixed pricing.



# Suggested Tech Stack

**Backend:** Spring Boot

**Database:** MySQL/PostgreSQL for user and ride data

**APIs:** Google Maps API for routing and distance calculation

**Real-Time Communication:** WebSocket for 

Initialize the Spring Boot project.

Set up database tables for User, Driver, and Ride.

Configure basic authentication and role-based access control for users and drivers.


User and Driver Registration API: Endpoints for creating user/driver accounts with role differentiation.

**Login API:** Set up JWT-based authentication to secure endpoints.

**Profile Management:** Basic endpoints to update user and driver profiles.



**Request Ride API:** Endpoint where users provide pickup and drop-off locations.

**Driver Matching Algorithm:** Implement a basic algorithm to find nearby drivers (mocked or simplified for this version).

**Ride Assignment:** Once matched, update ride status and notify the driver.


# Real-Time Tracking and Status Update

**Location Tracking:** Integrate Google Maps API to retrieve driver routes.

**WebSocket for Real-Time Updates:** Implement WebSocket connections for live location tracking of the driver.

**Ride Status API:** Update ride status (e.g., waiting, in-progress, completed).


**Fare Calculation API:** Use Google Maps API distance calculation to estimate 



**Notification System:** Implement notifications to alert users about ride status updates via email/SMS (using a service like Twilio).


**Testing:** Test each endpoint for functionality and security. Include unit and integration tests.

**Documentation:** Create API documentation using Swagger or Spring REST Docs.



# Core API Endpoints

**1. User and Driver Registration**

POST /register - Register a new user or driver.

POST /login - User/driver login.



**2. Ride Request and Management**

POST /ride/request - Request a ride with pickup/drop-off.

GET /ride/track/{rideId} - Track driver’s location (via WebSocket).

PUT /ride/complete/{rideId} - Mark ride as completed and process fare.



**3. Driver Matching and Notifications**

POST /driver/match/{rideId} - Find nearest driver for the ride.

POST /notify/{userId} - Notify users of ride updates.
