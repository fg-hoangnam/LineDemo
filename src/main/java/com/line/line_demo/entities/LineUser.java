package com.line.line_demo.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Table(name = "line_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_line_user_line_user_id",
                        columnNames = {"line_id"}
                )
        })
@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class LineUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "email")
    String email;

    @Column(name = "line_id", nullable = false)
    String lindId;

    public LineUser(String lineUserId){
        this.lindId = lineUserId;
    }

}
