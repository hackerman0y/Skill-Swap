package com.hala.skillss.controller;

import com.hala.skillss.model.Skill;
import com.hala.skillss.repository.SkillRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/hala/skills")
@CrossOrigin
public class SkillController {

    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // Get all skills
    @GetMapping
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    // Add skill
    @PostMapping
    public Skill addSkill(@RequestBody Skill skill) {
        return skillRepository.save(skill);
    }

    // Update skill
    @PutMapping("/{id}")
    public Skill updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        return skillRepository.findById(id).map(existing -> {
            existing.setName(skill.getName());
            existing.setDescription(skill.getDescription());
            existing.setCategory(skill.getCategory());
            existing.setType(skill.getType());
            return skillRepository.save(existing);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));
    }

    // Delete skill
    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found");
        }
        skillRepository.deleteById(id);
    }

    // Get skill by id (increase views + update lastSeen)
    @GetMapping("/{id}")
    public Skill getSkillById(@PathVariable Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));

        skill.setLastSeen(LocalDateTime.now());
        skill.setViews(skill.getViews() + 1);

        return skillRepository.save(skill);
    }

    // Search
    @GetMapping("/search")
    public List<Skill> searchSkills(@RequestParam String name) {
        return skillRepository.findByNameContainingIgnoreCase(name);
    }

    // Filter
    @GetMapping("/filter")
    public List<Skill> filterByCategory(@RequestParam String category) {
        return skillRepository.findByCategory(category);
    }

    // Advanced filter
    @GetMapping("/advanced-filter")
    public List<Skill> advancedFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type) {

        List<Skill> skills = skillRepository.findAll();

        if (name != null) {
            skills = skills.stream()
                    .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }

        if (category != null) {
            skills = skills.stream()
                    .filter(s -> s.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        if (type != null) {
            skills = skills.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .toList();
        }

        return skills;
    }

    // Sorting (A-Z / Z-A)
    @GetMapping("/sort")
    public List<Skill> sortSkills(@RequestParam String direction) {

        if (direction.equalsIgnoreCase("asc")) {
            return skillRepository.findAllByOrderByNameAsc();
        } else {
            return skillRepository.findAllByOrderByNameDesc();
        }
    }

    // Popular (by views)
    @GetMapping("/popular")
    public List<Skill> sortByPopularity() {
        return skillRepository.findAllByOrderByViewsDesc();
    }

    // Recommendation
    @GetMapping("/recommend")
    public List<Skill> recommend(
            @RequestParam String category,
            @RequestParam Long excludeId) {

        return skillRepository.findByCategoryAndIdNot(category, excludeId)
                .stream()
                .limit(4)
                .toList();
    }

    // Categories (dynamic)
    @GetMapping("/categories")
    public List<String> getCategories() {
        return skillRepository.findDistinctCategories();
    }
}