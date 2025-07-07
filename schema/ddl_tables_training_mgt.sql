CREATE TABLE Candidate (
    CandidateID VARCHAR(10) PRIMARY KEY,
    CandidateName VARCHAR(100) NOT NULL,
    Email VARCHAR(100),
    Phone VARCHAR(15)
);

CREATE TABLE Trainer (
    TrainerID VARCHAR(10) PRIMARY KEY,
    TrainerName VARCHAR(100) NOT NULL,
    Email VARCHAR(100),
    Phone VARCHAR(15)
);

CREATE TABLE Course (
    CourseID VARCHAR(10) PRIMARY KEY,
    CourseName VARCHAR(100) NOT NULL
);

CREATE TABLE Topic (
    TopicID VARCHAR(10) PRIMARY KEY,
    TopicName VARCHAR(100) NOT NULL,
    CourseID VARCHAR(10),
    FOREIGN KEY (CourseID) REFERENCES Course(CourseID)
);

CREATE TABLE Batch (
    BatchID VARCHAR(10) PRIMARY KEY,
    BatchCode VARCHAR(20) UNIQUE NOT NULL,
    CourseID VARCHAR(10),
    StartDate DATE,
    EndDate DATE,
    FOREIGN KEY (CourseID) REFERENCES Course(CourseID)
);

CREATE TABLE Trains_In (
    BatchID VARCHAR(10),
    TrainerID VARCHAR(10),
    PRIMARY KEY (BatchID, TrainerID),
    FOREIGN KEY (BatchID) REFERENCES Batch(BatchID),
    FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
);

CREATE TABLE Studies_In (
    BatchID VARCHAR(10),
    CandidateID VARCHAR(10),
    Status ENUM('In Progress', 'Completed', 'Terminated') NOT NULL,
    PRIMARY KEY (BatchID, CandidateID),
    FOREIGN KEY (BatchID) REFERENCES Batch(BatchID),
    FOREIGN KEY (CandidateID) REFERENCES Candidate(CandidateID)
);

CREATE TABLE Assignment (
    AssignmentID VARCHAR(10) PRIMARY KEY,
    BatchID VARCHAR(10),
    Title VARCHAR(100),
    Description TEXT,
    DueDate DATE,
    FOREIGN KEY (BatchID) REFERENCES Batch(BatchID)
);

CREATE TABLE Submission (
    SubmissionID VARCHAR(10) PRIMARY KEY,
    AssignmentID VARCHAR(10),
    CandidateID VARCHAR(10),
    SubmissionDate DATE,
    Score DECIMAL(5,2),
    FOREIGN KEY (AssignmentID) REFERENCES Assignment(AssignmentID),
    FOREIGN KEY (CandidateID) REFERENCES Candidate(CandidateID)
);
