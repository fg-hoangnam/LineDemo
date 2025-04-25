package com.line.line_demo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Table(name = "line_account_info",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_line_account_info_line_id",
                        columnNames = {"line_id"}
                )
        })
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineAccountInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "email")
    String email;

    @Column(name = "line_id", nullable = false)
    String lineId;

    public LineAccountInfo(String lineUserId){
        this.lineId = lineUserId;
    }

}
