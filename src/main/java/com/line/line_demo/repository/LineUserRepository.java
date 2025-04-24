package com.line.line_demo.repository;

import com.line.line_demo.entities.LineUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LineUserRepository extends JpaRepository<LineUser, Long> {

    @Query(value = """
    select lu.lindId from LineUser lu where lu.lindId in (:userIds)
    """)
    List<String> findAllByUserIdIn(Collection<String> userIds);

}
