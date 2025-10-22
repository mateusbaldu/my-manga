package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;
import fatecipi.progweb.mymanga.services.MangaService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import io.restassured.module.mockmvc.matcher.RestAssuredMockMvcMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class MangaControllerTest {
    @Mock
    private MangaService mangaService;

    @InjectMocks
    private MangaController mangaController;

    private MangaResponse mangaResponse;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(mangaController);
        mangaResponse = new MangaResponse(
                1L,
                "Test",
                "Test Author",
                "Test Mangá test test",
                1.5,
                "Test",
                MangaStatus.COMPLETED,
                Genres.ACTION,
                null
        );
    }

    @Nested
    class findById {
        @Test
        @DisplayName("GET /my-manga/mangas/{id} - should return MangaResponse when everything is ok")
        void findById_returnMangaResponse_WhenEverythingIsOk() {
            when(mangaService.getMangaResponseById(1L)).thenReturn(mangaResponse);

            RestAssuredMockMvc
                    .given()
                    .when()
                        .get("my-manga/mangas/{id}", 1L)
                    .then()
                        .statusCode(HttpStatus.OK.value())
                        .body("id", equalTo(1))
                        .body("title", equalTo("Test"))
                        .body("author", equalTo("Test Author"));
        }
    }

    @Nested
    class listAll {
        @Test
        @DisplayName("GET /my-manga/mangas/all - should return Page of MangaResponse when everything is ok")
        void listAll_returnPageMangaResponse_WhenEverythingIsOk() {
            Page<MangaResponse> page = new PageImpl<>(List.of(mangaResponse), PageRequest.of(0,10), 1);
            when(mangaService.listAll(any(PageRequest.class))).thenReturn(page);

            RestAssuredMockMvc
                    .given()
                    .when().get("/my-manga/mangas/all?page=0&size=10")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size", equalTo(1))
                    .body("totalElements", equalTo(1))
                    .body("totalPages", equalTo(1))
                    .body("content[0].id", equalTo(1))
                    .body("content[0].title", equalTo("Test"));
        }
    }
}