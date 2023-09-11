package com.beteam.willu.hashtag.entity;

import com.beteam.willu.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "BoardTagMap")
public class BoardTagMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public BoardTagMap(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }
}
