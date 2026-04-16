package com.hala.skillss.repository;

import com.hala.skillss.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    //  Search by name
    List<Skill> findByNameContainingIgnoreCase(String name);

    // Filter by category
    List<Skill> findByCategory(String category);
    List<Skill> findByNameContainingIgnoreCaseAndCategoryAndType(
            String name, String category, String type);
    List<Skill> findAllByOrderByNameAsc();
    List<Skill> findAllByOrderByNameDesc();
    // for smart recommendation
    List<Skill> findByCategoryAndIdNot(String category, Long id);
    List<Skill> findAllByOrderByViewsDesc();
    @Query("SELECT DISTINCT s.category FROM Skill s")
    List<String> findDistinctCategories();
}