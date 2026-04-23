package com.skillswap.controller;

import com.skillswap.entity.Skill;
import com.skillswap.entity.User;
import com.skillswap.config.JwtUtil;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.UserRepository;
import com.skillswap.service.SkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin("*")
public class SkillController {

    private final SkillService skillService;
    private final UserRepository userRepo;
    private final SkillRepository skillRepo;
    private final JwtUtil jwtUtil;

    public SkillController(SkillService skillService, UserRepository userRepo, SkillRepository skillRepo, JwtUtil jwtUtil) {
        this.skillService = skillService;
        this.userRepo = userRepo;
        this.skillRepo = skillRepo;
        this.jwtUtil = jwtUtil;
    }

    //  GET ALL
    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    //  GET BY ID
    @GetMapping("/{id}")
    public Skill getSkill(@PathVariable Long id) {
        return skillService.getSkillById(id);
    }

    // ADD SKIll
    @PostMapping
    public Skill addSkill(@RequestBody Skill skill,
                          @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepo.findByEmail(email).orElseThrow();

        skill.setUser(user);
        skill.setPopularity(0);

        return skillService.addSkill(skill);
    }

    //  UPDATE
    @PutMapping("/{id}")
    public Skill updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        return skillService.updateSkill(id, skill);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
    }


    @PutMapping("/{id}/increase")
    public Skill increasePopularity(@PathVariable Long id) {
        Skill skill = skillRepo.findById(id).orElseThrow();
        skill.setPopularity(skill.getPopularity() + 1);
        return skillRepo.save(skill);
    }

    // GET SKILLS BY USER
    @GetMapping("/user/{userId}")
    public List<Skill> getSkillsByUser(@PathVariable Long userId) {
        return skillService.getSkillsByUser(userId);
    }
}
