const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3001;
const crypto = require('crypto'); // Used for generating IDs

const corsOptions = {
  origin: [
    'http://localhost:3001', // In case you access server directly
    'http://localhost:3000',  // React development server
    'http://127.0.0.1:3000',  // Alternative localhost
  ],
  credentials: true,
  optionsSuccessStatus: 200
};

app.use(cors(corsOptions));
app.use(express.json());

// --- MOCK DATA ---
const assessmentDashboardData = [
  {
    id: 3,
    employeeId: 'E1003',
    name: 'Rohan Singh',
    project: 'Project Phoenix',
    currentLevel: 'L2',
    assessmentStatus: 'Pass',
    examHistory: [
      { examId: 301, level: 'L1', template: 'Java Basics', score: 91, status: 'Pass', date: '2024-11-10' },
      { examId: 302, level: 'L2', template: 'Advanced JavaScript', score: 88, status: 'Pass', date: '2025-05-20' },
    ]
  },
  {
    id: 5,
    employeeId: 'E1005',
    name: 'Vikram Kumar',
    project: 'Project Titan',
    currentLevel: 'L1',
    assessmentStatus: 'Scheduled',
    examHistory: [
      { examId: 501, level: 'L1', template: 'Python Fundamentals', score: 0, status: 'Scheduled', date: '2025-10-25' },
    ]
  },
  {
    id: 6,
    employeeId: 'E1006',
    name: 'Diya Patel',
    project: 'Project Phoenix',
    currentLevel: 'L1',
    assessmentStatus: 'Overdue',
    examHistory: [
      { examId: 601, level: 'L1', template: 'Selenium Basics', score: 0, status: 'Overdue', date: '2025-09-15' },
    ]
  },
  {
    id: 9,
    employeeId: 'E1009',
    name: 'Candidate Candidate',
    project: 'Project Nebula',
    currentLevel: 'L2',
    assessmentStatus: 'Pending',
    examHistory: [
      { examId: 901, level: 'L1', template: 'Agile Fundamentals', score: 85, status: 'Pass', date: '2025-02-01' },
      { examId: 902, level: 'L2', template: 'React Basics', score: 0, status: 'Pending', date: '2025-11-05' },
    ]
  },
  {
    id: 10,
    employeeId: 'E1010',
    name: 'Isha Reddy',
    project: 'Project Griffin',
    currentLevel: 'L1',
    assessmentStatus: 'Pass',
    examHistory: [
        { examId: 1001, level: 'L1', template: 'SQL Fundamentals', score: 58, status: 'Fail', date: '2025-08-10' },
        { examId: 1002, level: 'L1', template: 'SQL Fundamentals', score: 82, status: 'Pass', date: '2025-09-01' },
    ]
  }
];


const graphData = [
  { name: 'Aarav Sharma', score: 20, subject: 'Java', submittedAt: new Date(Date.now() - 100000000).toISOString() },
  { name: 'Diya Patel', score: 15, subject: 'Python', submittedAt: new Date(Date.now() - 50000000).toISOString() },
  { name: 'Rohan Mehta', score: 25, subject: 'JavaScript', submittedAt: new Date().toISOString() },
  { name: 'Aarav Sharma', score: 18, subject: 'Python', submittedAt: new Date(Date.now() - 200000000).toISOString() },
];

let mockUsers = [
  { id: 1, firstName: 'Aarav', lastName: 'Sharma', email: 'aarav.sharma@example.com', role: 'Admin', status: 'active', mobile_no: '9876543210' },
  { id: 2, firstName: 'Priya', lastName: 'Patel', email: 'priya.patel@example.com', role: 'Manager', status: 'active', mobile_no: '9876543211' },
  { id: 3, firstName: 'Rohan', lastName: 'Singh', email: 'rohan.singh@example.com', role: 'Candidate', status: 'inactive', managerId: 2, mobile_no: '9876543212' },
  { id: 4, firstName: 'Sneha', lastName: 'Gupta', email: 'sneha.gupta@example.com', role: 'Manager', status: 'active', mobile_no: '9876543213' },
  { id: 5, firstName: 'Vikram', lastName: 'Kumar', email: 'vikram.kumar@example.com', role: 'Candidate', status: 'active', managerId: 4, mobile_no: '9876543214' },
  { id: 6, firstName: 'Diya', lastName: 'Patel', email: 'diya.patel@example.com', role: 'Candidate', status: 'active', managerId: 2, mobile_no: '8765432109' },
  { id: 7, firstName: 'Admin', lastName: 'Admin', email: 'Admin@example.com', role: 'Admin', status: 'active', mobile_no: '9876543210' },
  { id: 8, firstName: 'Manager', lastName: 'Manager', email: 'Manager@example.com', role: 'Manager', status: 'active', mobile_no: '9876543210' },
  { id: 9, firstName: 'Candidate', lastName: 'Candidate', email: 'Candidate@example.com', role: 'Candidate', status: 'active', managerId: 2, mobile_no: '8765432109' },
];
let mockSubjects = ['Physics', 'Chemistry', 'Biology', 'Mathematics', 'History'];
let mockTemplates = [
  { id: 1, name: "Introductory Physics", subjects: [{ id: 's1', subject: 'Physics', weight: 100, difficulty: 'Easy' }] },
  { id: 2, name: "Advanced Chemistry", subjects: [{ id: 's2', subject: 'Chemistry', weight: 100, difficulty: 'Hard' }] },
];
let nextUserId = 10;
let nextTemplateId = 3;
let scheduledExams = [];
const examResultsData = [
  {
    "name": "Priya Sharma", "email": "priya.sharma@example.com", "submittedAt": "2025-10-07T14:30:00Z",
    "skillLevels": {
      "L1: Foundational Skills": { "subjects": { "Arithmetic": { "score": 95 }, "Basic Grammar": { "score": 88 } } },
      "L2: Intermediate Aptitude": { "subjects": { "Algebra": { "score": 92 }, "Reading Comprehension": { "score": 85 } } },
      "L3: Advanced Application": { "subjects": { "Geometry": { "score": 89 }, "Data Interpretation": { "score": 78 } } },
      "L4: Expert Reasoning": { "subjects": { "Calculus": { "score": 75 }, "Logical Reasoning": { "score": 91 } } }
    }
  },
  {
    "name": "Arjun Singh", "email": "arjun.singh@example.com", "submittedAt": "2025-10-08T09:15:00Z",
    "skillLevels": {
      "L1: Foundational Skills": { "subjects": { "General Science": { "score": 82 }, "Vocabulary": { "score": 79 } } },
      "L2: Intermediate Aptitude": { "subjects": { "Physics": { "score": 88 }, "History": { "score": 91 } } },
      "L3: Advanced Application": { "subjects": { "Chemistry": { "score": 76 } } },
      "L4: Expert Reasoning": { "subjects": { "Advanced Algorithms": { "score": 95 }, "Economics": { "score": 85 } } }
    }
  }
];

const DB = {
  candidates: {
    "candidate-1": { email: 'candidate@example.com', name: 'John Doe' }
  },
  exams: {
    'l1-aptitude': {
      name: 'Programming Aptitude Test',
      skillLevel: 'Level 1',
      time_limit: 10,
      scheduleDate: '2025-10-15',
      subjects: {
        java: {
          questions: [{
            id: 101,
            question: "What is the primary function of the JVM?",
            options: ["Compile Java code", "Act as a runtime environment", "Debug Java applications", "Manage databases"],
            correct_answer: 1
          }, {
            id: 102,
            question: "Which keyword is used to define a constant in Java?",
            options: ["const", "static", "final", "let"],
            correct_answer: 2
          }]
        },
        python: {
          questions: [{
            id: 201,
            question: "Which data type is immutable in Python?",
            options: ["list", "dictionary", "set", "tuple"],
            correct_answer: 3
          }]
        },
        sql: {
          questions: [{
            id: 301,
            question: "Which SQL clause is used to filter results based on an aggregate function?",
            options: ["GROUP BY", "ORDER BY", "HAVING", "WHERE"],
            correct_answer: 2
          }]
        }
      }
    },
    'l2-react': {
      name: 'React Basics',
      skillLevel: 'Level 2',
      time_limit: 15,
      scheduleDate: '2025-10-16',
      subjects: {
        react: {
          questions: [{
            id: 401,
            question: "What is JSX?",
            options: ["A JavaScript library", "A syntax extension for JavaScript", "A CSS preprocessor", "A database query language"],
            correct_answer: 1
          }, {
            id: 402,
            question: "Which hook is used to manage state in a functional component?",
            options: ["useEffect", "useContext", "useState", "useReducer"],
            correct_answer: 2
          }]
        }
      }
    },
    'l3-nodejs': {
      name: 'Node.js & Express',
      skillLevel: 'Level 2',
      time_limit: 20,
      scheduleDate: '2025-10-17',
      subjects: {}
    }
  },
  submissions: {
    "candidate-1": {}
  }
};

const mockGeneratedQuestions = [
  {
    "success": true,
    "data": {
      "mcq_results": [
        {
          "question": "According to the text, what is a core principle of the Java programming language, often referred to as the ‘Write Once, Run Anywhere’ (WORA) philosophy?",
          "options": {
            "A": "Java code must be compiled specifically for each operating system it will run on.",
            "B": "Java code can be compiled once and executed on any platform that supports the Java Virtual Machine (JVM) without modification.",
            "C": "Java code is inherently platform-dependent and requires separate compilation for each operating system.",
            "D": "Java code relies exclusively on direct hardware access for optimal performance across all platforms."
          },
          "correct_answer": "B",
          "explanation": "The text explicitly states that ‘compiled Java code can run on all platforms that support Java without the need to recompile,’ which directly reflects the WORA principle. Options A, C, and D contradict this core functionality of Java."
        },
        {
          "question": "Based on the text, which of the following best describes a key characteristic of Java's platform independence?",
          "options": {
            "A": "Java code is compiled directly into machine code specific to each operating system.",
            "B": "Java code is compiled into bytecode, which is then interpreted by the JVM, allowing it to run on different platforms.",
            "C": "Java relies solely on the underlying operating system's libraries for platform-specific functionality.",
            "D": "Java code is inherently tied to a single operating system due to its object-oriented design."
          },
          "correct_answer": "B",
          "explanation": "The text explicitly states that Java code is compiled into platform-neutral bytecode, which is then executed by the JVM. This is the core mechanism behind Java's 'Write Once, Run Anywhere' capability. Option A is incorrect because it describes compilation to machine code, and options C and D misrepresent how Java achieves platform independence."
        },
        {
          "question": "Based on the text, which of the following BEST describes Java's key advantage regarding platform compatibility?",
          "options": {
            "A": "Java code requires significant modifications for each new operating system, ensuring compatibility but demanding substantial development time.",
            "B": "Java’s bytecode is executed by the JVM, allowing it to run on any platform without requiring platform-specific code changes, a feature known as ‘Write Once, Run Anywhere’.",
            "C": "Java is inherently tied to a specific operating system, necessitating platform-specific adaptations for optimal performance.",
            "D": "Java’s portability is limited to only Windows and macOS environments due to compatibility constraints."
          },
          "correct_answer": "B",
          "explanation": "The text explicitly states that Java’s byte code is executed by the JVM, enabling it to run on any platform without needing specific code changes. This is a core feature defining Java’s platform independence."
        },
        {
          "question": "The text highlights several security features of Java. Which of the following BEST summarizes Java’s approach to security?",
          "options": {
            "A": "Java relies heavily on user authentication and authorization protocols to protect sensitive data.",
            "B": "Java’s security is dependent on the operating system’s security features; Java simply leverages them.",
            "C": "Java is secure because developers don’t directly interact with underlying memory or the operating system, and utilizes automatic garbage collection, reducing vulnerabilities.",
            "D": "Java’s security primarily relies on complex encryption algorithms, which are automatically managed by the JVM."
          },
          "correct_answer": "C",
          "explanation": "The text states Java is secure ‘by architecture’ due to the lack of direct memory interaction and the use of automatic garbage collection, eliminating a major source of vulnerabilities."
        },
        {
          "question": "Considering the diverse applications listed in the text (Enterprise solutions, Game development, etc.), what characteristic of Java contributes most significantly to its widespread use across these varied fields?",
          "options": {
            "A": "Java's limited support for graphical user interfaces restricts its application to specific domains.",
            "B": "Java’s platform independence allows it to be utilized in environments and systems that are not necessarily compatible.",
            "C": "Java’s focus on low-level programming makes it ideal for hardware-dependent applications.",
            "D": "Java’s reliance on pre-compiled libraries limits its adaptability to new software development challenges."
          },
          "correct_answer": "B",
          "explanation": "The text emphasizes Java’s platform independence as a key factor in its extensive use in diverse fields like enterprise solutions, game development, and mobile applications. This allows Java to be used across a wide variety of environments."
        },
        {
          "question": "According to the text, what is a key prerequisite for someone wanting to learn Java?",
          "options": {
            "A": "Extensive experience with hardware configuration.",
            "B": "A foundational understanding of programming environments and basic programming concepts.",
            "C": "Prior knowledge of database management systems.",
            "D": "Fluency in multiple programming languages."
          },
          "correct_answer": "B",
          "explanation": "The text explicitly states, 'Before you start learning Java, it is assumed that the readers have a reasonable exposure to any programming environment and knowledge of basic concepts such as variables, commands, syntax, etc.'"
        },
        {
          "question": "The passage highlights several advantages of Java. Which of the following BEST describes a key benefit of Java’s compiled nature compared to interpreted languages like Python?",
          "options": {
            "A": "Java is generally faster and more efficient due to its compiled nature.",
            "B": "Java is easier to debug due to its compiled code.",
            "C": "Java’s compiled nature allows it to be more easily adapted to different platforms.",
            "D": "Java’s compiled code requires less memory than interpreted code."
          },
          "correct_answer": "A",
          "explanation": "The text directly states, 'Java is generally faster and more efficient than Python because it is a compiled language, whereas Python is an interpreted language...'"
        },
        {
          "question": "Based on the information provided, what is a primary reason for the popularity of Java as a programming language?",
          "options": {
            "A": "Its simplicity and ease of use for beginners.",
            "B": "Its ability to be easily extended due to its object-oriented model and compiled nature.",
            "C": "Its exclusive support for functional programming paradigms.",
            "D": "Its strong reliance on database connectivity."
          },
          "correct_answer": "B",
          "explanation": "The text states, 'The Java language is easily extensible because it is based on an object model. Unlike many other programming languages, Java is compiled, not into a platform-dependent machine but into platform-independent byte code.'"
        },
        {
          "question": "Based on the text, which of the following best describes the primary function of the Java Virtual Machine (JVM)?",
          "options": {
            "A": "To directly compile Java source code into executable machine code.",
            "B": "To provide a platform-independent environment for executing Java bytecode.",
            "C": "To manage memory allocation and deallocation in Java programs.",
            "D": "To handle user input and output in Java applications."
          },
          "correct_answer": "B",
          "explanation": "The text explicitly states that the JVM 'provides a platform-independent environment for executing Java bytecode.' This is its core function – translating and running Java code regardless of the underlying operating system."
        },
        {
          "question": "The text outlines several key aspects of Object-Oriented Programming (OOP) in Java. Which of the following best summarizes the relationship between ‘Classes’ and ‘Objects’ as presented in the document?",
          "options": {
            "A": "Classes are instances of objects; objects are blueprints for classes.",
            "B": "Classes are the blueprints, and objects are instances created from those blueprints.",
            "C": "Objects are the blueprints, and classes are instances created from those blueprints.",
            "D": "Classes and Objects are the same thing; they are interchangeable terms."
          },
          "correct_answer": "B",
          "explanation": "The text states that Java ‘Classes and Objects’ are presented, with Classes defined as ‘blueprints’ and Objects as ‘instances created from those blueprints.’"
        },
        {
          "question": "Considering the sections on Error Handling and Exception Management, what is the primary purpose of the ‘try-catch’ block in Java?",
          "options": {
            "A": "To declare variables and define data types.",
            "B": "To handle potential errors or exceptions that may occur during program execution, preventing the program from crashing.",
            "C": "To define the main method of a Java program.",
            "D": "To create and manage Java objects."
          },
          "correct_answer": "B",
          "explanation": "The text’s section on ‘Java Try Catch Block’ specifically details the block’s function: ‘to handle potential errors or exceptions…preventing the program from crashing.’"
        },
        {
          "question": "The text covers various aspects of Java input and output. Which section primarily focuses on receiving data from the user?",
          "options": {
            "A": "Java - File Handling",
            "B": "Java - Hello World Program",
            "C": "Java - User Input",
            "D": "Java - Date and Time"
          },
          "correct_answer": "C",
          "explanation": "The text explicitly lists ‘Java - User Input’ as a section dedicated to receiving data from the user."
        },
        {
          "question": "According to the text's structure, which section discusses the fundamental principles of Java's method system?",
          "options": {
            "A": "Java - Variable Scopes",
            "B": "Java - Constructors",
            "C": "Java - Methods",
            "D": "Java - Access Modifiers"
          },
          "correct_answer": "C",
          "explanation": "The text lists ‘Java - Methods’ as a specific section detailing the core functionality and characteristics of methods within Java programs."
        },
        {
          "question": "Based on the text, which of the following BEST describes a key characteristic of Java's design that contributes to its ‘Write Once, Run Anywhere’ (WORA) promise?",
          "options": {
            "A": "Java’s reliance on a specific operating system’s hardware architecture ensures compatibility.",
            "B": "Java’s bytecode is interpreted on the fly, adapting to different platforms without modification.",
            "C": "Java’s extensive use of pre-compiled libraries guarantees consistent execution across environments.",
            "D": "Java’s strict enforcement of type checking prevents runtime errors and ensures portability."
          },
          "correct_answer": "B",
          "explanation": "The text explicitly states that Java byte code is ‘translated on the fly to native machine instructions’ and is not stored anywhere. This dynamic translation is the core mechanism that allows Java programs to run on different platforms without modification, fulfilling the WORA promise. Option A is incorrect because it describes a limitation, not a benefit. Option C is incorrect as pre-compiled libraries don’t guarantee portability. Option D focuses on compile-time checks, not the runtime aspect of portability."
        },
        {
          "question": "According to the text, which of the following best describes the core shift in Java's development strategy following the release of Java SE 8?",
          "options": {
            "A": "A renewed focus on solely supporting mobile applications through the J2ME platform.",
            "B": "A transition from a ‘Write Once, Run Anywhere’ philosophy to a more modular and platform-specific approach with the introduction of the module system.",
            "C": "A complete abandonment of the GPL license in favor of proprietary development models.",
            "D": "A return to a purely command-line interface, eliminating graphical user interface (GUI) support."
          },
          "correct_answer": "B",
          "explanation": "The text explicitly states that Java SE 9 introduced the module system, representing a significant shift towards modularity and platform-specific design, moving away from the ‘Write Once, Run Anywhere’ ideal. Option A is incorrect as J2ME was a separate focus. Option C is false as Sun fully embraced the GPL. Option D is inaccurate as Java maintained GUI support."
        },
        {
          "question": "The timeline presented in the text illustrates a continuous evolution of Java. Which of the following best summarizes the overall trend observed in the versions listed?",
          "options": {
            "A": "A decline in performance and stability as Java versions progressed.",
            "B": "A gradual increase in features and improvements, with each major release building upon previous advancements.",
            "C": "A cyclical pattern of innovation followed by periods of stagnation.",
            "D": "A consistent focus on backward compatibility, with minimal changes between versions."
          },
          "correct_answer": "B",
          "explanation": "The text demonstrates a clear progression of features and improvements across the Java versions, from JDK 1.0 to Java SE 21.  Each release introduced new capabilities and optimizations. Option A is incorrect as there's a general trend of improvement. Option C is inaccurate as there's a constant flow of innovation. Option D misrepresents the evolution of the language."
        },
        {
          "question": "The text mentions several new features introduced in later Java SE versions. Which of the following features was *not* introduced between Java SE 7 and Java SE 11?",
          "options": {
            "A": "Support for dynamic languages.",
            "B": "Lambda expressions.",
            "C": "Support for Synthetic proxy classes.",
            "D": "Support for heap allocation on alternate memory devices."
          },
          "correct_answer": "D",
          "explanation": "The text details features introduced in Java SE 7 (support for dynamic languages) and Java SE 8 (JNDI, JPDA, JavaSound). It also details features in Java SE 10 (heap allocation on alternate memory devices). However, it does *not* mention support for heap allocation on alternate memory devices, which was introduced in Java SE 11. Synthetic proxy classes were added in Java SE 1.4"
        },
        {
          "question": "Java's inherent portability is primarily due to its architecture-neutral design and reliance on the JVM. Which of the following BEST describes the role of the JVM in facilitating this portability?",
          "options": {
            "A": "The JVM directly translates Java code into machine-specific instructions, ensuring compatibility across different hardware architectures.",
            "B": "The JVM acts as an intermediary, interpreting Java bytecode, which is independent of the underlying operating system or hardware.",
            "C": "The JVM compiles Java code directly into native machine code, eliminating the need for platform-specific libraries.",
            "D": "The JVM dynamically adjusts Java code based on the target platform, optimizing performance for each specific environment."
          },
          "correct_answer": "B",
          "explanation": "The correct answer is B. Java code is compiled into bytecode, and the JVM then interprets this bytecode. This bytecode is platform-independent, meaning the same bytecode can run on any system with a compatible JVM. Options A, C, and D are inaccurate; the JVM is the key to Java’s portability, not direct compilation or dynamic adaptation."
        }
      ],
      "total_questions": 18
    }
  }
];




// --- Login API ---
app.post('/api/users/login', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ message: 'Email and password are required.' });
  }
  const user = mockUsers.find(u => u.email.toLowerCase() === email.trim().toLowerCase());
  if (user) {
    res.status(200).json({
      message: 'Login successful!',
      user: {
        username: user.email,
        role: user.role.toLowerCase(),
        userId: user.id
      }
    });
  } else {
    res.status(401).json({ message: 'Invalid credentials' });
  }
});
// --- API Endpoints for a candidate ---

// Get all exams 
app.get('/api/:candidate_id/exams', (req, res) => {
  const { candidate_id } = req.params;
  if (!DB.candidates[candidate_id]) {
    return res.status(404).json({ message: 'Candidate not found.' });
  }
  const examList = Object.entries(DB.exams).map(([id, data]) => ({
    id,
    name: data.name,
    skillLevel: data.skillLevel,
    scheduleDate: data.scheduleDate,
  }));
  res.json(examList);
});

// Get a single exam's details (without answers)
app.get('/api/:candidate_id/exams/:examId', (req, res) => {
  const { candidate_id, examId } = req.params;
  if (!DB.candidates[candidate_id]) {
    return res.status(404).json({ message: 'Candidate not found.' });
  }
  const originalExamData = DB.exams[examId];
  if (!originalExamData) {
    return res.status(404).json({ message: 'Exam not found' });
  }
  const transformedQuestions = mockGeneratedQuestions[0].data.mcq_results.map((q, index) => ({
    id: 500 + index,
    question: q.question,
    options: Object.values(q.options),
  }));
  const examDataForCandidate = {
    ...originalExamData,
    subjects: {
      react: {
        questions: transformedQuestions,
      },
    },
  };
  res.json(examDataForCandidate);
});

// Get all results for a candidate
app.get('/api/:candidate_id/results', (req, res) => {
  const { candidate_id } = req.params;
  const candidateSubmissions = DB.submissions[candidate_id] || {};
  const results = Object.entries(candidateSubmissions).map(([examId, data]) => ({
    examId,
    ...data,
  }));
  res.json(results);
});

// Submit answers and generate a detailed result
app.post('/api/:candidate_id/exams/:examId/submit', (req, res) => {
  const { candidate_id, examId } = req.params;
  const { answers } = req.body;

  if (!answers) return res.status(400).json({ message: 'Answers are required.' });
  if (!DB.candidates[candidate_id]) return res.status(404).json({ message: 'Candidate not found.' });

  const exam = DB.exams[examId];
  if (!exam) return res.status(404).json({ message: 'Exam not found.' });

  const allQuestions = [];
  Object.values(exam.subjects).forEach(subject => {
    allQuestions.push(...subject.questions);
  });

  let totalCorrect = 0;
  let totalAnswered = 0;

  const analysisQuestions = allQuestions.map(q => {
    const userAnswer = answers[q.id];
    let status = 'unanswered';
    if (userAnswer !== undefined) {
      totalAnswered++;
      if (userAnswer === q.correct_answer) {
        status = 'correct';
        totalCorrect++;
      } else {
        status = 'incorrect';
      }
    }
    return { ...q, user_answer: userAnswer, status };
  });

  const subjectScores = {};
  Object.keys(exam.subjects).forEach(subjectName => {
    const questionsInSubject = exam.subjects[subjectName].questions;
    const correctInSubject = analysisQuestions.filter(q =>
      questionsInSubject.some(sq => sq.id === q.id) && q.status === 'correct'
    ).length;
    subjectScores[subjectName] = {
      score: questionsInSubject.length > 0 ? (correctInSubject / questionsInSubject.length) * 100 : 0
    };
  });

  const totalQuestions = allQuestions.length;
  const result = {
    submittedAt: new Date().toISOString(),
    subjects: subjectScores,
    totalScore: totalQuestions > 0 ? (totalCorrect / totalQuestions) * 100 : 0,
    analysis: {
      totalQuestions: totalQuestions,
      correct: totalCorrect,
      incorrect: totalAnswered - totalCorrect,
      unanswered: totalQuestions - totalAnswered,
      questions: analysisQuestions
    }
  };

  if (!DB.submissions[candidate_id]) {
    DB.submissions[candidate_id] = {};
  }
  DB.submissions[candidate_id][examId] = result;
  res.status(201).json(result);
});


// --- API Endpoints for Admin ---

// == USERS ==
app.get('/api/users', (req, res) => res.json(mockUsers));

app.post('/api/users/create', (req, res) => {
  const newUser = { ...req.body, id: nextUserId++, status: 'active' };
  mockUsers.unshift(newUser);
  res.status(201).json(newUser);
});

app.patch('/api/users/update/:id', (req, res) => {
  const id = parseInt(req.params.id);
  const userIndex = mockUsers.findIndex(u => u.id === id);
  if (userIndex > -1) {
    mockUsers[userIndex] = { ...mockUsers[userIndex], ...req.body };
    return res.json(mockUsers[userIndex]);
  }
  res.status(404).json({ message: 'User not found' });
});

app.delete('/api/users/delete/:id', (req, res) => {
  const id = parseInt(req.params.id);
  mockUsers = mockUsers.filter(u => u.id !== id);
  res.status(200).json({ success: true });
});

app.patch('/api/users/toggle-status/:id', (req, res) => {
  const id = parseInt(req.params.id);
  const user = mockUsers.find(u => u.id === id);
  if (user) {
    user.status = user.status === 'active' ? 'inactive' : 'active';
    return res.json(user);
  }
  res.status(404).json({ message: 'User not found' });
});

app.get('/api/getAllManager', (req, res) => {
  res.json(mockUsers.filter(u => u.role === 'Manager'));
});

// == SUBJECTS ==
app.get('/api/subjects', (req, res) => res.json(mockSubjects));

// == TEMPLATES ==
app.get('/api/templates', (req, res) => res.json(mockTemplates));

app.post('/api/templates/create', (req, res) => {
  const newTemplate = { ...req.body, id: nextTemplateId++ };
  mockTemplates.unshift(newTemplate);
  res.status(201).json(newTemplate);
});

app.patch('/api/templates/update/:id', (req, res) => {
  const id = parseInt(req.params.id);
  const templateIndex = mockTemplates.findIndex(t => t.id === id);
  if (templateIndex > -1) {
    mockTemplates[templateIndex] = { ...mockTemplates[templateIndex], ...req.body };
    return res.json(mockTemplates[templateIndex]);
  }
  res.status(404).json({ message: 'Template not found' });
});

app.delete('/api/templates/delete/:id', (req, res) => {
  const id = parseInt(req.params.id);
  mockTemplates = mockTemplates.filter(t => t.id !== id);
  res.status(200).json({ success: true });
});

// --- ⭐️ API Endpoints for Assessment Hub ⭐️ ---

/**
 * GET all templates for the assessment hub
 * @route GET /api/assessment-hub/templates
 */
app.get('/api/assessment-hub/templates', (req, res) => {
    console.log("Request received: GET /api/assessment-hub/templates");
    res.json(mockTemplates);
});

/**
 * GET all candidates for the assessment hub
 * @route GET /api/assessment-hub/candidates
 */
app.get('/api/assessment-hub/candidates', (req, res) => {
    console.log("Request received: GET /api/assessment-hub/candidates");
    const candidates = mockUsers
        .filter(u => u.role === 'Candidate')
        .map(c => ({
            id: `cand_${c.id}`, // Create a frontend-friendly ID
            fullName: `${c.firstName} ${c.lastName}`,
            mobile: c.mobile_no,
            email: c.email
        }));
    res.json(candidates);
});

/**
 * POST to generate questions based on a template and skill level
 * @route POST /api/assessment-hub/generate-questions
 * @body { templateId: string, skillLevel: string }
 */
app.post('/api/assessment-hub/generate-questions', (req, res) => {
    const { templateId, skillLevel } = req.body;
    console.log(`Request received: POST /api/assessment-hub/generate-questions`);
    console.log(`> Body: Generating questions for templateId: ${templateId} and skillLevel: ${skillLevel}`);

    if (!templateId || !skillLevel) {
        return res.status(400).json({ message: 'Template ID and Skill Level are required.' });
    }

    // Mock logic: return a static list of questions, personalized with the skill level
    // const mockGeneratedQuestions = Array.from({ length: 20 }, (_, i) => ({
    //     id: `q_${crypto.randomBytes(4).toString('hex')}`,
    //     text: `This is question #${i + 1} for the ${skillLevel} skill level?`,
    //     options: ['Option A', 'Option B', 'Option C', 'Option D'],
    // }));

    res.json(mockGeneratedQuestions);
});

/**
 * POST to schedule a new exam
 * @route POST /api/assessment-hub/schedule-exam
 * @body { scheduleData object }
 */
app.post('/api/assessment-hub/schedule-exam', (req, res) => {
    const scheduleData = req.body;
    console.log("Request received: POST /api/assessment-hub/schedule-exam");
    console.log("> Body:", scheduleData);


    if (!scheduleData.templateId || !scheduleData.candidateIds || scheduleData.candidateIds.length === 0) {
        return res.status(400).json({ message: 'Missing required scheduling information.' });
    }

    const newSchedule = {
        scheduleId: `sch_${crypto.randomBytes(4).toString('hex')}`,
        createdAt: new Date().toISOString(),
        ...scheduleData
    };

    scheduledExams.push(newSchedule);
    console.log("✅ Exam successfully stored. Total scheduled exams:", scheduledExams.length);

    res.status(201).json({
        message: 'Exam scheduled successfully!',
        scheduleDetails: newSchedule
    });
});

app.get('/api/graphData', (req, res) => {
  res.status(200).json(graphData);
});


// Define a GET endpoint to fetch the results
app.get('/api/results', (req, res) => {
  console.log("Data requested from /api/results");
  res.json(examResultsData);
});

// Endpoint for Assessment Dashboard data
app.get('/api/assessment-dashboard/:managerId', (req, res) => {
    const managerEmail = req.params.managerId; // The ID from the frontend is the manager's email
    console.log(`Data requested from /api/assessment-dashboard for manager: ${managerEmail}`);
    const manager = mockUsers.find(
        user => user.role === 'Manager' && user.email.toLowerCase() === managerEmail.toLowerCase()
    );
    if (!manager) {
        // console.warn(`Manager with email ${managerEmail} not found.`);
        // return res.json([]);
        return res.json(assessmentDashboardData);
    }
    const managerNumericId = manager.id;
    const managedCandidateNames = mockUsers
        .filter(user => user.role === 'Candidate' && user.managerId === managerNumericId)
        .map(c => `${c.firstName} ${c.lastName}`);
    console.log(`Found candidates for manager ${managerEmail}:`, managedCandidateNames);
    const dashboardDataForManager = assessmentDashboardData.filter(
        record => managedCandidateNames.includes(record.name)
    );
    res.json(dashboardDataForManager);
});

app.listen(PORT, () => {
  console.log(`✅ Server is running on http://localhost:${PORT}`);
});
