package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.dto.manga.MangaUpdate;
import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;
import fatecipi.progweb.mymanga.services.MangaService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebMvcTest(
        controllers = MangaController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class MangaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MangaService mangaService;

    private MangaResponse mangaResponse;
    private MangaUpdate mangaUpdate;
    private MangaCreate mangaCreate;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        mangaResponse = new MangaResponse(
                1L,
                "Test",
                "Test Author",
                "Test Mangá description",
                1.5,
                "Test",
                MangaStatus.COMPLETED,
                Genres.ACTION,
                null
        );

        mangaUpdate = new MangaUpdate(
                "Test Updated",
                "Test Author Updated",
                "Test Mangá description updated",
                1.5,
                MangaStatus.PAUSED,
                Genres.ADVENTURE,
                "Updated Keywords"
        );

        mangaCreate = new MangaCreate(
                "Test",
                "Test Author",
                "Test Mangá description",
                1.5,
                MangaStatus.COMPLETED,
                Genres.ACTION,
                "Test"
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
            verify(mangaService, times(1)).getMangaResponseById(1L);
        }
    }

    @Nested
    class listAll {
        @Test
        @DisplayName("GET /my-manga/mangas/all - should return Page of MangaResponse when everything is ok")
        void listAll_returnPageMangaResponse_WhenEverythingIsOk() {
            Page<MangaResponse> page = new PageImpl<>(List.of(mangaResponse), PageRequest.of(0,10) , 1);
            when(mangaService.listAll(any(PageRequest.class))).thenReturn(page);

            RestAssuredMockMvc
                    .given()
                    .when().get("/my-manga/mangas/all?page=0&size=10")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", equalTo(1))
                    .body("totalPages", equalTo(1))
                    .body("content[0].id", equalTo(1))
                    .body("content[0].title", equalTo("Test"));
            verify(mangaService, times(1)).listAll(any(PageRequest.class));
        }
    }

    @Nested
    class listByKeyword {
        @Test
        @DisplayName("GET /my-manga/mangas/{keyword} - should return Page of MangaResponse when everything is ok")
        void listByKeyword_returnPageMangaResponse_WhenEverythingIsOk() {
            Page<MangaResponse> page = new PageImpl<>(List.of(mangaResponse), PageRequest.of(0,10) , 1);
            when(mangaService.findByKeyword(anyString(), any(PageRequest.class))).thenReturn(page);

            RestAssuredMockMvc
                    .given()
                    .when().get("/my-manga/mangas/search/{keyword}", "Test")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", equalTo(1))
                    .body("totalPages", equalTo(1))
                    .body("content[0].id", equalTo(1))
                    .body("content[0].title", equalTo("Test"));
            verify(mangaService, times(1)).findByKeyword(eq("Test"), any(PageRequest.class));
        }
    }

    @Nested
    class update {
        @Test
        @DisplayName("PATCH /my-manga/mangas/{id} - should return Manga response when everything is ok")
        void update_returnMangaResponse_WhenEverythingIsOk() {
            mangaResponse = new MangaResponse(
                    1L,
                    "Test Updated",
                    "Test Author Updated",
                    "Test Mangá description updated",
                    1.5,
                    "Updated Keywords",
                    MangaStatus.PAUSED,
                    Genres.ADVENTURE,
                    null
            );
            when(mangaService.update(anyLong(), any(MangaUpdate.class))).thenReturn(mangaResponse);

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(mangaUpdate)
                    .when().patch("/my-manga/mangas/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("title", equalTo("Test Updated"))
                    .body("author", equalTo("Test Author Updated"));
            verify(mangaService, times(1)).update(1L, mangaUpdate);
        }
    }

    @Nested
    class create {
        @Test
        @DisplayName("POST /my-manga/mangas/new - should return Manga response when everything is ok")
        void create_returnMangaResponse_WhenEverythingIsOk() {
            doReturn(mangaResponse).when(mangaService).save(any(MangaCreate.class));

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(mangaCreate)
                    .when().post("/my-manga/mangas/new")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("title", equalTo("Test"));
            verify(mangaService, times(1)).save(mangaCreate);
        }
    }

    @Nested
    class delete {
        @Test
        @DisplayName("DELETE /my-manga/mangas/{id} - should return void when everything is ok")
        void delete_returnVoid_WhenEverythingIsOk() {
            doNothing().when(mangaService).deleteMangaById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .delete("/my-manga/mangas/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            verify(mangaService, times(1)).deleteMangaById(anyLong());
        }
    }
}