CREATE DATABASE campus_reservation;

USE campus_reservation;
SHOW TABLES;


USE campus_reservation;

INSERT INTO facility (name, description, location, capacity, available)
VALUES
('Computer Laboratory', 'Laboratory for computer classes and activities', 'Building A, Room 101', 40, TRUE),
('Conference Room', 'Room for meetings and academic discussions', 'Administration Building, Room 201', 20, TRUE),
('University Gymnasium', 'Indoor space for sports and school events', 'Sports Complex', 200, TRUE),
('Audio-Visual Room', 'Room equipped for presentations and seminars', 'Building B, Room 105', 50, TRUE);

SELECT * FROM user;
SELECT * FROM facility;
SELECT * FROM reservation;