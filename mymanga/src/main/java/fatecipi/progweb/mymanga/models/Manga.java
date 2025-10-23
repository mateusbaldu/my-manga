package fatecipi.progweb.mymanga.models;

import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "manga")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Manga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manga_id")
    private Long id;

    private String title;
    private String author;
    private String description;
    private Double rating;
    private String keywords;

    @Enumerated(EnumType.STRING)
    private MangaStatus status;

    @Enumerated(EnumType.STRING)
    private Genres genres;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Volume> volume;
}

