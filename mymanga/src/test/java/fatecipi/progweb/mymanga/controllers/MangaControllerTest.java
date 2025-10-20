package fatecipi.progweb.mymanga.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.services.MangaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MangaController.class)
@ExtendWith(MockitoExtension.class)
class MangaControllerTest {
    @Mock
    private MangaService mangaService;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class create {
        @Test
        @DisplayName("POST - /my-manga/mangas/new - should return 200 and ResponseEntity of MangaResponse")
        void create_returnMangaResponse_whenEverythingIsOk() throws Exception {
            MangaCreate mangaCreate = MangaCreate.builder()
                    .title("Test")
                    .author("Test author")
                    .description("Test description")


                    .build();
            given(mangaService.save(any(MangaCreate.class))).willAnswer((invocation -> invocation.getArgument(0)));

            ResultActions output = mvc.perform(post("/my-manga/mangas/new")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mangaCreate)));
        }
    }
}

