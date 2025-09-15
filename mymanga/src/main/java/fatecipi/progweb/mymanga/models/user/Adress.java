package fatecipi.progweb.mymanga.models.user;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Adress {
    @Size(min = 8, max = 8)
    private String cep;
    private String street;
    private String number;
    private String complement;
    private String locality;
    private String city;
    private String state;
}
