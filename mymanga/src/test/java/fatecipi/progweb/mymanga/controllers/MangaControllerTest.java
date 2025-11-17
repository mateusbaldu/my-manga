package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.dto.manga.MangaCardResponse;
import fatecipi.progweb.mymanga.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.dto.manga.MangaUpdate;
import fatecipi.progweb.mymanga.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;
import fatecipi.progweb.mymanga.services.MangaService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private MangaCardResponse mangaCardResponse;

    private VolumeResponse volumeResponse;
    private VolumeCreate volumeCreate;

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
                null,
                "imageUrl.com"
        );

        mangaCardResponse = new MangaCardResponse(
                1L,
                "Test",
                "Test Author",
                "Test Mangá description",
                1.5,
                MangaStatus.COMPLETED,
                Genres.ACTION,
                "imageUrl.com"
        );

        mangaUpdate = new MangaUpdate(
                "Test Updated",
                "Test Author Updated",
                "Test Mangá description updated",
                1.5,
                MangaStatus.PAUSED,
                Genres.ADVENTURE,
                "Updated Keywords",
                "imageUrl.com"
        );

        mangaCreate = new MangaCreate(
                "Test",
                "Test Author",
                "Test Mangá description",
                1.5,
                MangaStatus.COMPLETED,
                Genres.ACTION,
                "Test",
                "imageUrl.com"
        );

        volumeResponse = new VolumeResponse(
                1L,
                1,
                BigDecimal.valueOf(10),
                "1 to 10",
                LocalDate.now(),
                10,
                mangaResponse.id(),
                mangaResponse.title()
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
                        .get("/mangas/{id}", 1L)
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
            Page<MangaCardResponse> page = new PageImpl<>(List.of(mangaCardResponse), PageRequest.of(0,10) , 1);
            when(mangaService.listAll(any(PageRequest.class))).thenReturn(page);

            RestAssuredMockMvc
                    .given()
                    .when().get("/mangas/all?page=0&size=10")
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
                    .param("keyword", "Test")
                    .when()
                    .get("/mangas/search")
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
                    null,
                    "imageUrlUpdated"
            );
            when(mangaService.update(anyLong(), any(MangaUpdate.class))).thenReturn(mangaResponse);

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(mangaUpdate)
                    .when().patch("/mangas/{id}", 1L)
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
            doReturn(mangaResponse).when(mangaService).create(any(MangaCreate.class));

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(mangaCreate)
                    .when().post("/mangas/new")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("title", equalTo("Test"));
            verify(mangaService, times(1)).create(mangaCreate);
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
                    .delete("/mangas/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            verify(mangaService, times(1)).deleteMangaById(anyLong());
        }
    }



    @Nested
    class findVolumeById {
        @Test
        @DisplayName("GET /my-manga/mangas/{id}/volumes/{volId} - should return Volume Response" +
                "when everything is ok")
        void findVolumeById_returnVolumeResponse_WhenEverythingIsOk() {
            doReturn(volumeResponse).when(mangaService).getVolumeResponseById(anyLong(), anyLong());

            RestAssuredMockMvc
                    .given()
                    .get("/mangas/{id}/volumes/{volId}", 1L, 1L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(1));
            verify(mangaService, times(1)).getVolumeResponseById(anyLong(), anyLong());
        }
    }

    @Nested
    class getAllVolumesForManga {
        @Test
        @DisplayName("GET /my-manga/mangas/{id}/volumes/all - should return Page of Volume Response" +
                "when everything is ok")
        void getAllVolumesForManga_returnpAGEVolumeResponse_WhenEverythingIsOk() {
            Page<MangaResponse> pageResponse = new PageImpl<>(List.of(mangaResponse), PageRequest.of(0, 10), 1);
            doReturn(pageResponse).when(mangaService).getAllVolumesForManga(anyLong(), any());

            RestAssuredMockMvc
                    .given()
                    .get("/mangas/{id}/volumes/all", 1L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", equalTo(1))
                    .body("totalPages", equalTo(1))
                    .body("content[0].title", equalTo("Test"));
            verify(mangaService, times(1)).getAllVolumesForManga(anyLong(), any());
        }
    }

    @Nested
    class addVolumesToManga {
        @Test
        @DisplayName("POST /my-manga/mangas/{id}/volumes/new - should return a List of Volume" +
                "Response when everything is ok")
        void addVolumesToManga_returnListVolumeResponse_WhenEverythingIsOk() {
            doReturn(List.of(volumeResponse)).when(mangaService).addVolumesToManga(anyLong(), anyList());
            List<VolumeCreate> createList = new ArrayList<>();
            createList.add(volumeCreate);

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(createList)
                    .post("/mangas/{id}/volumes/new", 1L)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("[0].mangaTitle", equalTo("Test"));
            verify(mangaService, times(1)).addVolumesToManga(anyLong(), anyList());
        }
    }

    @Nested
    class updateVolume {
        @Test
        @DisplayName("PATCH /my-manga/mangas/{id}/volumes/{volId} - should return Volume Response" +
                "when everything is ok")
        void updateVolume_returnVolumeResponse_WhenEverythingIsOk() {
            doReturn(volumeResponse).when(mangaService).updateVolume(anyLong(), anyLong(), any());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(mangaUpdate)
                    .patch("/mangas/{id}/volumes/{volId}", 1L, 1L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("mangaTitle", equalTo("Test"));
            verify(mangaService, times(1)).updateVolume(anyLong(), anyLong(), any());
        }
    }

    @Nested
    class deleteVolume {
        @Test
        @DisplayName("DELETE /my-manga/mangas/{id}/volumes/{volId} - should return No Content Response" +
                "Entity when everything is ok")
        void delete_returnNoContent_WhenEverythingIsOk() {
            doNothing().when(mangaService).deleteVolumeById(anyLong(), anyLong());

            RestAssuredMockMvc
                    .given()
                    .delete("/mangas/{id}/volumes/{volId}", 1L, 1L)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            verify(mangaService, times(1)).deleteVolumeById(anyLong(), anyLong());
        }
    }
}