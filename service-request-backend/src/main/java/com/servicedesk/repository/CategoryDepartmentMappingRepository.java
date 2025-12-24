package com.servicedesk.repository;

import com.servicedesk.entity.CategoryDepartmentMapping;
import com.servicedesk.entity.Department;
import com.servicedesk.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CategoryDepartmentMapping entity
 */
@Repository
public interface CategoryDepartmentMappingRepository extends JpaRepository<CategoryDepartmentMapping, Long> {

    Optional<CategoryDepartmentMapping> findByCategoryId(Long categoryId);

    List<CategoryDepartmentMapping> findByDepartmentId(Long departmentId);

    Optional<CategoryDepartmentMapping> findByCategoryAndDepartment(ServiceCategory category, Department department);

    boolean existsByCategoryId(Long categoryId);
}
