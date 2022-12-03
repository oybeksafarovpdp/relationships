package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;

import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    GroupRepository groupRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                  @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return studentPage;
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentsforGroup(@PathVariable Integer groupId,
                                             @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Id(groupId, pageable);
        return studentPage;
    }

    @PostMapping()
    public String addStudent(@RequestBody StudentDto studentDto) {
        Optional<Address> byId = addressRepository.findById(studentDto.getAddress_id());
        if (!byId.isPresent()) return "Bunday Address yo'q!";
        Optional<Group> byId1 = groupRepository.findById(studentDto.getGroup_id());
        if (!byId1.isPresent()) return "Bunday guruh yo'q";
        Group group = byId1.get();
        Address address = byId.get();
        Student student = new Student();
        student.setAddress(address);
        student.setGroup(group);
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        studentRepository.save(student);
        return "Student qo'shildi";
    }

    @PutMapping("/{id}")
    public String updateStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Optional<Student> byId2 = studentRepository.findById(id);
        if (!byId2.isPresent()) return "Bunday student topilmadi!";
        Optional<Address> byId = addressRepository.findById(studentDto.getAddress_id());
        if (!byId.isPresent()) return "Bunday address yo'q";
        Optional<Group> byId1 = groupRepository.findById(studentDto.getGroup_id());
        if (!byId1.isPresent()) return "Bunday guruh yo'q";

        Student student = byId2.get();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setAddress(byId.get());
        student.setGroup(byId1.get());
        studentRepository.save(student);
        return "Student o'zgartirildi!!";
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        Optional<Student> byId = studentRepository.findById(id);
        if (byId.isPresent()) {
            studentRepository.deleteById(id);
            return "Student topildi va o'chirildi!!!";
        } else {
            return "Student topilmadi!!!";
        }
    }



}
