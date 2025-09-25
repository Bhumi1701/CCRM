public import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main class for the Campus Course & Records Manager (CCRM) project.
 * This single file contains all necessary classes, organized as static nested classes
 * to simulate a package structure, fulfilling the project requirements.
 */
public class CCRMProject {

    // =================================================================================
    // :: DOMAIN ::
    // All core data model classes (POJOs).
    // =================================================================================

    /**
     * [REQUIREMENT] Abstract Class & Inheritance (Base Class)
     * Abstract base class representing a person in the institution.
     */
    public static abstract class Person {
        private final int id;
        private String fullName;
        private String email;

        protected Person(int id, String fullName, String email) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
        }

        public abstract String getProfile(); // Abstract method

        // [REQUIREMENT] Encapsulation (private fields, public getters/setters)
        public int getId() { return id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public String toString() {
            return "ID: " + id + ", Name: " + fullName;
        }
    }

    /**
     * [REQUIREMENT] Inheritance (Subclass)
     * Represents a Student, extending the Person class.
     */
    public static class Student extends Person {
        private final String regNo;
        private final List<Enrollment> enrolledCourses = new ArrayList<>();

        public Student(int id, String regNo, String fullName, String email) {
            super(id, fullName, email);
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }
        public List<Enrollment> getEnrolledCourses() { return new ArrayList<>(enrolledCourses); }
        public void enrollInCourse(Enrollment enrollment) { enrolledCourses.add(enrollment); }

        @Override
        public String getProfile() {
            return String.format("Student Profile [ID: %d, RegNo: %s, Name: %s]", getId(), regNo, getFullName());
        }
    }

    /**
     * [REQUIREMENT] Design Pattern: Builder
     * Represents a Course. This class is immutable and uses a static nested Builder class.
     */
    public static final class Course {
        private final String code;
        private final String title;
        private final int credits;
        private final String department;

        private Course(Builder builder) {
            this.code = builder.code;
            this.title = builder.title;
            this.credits = builder.credits;
            this.department = builder.department;
        }

        public String getCode() { return code; }
        public String getTitle() { return title; }
        public int getCredits() { return credits; }
        public String getDepartment() { return department; }

        @Override
        public String toString() {
            return String.format("Course[Code=%s, Title='%s', Credits=%d, Dept=%s]", code, title, credits, department);
        }

        /**
         * [REQUIREMENT] Nested Class (Static)
         * The Builder class for creating immutable Course instances.
         */
        public static class Builder {
            private final String code;
            private final String title;
            private int credits = 3; // Default value
            private String department = "General"; // Default value

            public Builder(String code, String title) {
                this.code = code;
                this.title = title;
            }

            public Builder credits(int credits) {
                this.credits = credits;
                return this;
            }

            public Builder department(String department) {
                this.department = department;
                return this;
            }

            public Course build() {
                // Assertions for invariants
                assert code != null && !code.trim().isEmpty() : "Course code cannot be null or empty.";
                assert credits > 0 && credits < 10 : "Credits must be between 1 and 9.";
                return new Course(this);
            }
        }
    }

    /**
     * Represents an enrollment linking a Student to a Course, along with a grade.
     */
    public static class Enrollment {
        private final Student student;
        private final Course course;
        private Grade grade;
        private final LocalDateTime enrollmentDate; // [REQUIREMENT] Date/Time API

        public Enrollment(Student student, Course course) {
            this.student = student;
            this.course = course;
            this.enrollmentDate = LocalDateTime.now();
        }

        public Student getStudent() { return student; }
        public Course getCourse() { return course; }
        public Grade getGrade() { return grade; }
        public void setGrade(Grade grade) { this.grade = grade; }

        @Override
        public String toString() {
            String gradeStr = (grade != null) ? grade.name() : "In Progress";
            return String.format("  - %-40s | Grade: %-12s", course.getTitle() + " (" + course.getCode() + ")", gradeStr);
        }
    }

    /**
     * [REQUIREMENT] Enum with Constructor and Fields
     * Represents letter grades and their corresponding grade points.
     */
    public enum Grade {
        S(10.0), A(9.0), B(8.0), C(7.0), D(6.0), E(5.0), F(0.0);
        private final double points;
        Grade(double points) { this.points = points; }
        public double getPoints() { return points; }
    }

    // =================================================================================
    // :: EXCEPTIONS ::
    // Custom exceptions for handling specific business rule violations.
    // =================================================================================

    /**
     * [REQUIREMENT] Custom Checked Exception
     */
    public static class DuplicateEnrollmentException extends Exception {
        public DuplicateEnrollmentException(String message) { super(message); }
    }

    /**
     * [REQUIREMENT] Custom Checked Exception
     */
    public static class MaxCreditLimitExceededException extends Exception {
        public MaxCreditLimitExceededException(String message) { super(message); }
    }

    // =================================================================================
    // :: CONFIG ::
    // Configuration management.
    // =================================================================================

    /**
     * [REQUIREMENT] Design Pattern: Singleton
     * Manages application configuration paths.
     */
    public static class AppConfig {
        private static AppConfig instance;
        public final Path dataDirectory = Paths.get("app-data");
        public final Path backupDirectory = Paths.get("backups");
        private AppConfig() {
            try {
                Files.createDirectories(dataDirectory);
                Files.createDirectories(backupDirectory);
            } catch (IOException e) {
                System.err.println("FATAL: Could not initialize storage directories.");
                throw new RuntimeException(e);
            }
        }
        public static synchronized AppConfig getInstance() {
            if (instance == null) instance = new AppConfig();
            return instance;
        }
    }

    // =================================================================================
    // :: SERVICES ::
    // Classes that handle business logic.
    // =================================================================================

    public static class StudentService {
        private final List<Student> students = new ArrayList<>();
        private int nextId = 1;

        public void addStudent(String regNo, String fullName, String email) {
            students.add(new Student(nextId++, regNo, fullName, email));
            System.out.println("Student added: " + fullName);
        }
        public Optional<Student> findByRegNo(String regNo) {
            return students.stream().filter(s -> s.getRegNo().equalsIgnoreCase(regNo)).findFirst();
        }
        public List<Student> getAllStudents() { return new ArrayList<>(students); }
    }

    public static class CourseService {
        private final List<Course> courses = new ArrayList<>();
        public void addCourse(Course course) {
            courses.add(course);
            System.out.println("Course added: " + course.getTitle());
        }
        public Optional<Course> findByCode(String code) {
            return courses.stream().filter(c -> c.getCode().equalsIgnoreCase(code)).findFirst();
        }
        public List<Course> getAllCourses() { return new ArrayList<>(courses); }
    }

    public static class EnrollmentService {
        private static final int MAX_CREDITS_PER_SEMESTER = 20;

        public void enrollStudent(Student student, Course course) throws DuplicateEnrollmentException, MaxCreditLimitExceededException {
            // Check for duplicate enrollment
            boolean alreadyEnrolled = student.getEnrolledCourses().stream()
                .anyMatch(e -> e.getCourse().getCode().equalsIgnoreCase(course.getCode()));
            if (alreadyEnrolled) {
                throw new DuplicateEnrollmentException("Student is already enrolled in course " + course.getCode());
            }

            // Check for credit limit
            int currentCredits = student.getEnrolledCourses().stream()
                .mapToInt(e -> e.getCourse().getCredits()).sum();
            if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
                throw new MaxCreditLimitExceededException("Cannot enroll. Max credit limit of " + MAX_CREDITS_PER_SEMESTER + " would be exceeded.");
            }

            Enrollment newEnrollment = new Enrollment(student, course);
            student.enrollInCourse(newEnrollment);
            System.out.println("Enrollment successful for " + student.getFullName() + " in " + course.getTitle());
        }

        public void assignGrade(Student student, Course course, Grade grade) {
            student.getEnrolledCourses().stream()
                .filter(e -> e.getCourse().getCode().equalsIgnoreCase(course.getCode()))
                .findFirst()
                .ifPresent(enrollment -> {
                    enrollment.setGrade(grade);
                    System.out.println("Grade " + grade + " assigned for " + course.getTitle());
                });
        }
    }

    public static class TranscriptService {
        /**
         * [REQUIREMENT] Polymorphism & toString() override
         * Generates and prints a student's transcript.
         */
        public static void printTranscript(Student student) {
            System.out.println("\n-------------------- TRANSCRIPT --------------------");
            System.out.println(student.getProfile());
            System.out.println("--------------------------------------------------");
            if (student.getEnrolledCourses().isEmpty()) {
                System.out.println("No courses enrolled.");
            } else {
                student.getEnrolledCourses().forEach(System.out::println); // Polymorphic call to Enrollment.toString()
            }
            System.out.println("--------------------------------------------------");
            System.out.printf("GPA: %.2f%n", calculateGpa(student));
            System.out.println("--------------------------------------------------\n");
        }

        private static double calculateGpa(Student student) {
            List<Enrollment> gradedCourses = student.getEnrolledCourses().stream()
                .filter(e -> e.getGrade() != null)
                .collect(Collectors.toList());

            if (gradedCourses.isEmpty()) return 0.0;

            double totalPoints = gradedCourses.stream()
                .mapToDouble(e -> e.getGrade().getPoints() * e.getCourse().getCredits()).sum();
            int totalCredits = gradedCourses.stream()
                .mapToInt(e -> e.getCourse().getCredits()).sum();
            
            return totalPoints / totalCredits;
        }
    }

    // =================================================================================
    // :: I/O ::
    // File handling operations.
    // =================================================================================

    /**
     * [REQUIREMENT] NIO.2 & Streams
     * Handles file import, export, and backup operations.
     */
    public static class FileService {
        private final AppConfig config = AppConfig.getInstance();

        public void importCourses(String fileName, CourseService courseService) {
            Path filePath = Paths.get(fileName);
            if (!Files.exists(filePath)) {
                System.out.println("Import file not found: " + fileName);
                return;
            }
            try (Stream<String> lines = Files.lines(filePath)) {
                lines.map(line -> line.split(","))
                     .filter(parts -> parts.length == 4)
                     .forEach(parts -> {
                         Course course = new Course.Builder(parts[0], parts[1])
                                             .credits(Integer.parseInt(parts[2]))
                                             .department(parts[3])
                                             .build();
                         courseService.addCourse(course);
                     });
                System.out.println("Courses imported successfully from " + fileName);
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error reading course import file: " + e.getMessage());
            }
        }

        public void backupData() {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path destinationDir = config.backupDirectory.resolve("backup_" + timestamp);
            try {
                Files.createDirectories(destinationDir);
                try (Stream<Path> stream = Files.walk(config.dataDirectory)) {
                    stream.forEach(source -> {
                        try {
                            Path destination = destinationDir.resolve(config.dataDirectory.relativize(source));
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.err.println("Failed to copy: " + source);
                        }
                    });
                }
                System.out.println("Backup successful: " + destinationDir);
            } catch (IOException e) {
                System.err.println("Backup failed: " + e.getMessage());
            }
        }
    }

    // =================================================================================
    // :: UTILITIES ::
    // Helper utilities.
    // =================================================================================

    /**
     * [REQUIREMENT] Recursion
     * Contains a recursive method to calculate directory size.
     */
    public static class RecursiveFileUtils {
        public static long calculateDirectorySize(Path path) {
            try {
                if (!Files.isDirectory(path)) return Files.size(path);
                try (Stream<Path> stream = Files.list(path)) {
                    return stream.mapToLong(RecursiveFileUtils::calculateDirectorySize).sum();
                }
            } catch (IOException e) {
                System.err.println("Error calculating size for " + path + ": " + e.getMessage());
                return 0L;
            }
        }
    }

    // =================================================================================
    // :: CLI - MAIN APPLICATION ::
    // The command-line interface and main entry point.
    // =================================================================================

    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();
    private static final CourseService courseService = new CourseService();
    private static final EnrollmentService enrollmentService = new EnrollmentService();
    private static final FileService fileService = new FileService();

    public static void main(String[] args) {
        System.out.println("Welcome to the Campus Course & Records Manager!");
        loadSampleData();

        // [REQUIREMENT] Loop (while) and Switch
        boolean exit = false;
        while (!exit) {
            printMenu();
            System.out.print("Enter choice: ");
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> manageStudents();
                case "2" -> manageCourses();
                case "3" -> manageEnrollment();
                case "4" -> manageData();
                case "5" -> printPlatformInfo();
                case "0" -> exit = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Enrollment & Grades");
        System.out.println("4. Data Import/Backup");
        System.out.println("5. Show Java Platform Info");
        System.out.println("0. Exit");
        System.out.println("=============================");
    }

    private static void manageStudents() {
        System.out.println("\n--- Student Menu ---");
        System.out.println("1. Add Student");
        System.out.println("2. View Transcript");
        System.out.println("3. List All Students");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();
        switch(choice) {
            case "1":
                System.out.print("Enter RegNo: "); String regNo = scanner.nextLine();
                System.out.print("Enter Full Name: "); String name = scanner.nextLine();
                System.out.print("Enter Email: "); String email = scanner.nextLine();
                studentService.addStudent(regNo, name, email);
                break;
            case "2":
                System.out.print("Enter Student RegNo: ");
                studentService.findByRegNo(scanner.nextLine())
                    .ifPresentOrElse(TranscriptService::printTranscript, 
                                     () -> System.out.println("Student not found."));
                break;
            case "3":
                System.out.println("\n--- All Students ---");
                studentService.getAllStudents().forEach(s -> System.out.println(s.getProfile()));
                break;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private static void manageCourses() {
         System.out.println("\n--- Course Menu ---");
         System.out.println("1. List All Courses");
         System.out.print("Choice: ");
         if (scanner.nextLine().equals("1")) {
             System.out.println("\n--- All Courses ---");
             courseService.getAllCourses().forEach(System.out::println);
         } else {
             System.out.println("Invalid choice.");
         }
    }

    private static void manageEnrollment() {
        System.out.println("\n--- Enrollment Menu ---");
        System.out.println("1. Enroll Student in Course");
        System.out.println("2. Assign Grade");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();
        
        Optional<Student> studentOpt = promptForStudent();
        if (studentOpt.isEmpty()) { System.out.println("Operation cancelled."); return; }
        
        Optional<Course> courseOpt = promptForCourse();
        if (courseOpt.isEmpty()) { System.out.println("Operation cancelled."); return; }

        switch(choice) {
            case "1":
                try {
                    enrollmentService.enrollStudent(studentOpt.get(), courseOpt.get());
                } catch (DuplicateEnrollmentException | MaxCreditLimitExceededException e) {
                    System.err.println("Enrollment Failed: " + e.getMessage());
                }
                break;
            case "2":
                System.out.print("Enter Grade (S, A, B, ...): ");
                try {
                    Grade grade = Grade.valueOf(scanner.nextLine().toUpperCase());
                    enrollmentService.assignGrade(studentOpt.get(), courseOpt.get(), grade);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid grade entered.");
                }
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    private static void manageData() {
        System.out.println("\n--- Data Menu ---");
        System.out.println("1. Import Courses from CSV");
        System.out.println("2. Create Backup");
        System.out.println("3. Show Backup Size");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();
        switch(choice) {
            case "1":
                System.out.print("Enter filename (e.g., test-data/courses.csv): ");
                fileService.importCourses(scanner.nextLine(), courseService);
                break;
            case "2":
                fileService.backupData();
                break;
            case "3":
                Path backupDir = AppConfig.getInstance().backupDirectory;
                long size = RecursiveFileUtils.calculateDirectorySize(backupDir);
                System.out.printf("Total size of all backups is: %.2f KB%n", size / 1024.0);
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    private static Optional<Student> promptForStudent() {
        System.out.print("Enter Student RegNo: ");
        String regNo = scanner.nextLine();
        Optional<Student> student = studentService.findByRegNo(regNo);
        if (student.isEmpty()) System.out.println("Student with RegNo '" + regNo + "' not found.");
        return student;
    }
    
    private static Optional<Course> promptForCourse() {
        System.out.print("Enter Course Code: ");
        String code = scanner.nextLine();
        Optional<Course> course = courseService.findByCode(code);
        if (course.isEmpty()) System.out.println("Course with code '" + code + "' not found.");
        return course;
    }
    
    private static void loadSampleData() {
        System.out.println("Loading sample data...");
        studentService.addStudent("S001", "Alice Smith", "alice@example.com");
        studentService.addStudent("S002", "Bob Johnson", "bob@example.com");
        courseService.addCourse(new Course.Builder("CS101", "Intro to Programming").credits(4).department("CS").build());
        courseService.addCourse(new Course.Builder("MA201", "Calculus I").credits(4).department("Math").build());
        courseService.addCourse(new Course.Builder("EN101", "English Composition").credits(3).department("English").build());
        System.out.println("Sample data loaded.");
    }
    
    private static void printPlatformInfo() {
        System.out.println("\n--- Java Platform Summary ---");
        System.out.println("Java SE (Standard Edition): For desktop and server applications. This project is a Java SE application.");
        System.out.println("Java EE (Enterprise Edition): Extends SE with features for large-scale, multi-tiered, and web applications.");
        System.out.println("Java ME (Micro Edition): A subset of SE for resource-constrained devices like mobile phones and embedded systems.");
        System.out.println("\n--- JVM/JRE/JDK ---");
        System.out.println("JVM (Java Virtual Machine): Executes the compiled Java bytecode.");
        System.out.println("JRE (Java Runtime Environment): Contains the JVM and libraries needed to RUN Java apps.");
        System.out.println("JDK (Java Development Kit): Contains the JRE and tools (compiler, etc.) to CREATE Java apps.");
    }
}
 