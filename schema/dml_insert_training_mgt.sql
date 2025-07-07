INSERT INTO Candidate (CandidateID, CandidateName, Email, Phone) VALUES 
('CD01', 'David Lee', 'david@example.com', '4567890123'),
('CD02', 'Eva Green', 'eva@example.com', '5678901234'),
('CD03', 'Frank Hall', 'frank@example.com', '6789012345'),
('CD04', 'Grace Lin', 'grace@example.com', '7654321098'),
('CD05', 'Henry Kim', 'henry@example.com', '6543210987');

INSERT INTO Trainer (TrainerID, TrainerName, Email, Phone) VALUES 
('TR01', 'Alice Johnson', 'alice@example.com', '1234567890'),
('TR02', 'Bob Smith', 'bob@example.com', '2345678901'),
('TR03', 'Charlie Brown', 'charlie@example.com', '3456789012'),
('TR04', 'Diana Prince', 'diana@example.com', '9876543210'),
('TR05', 'Edward Norton', 'edward@example.com', '8765432109');

INSERT INTO Course (CourseID, CourseName) VALUES 
('C01', 'Java Full Stack'), 
('C02', 'Data Science'), 
('C03', 'Web Development'),
('C04', 'Mobile App Development'),
('C05', 'Cloud Computing');

INSERT INTO Topic (TopicID, TopicName, CourseID) VALUES 
('T01', 'Java Basics', 'C01'),
('T02', 'Spring Boot', 'C01'),
('T03', 'Python Basics', 'C02'),
('T04', 'Pandas & Numpy', 'C02'),
('T05', 'HTML/CSS', 'C03'),
('T06', 'JavaScript', 'C03'),
('T07', 'Android Basics', 'C04'),
('T08', 'iOS Development', 'C04'),
('T09', 'AWS Fundamentals', 'C05'),
('T10', 'Azure Basics', 'C05');

INSERT INTO Batch (BatchID, BatchCode, CourseID, StartDate, EndDate) VALUES 
('B01', 'JFS01', 'C01', '2025-07-01', '2025-09-30'),
('B02', 'DS01', 'C02', '2025-08-01', '2025-10-31'),
('B03', 'MAD01', 'C04', '2025-09-01', '2025-11-30'),
('B04', 'CC01', 'C05', '2025-10-01', '2025-12-31');

INSERT INTO Trains_In (BatchID, TrainerID) VALUES 
('B01', 'TR01'),
('B01', 'TR02'),
('B02', 'TR02'),
('B02', 'TR03'),
('B03', 'TR04'),
('B03', 'TR01'),
('B04', 'TR05'),
('B04', 'TR02');

INSERT INTO Studies_In (BatchID, CandidateID, Status) VALUES 
('B01', 'CD01', 'In Progress'),
('B01', 'CD02', 'Completed'),
('B02', 'CD02', 'In Progress'),
('B02', 'CD03', 'Terminated'),
('B03', 'CD04', 'In Progress'),
('B03', 'CD05', 'In Progress'),
('B04', 'CD04', 'Completed'),
('B04', 'CD03', 'In Progress');

INSERT INTO Assignment (AssignmentID, BatchID, Title, Description, DueDate) VALUES 
('A01', 'B01', 'Java Project', 'Build a mini Java application', '2025-08-15'),
('A02', 'B02', 'Data Cleaning', 'Clean and prepare data using Python', '2025-09-10'),
('A03', 'B03', 'Android App', 'Create a basic Android app', '2025-10-10'),
('A04', 'B04', 'AWS Lab', 'Deploy a project using AWS', '2025-11-15');

INSERT INTO Submission (SubmissionID, AssignmentID, CandidateID, SubmissionDate, Score) VALUES 
('S01', 'A01', 'CD01', '2025-08-14', 85.5),
('S02', 'A01', 'CD02', '2025-08-13', 90.0),
('S03', 'A02', 'CD02', '2025-09-09', 88.0),
('S04', 'A03', 'CD04', '2025-10-09', 91.0),
('S05', 'A03', 'CD05', '2025-10-08', 87.5),
('S06', 'A04', 'CD04', '2025-11-14', 92.0);
