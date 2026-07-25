package com.gameflix.auth.controller;

import com.gameflix.auth.entity.Course;
import com.gameflix.auth.repository.CourseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // GET /courses  -> course list page
    @GetMapping
    public String viewCourseList(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "course_list";   // or "index" temporarily if you don't have course_list.html yet
    }

    // GET /courses/new -> "Add Course" form
    @GetMapping("/new")
    public String showNewCourseForm(Model model) {
        Course course = new Course();
        model.addAttribute("course", course);
        return "new_course";
    }

    // POST /courses/save -> add or update course
    @PostMapping("/save")
    public String saveCourse(@ModelAttribute("course") Course course) {
        courseRepository.save(course);
        return "redirect:/courses";
    }

    // GET /courses/update/{id} -> update form
    @GetMapping("/update/{id}")
    public String showFormForUpdate(@PathVariable("id") Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course id: " + id));
        model.addAttribute("course", course);
        return "update_course";
    }

    // GET /courses/delete/{id} -> delete course
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseRepository.deleteById(id);
        return "redirect:/courses";
    }
}