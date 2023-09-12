package com.beteam.willu.hashtag.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.PERSIST)
    private List<BoardTagMap> boardTagMapList = new ArrayList<>();

    public Tag(String content) {
        this.content = content;
    }

}
