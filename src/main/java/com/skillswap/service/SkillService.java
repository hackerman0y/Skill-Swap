package com.skillswap.service;

import com.skillswap.entity.Skill;
import com.skillswap.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // GET ALL
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    // ADD
    public Skill addSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    // INCREASE
    public Skill increasePopularity(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setPopularity(skill.getPopularity() + 1);

        return skillRepository.save(skill);
    }

    // DELETE
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    // GET BY ID
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
    }

    //  UPDATE
    public Skill updateSkill(Long id, Skill updatedSkill) {
        Skill existing = getSkillById(id);

        existing.setName(updatedSkill.getName());
        existing.setCategory(updatedSkill.getCategory());
        existing.setType(updatedSkill.getType());
        existing.setDescription(updatedSkill.getDescription());

        return skillRepository.save(existing);
    }

    // GET SKILLS BY USER
    public List<Skill> getSkillsByUser(Long userId) {
        return skillRepository.findByUserId(userId);
    }
}