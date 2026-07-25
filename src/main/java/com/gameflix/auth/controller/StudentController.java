package com.gameflix.auth.controller;

import com.gameflix.auth.entity.Course;
import com.gameflix.auth.entity.Student;
import com.gameflix.auth.repository.CourseRepository;
import com.gameflix.auth.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentController(StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // GET /students
    @GetMapping
    public String viewStudentList(Model model) {
        List<Student> students = studentRepository.findAll();
        model.addAttribute("listStudents", students);
        return "student_list";
    }

    // GET /students/new
    @GetMapping("/new")
    public String showNewStudentForm(Model model) {
        Student student = new Student();
        model.addAttribute("student", student);
        return "new_student";
    }

    // POST /students/save
    @PostMapping("/save")
    public String saveStudent(@ModelAttribute("student") Student student) {
        studentRepository.save(student);
        return "redirect:/students";
    }

    // GET /students/update/{id}
    @GetMapping("/update/{id}")
    public String showStudFormForUpdate(@PathVariable("id") Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + id));
        model.addAttribute("student", student);

        return "update_student";
    }

    // GET /students/delete/{id}
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") Long id) {
        studentRepository.deleteById(id);
        return "redirect:/students";
    }

    // GET /students/{id}/courses
    @GetMapping("/{id}/courses")
    public String viewStudentCourses(@PathVariable("id") Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + id));

        List<Course> allCourses = courseRepository.findAll();

        model.addAttribute("student", student);
        model.addAttribute("courses", allCourses);
        model.addAttribute("title", "Courses for " + student.getStudName());

        return "student_course";
    }

    // POST /students/{sid}/courses/add
    // @Transactional keeps the Hibernate session open for this whole method so the
    // lazy-loaded student.getCourses() collection below can be read and modified.
    // Without it (open-in-view is disabled in application.properties), this throws
    // LazyInitializationException the moment a course is added to a student.
    @Transactional
    @PostMapping("/{sid}/courses/add")
    public String addCourseToStudent(@PathVariable("sid") Long sid,
                                     @RequestParam("courseId") Long courseId) {

        Student student = studentRepository.findById(sid)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + sid));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course id: " + courseId));

        Set<Course> courses = student.getCourses();
        if (courses == null) {
            courses = new HashSet<>();
        }
        courses.add(course);
        student.setCourses(courses);

        studentRepository.save(student);
        return "redirect:/students";
    }
}