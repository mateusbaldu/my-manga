package fatecipi.progweb.mymanga.models.volume;

import fatecipi.progweb.mymanga.models.manga.Manga;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "manga_volume")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Volume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer volumeNumber;
    private BigDecimal price;
    private String chapters;
    private LocalDate releaseDate;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id")
    private Manga manga;
}
