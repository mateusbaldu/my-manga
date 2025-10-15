package fatecipi.progweb.mymanga.models.dto.address;

public record CepResult (
        String cep,
        String logradouro,
        String complemento,
        String unidade,
        String bairro,
        String localidade,
        String uf,
        String estado,
        String regiao,
        String ibge,
        String gia,
        String ddd,
        String siafi,
        boolean erro
){
}
