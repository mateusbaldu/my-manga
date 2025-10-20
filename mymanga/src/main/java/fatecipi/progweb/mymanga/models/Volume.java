package fatecipi.progweb.mymanga.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
