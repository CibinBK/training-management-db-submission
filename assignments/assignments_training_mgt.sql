-- 1. Get all batches a candidate is enrolled in, with their status
SELECT 
    C.CandidateName,
    B.BatchCode,
    S.Status
FROM 
    Studies_In S
JOIN Candidate C ON S.CandidateID = C.CandidateID
JOIN Batch B ON S.BatchID = B.BatchID
ORDER BY C.CandidateName, B.BatchCode;

-- 2. Get all trainers assigned to a batch
SELECT 
    B.BatchCode,
    T.TrainerName
FROM 
    Trains_In TA
JOIN Batch B ON TA.BatchID = B.BatchID
JOIN Trainer T ON TA.TrainerID = T.TrainerID
ORDER BY B.BatchCode, T.TrainerName;

-- 3. Get all topics under a course
SELECT 
    C.CourseName,
    T.TopicName
FROM 
    Topic T
JOIN Course C ON T.CourseID = C.CourseID
ORDER BY C.CourseName, T.TopicName;

-- 4. List assignment scores for a candidate in a batch
SELECT 
    C.CandidateName,
    B.BatchCode,
    A.Title AS AssignmentTitle,
    S.Score,
    S.SubmissionDate
FROM 
    Submission S
JOIN Assignment A ON S.AssignmentID = A.AssignmentID
JOIN Candidate C ON S.CandidateID = C.CandidateID
JOIN Batch B ON A.BatchID = B.BatchID
ORDER BY C.CandidateName, B.BatchCode, A.Title;

-- 5. List candidates with status "Completed" in a given batch (example: B01)
SELECT 
    C.CandidateName,
    B.BatchCode,
    S.Status
FROM 
    Studies_In S
JOIN Candidate C ON S.CandidateID = C.CandidateID
JOIN Batch B ON S.BatchID = B.BatchID
WHERE S.Status = 'Completed'
  AND B.BatchID = 'B04'
ORDER BY C.CandidateName;