package com.line.line_demo.repository;

import com.line.line_demo.entities.LineAccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LineAccountInfoRepository extends JpaRepository<LineAccountInfo, Long> {

    @Query(value = """
    select lu.lineId from LineAccountInfo lu where lu.lineId in (:userIds)
    """, countQuery = """
    select count(lu) from LineAccountInfo lu where lu.lineId in (:userIds)
""")
    List<String> fetchAllByUserIdIn(Collection<String> userIds);

}
