package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.mappers.MangaMapper;
import fatecipi.progweb.mymanga.configs.mappers.VolumeMapper;
import fatecipi.progweb.mymanga.enums.Genres;
import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreateAndUpdate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.repositories.MangaRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MangaServiceTest {
    @Mock
    private MangaRepository mangaRepository;
    @Mock
    private MangaMapper mangaMapper;
    @Mock
    private VolumeRepository volumeRepository;
    @Mock
    private VolumeMapper volumeMapper;
    @InjectMocks
    private MangaService mangaService;

    @Captor
    private ArgumentCaptor<MangaCreateAndUpdate> mangaCreateAndUpdateCaptor;
    @Captor
    private ArgumentCaptor<Manga> mangaCaptor;
    @Captor
    private ArgumentCaptor<MangaResponse> mangaResponseCaptor;

    @Nested
    class listAll {
        @Test
        @DisplayName("should return a page of mangaresponse successfully")
        void listAll_returnPageMangaResponse_whenEverythingOk() {
            //Arrange
            Manga manga = new Manga(
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
            Pageable pageable = Pageable.ofSize(10);
            Page<Manga> page = new PageImpl<>(List.of(manga), pageable, 1);
            PageRequest pageRequest = PageRequest.of(0, 10);
            when(mangaRepository.findAll(pageRequest)).thenReturn(page);

            //Act
            Page<MangaResponse> pageResponse = mangaService.listAll(pageRequest);

            //Assert
            assertNotNull(pageResponse);
            assertEquals(1, pageResponse.getTotalElements());
            verify(mangaRepository, atLeastOnce()).findAll(pageRequest);
        }
    }

    @Nested
    class findByIdWithoutDto {
        @Test
        @DisplayName("should return a manga successfully")
        void findByIdWithoutDto_returnManga_whenEverythingIsOK() {
            //Arrange
            Manga manga = new Manga(
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
            doReturn(Optional.of(manga)).when(mangaRepository).findById(anyLong());

            //Act
            Manga output = mangaService.findByIdWithoutDto(1L);

            //Assert
            assertNotNull(output);
            verify(mangaRepository, atLeastOnce()).findById(1L);
        }

        @Test
        @DisplayName("should throw a exception when the manga dont exists")
        void findByIdWithoutDto_throwResourceNotFoundException_whenMangaDontExists() {
            //Arrange
            when(mangaRepository.findById(anyLong())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> mangaService.findByIdWithoutDto(anyLong()));
        }
    }

    @Nested
    class findById {
        @Test
        @DisplayName("should return a mangaResponse succesfully when the manga exists")
        void findById_returnMangaResponse_whenEverythingIsOK() {
            //Arrange
            Manga manga = new Manga(
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
            MangaResponse mangaResponse = new MangaResponse(
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
            when(mangaMapper.toMangaResponseDto(any(Manga.class))).thenReturn(mangaResponse);
            when(mangaRepository.findById(1L)).thenReturn(Optional.of(manga));

            //Act
            MangaResponse output = mangaService.findById(1L);

            //Assert
            assertNotNull(output);
            verify(mangaMapper, times(1)).toMangaResponseDto(mangaCaptor.capture());
            verify(mangaRepository, atLeastOnce()).findById(1L);

            Manga capturedManga = mangaCaptor.getValue();
            assertEquals(capturedManga.getId(), output.id());
            assertEquals(capturedManga.getTitle(), output.title());
        }
        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Mangá is not found")
        void findById_throwResourceNotFoundException_whenTheMangáIsNotFound() {
            //Arrange
            doReturn(Optional.empty()).when(mangaRepository).findById(anyLong());

            //Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> mangaService.findById(1L));
            verify(mangaRepository, atLeastOnce()).findById(1L);
        }
    }

    @Nested
    class findByKeywords {
        @Test
        @DisplayName("Should return a Page of MangaResponse successfully when searching with keyword")
        void findByKeywords_returnPageMangaResponse_whenEverythingIsOK() {
            Manga manga = new Manga(
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
            MangaResponse mangaResponse = new MangaResponse(
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
            Pageable pageable = PageRequest.of(0, 10);
            Page<Manga> pageResponse = new PageImpl<>(List.of(manga), pageable, 1);
            when(mangaMapper.toMangaResponseDto(manga)).thenReturn(mangaResponse);
            when(mangaRepository.findByKeyword("test", pageable)).thenReturn(pageResponse);

            Page<MangaResponse> output = mangaService.findByKeyword("test", pageable);

            assertNotNull(output);
            assertEquals(1, output.getTotalElements());
            verify(mangaMapper, times(1)).toMangaResponseDto(any(Manga.class));
            verify(mangaRepository, atLeastOnce()).findByKeyword("test", pageable);
        }
    }

    @Nested
    class deleteById {
        @Test
        @DisplayName("Should delete a manga successfully when everything is ok")
        void deleteById_returnVoid_whenEverythingIsOK() {
            Manga manga = new Manga(
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
            Long mangaId = manga.getId();
            doReturn(Optional.of(manga)).when(mangaRepository).findById(mangaId);
            doNothing().when(mangaRepository).delete(manga);

            mangaService.deleteById(mangaId);

            verify(mangaRepository, times(1)).delete(manga);
            verify(mangaRepository, times(1)).findById(mangaId);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Mangá is not found")
        void deleteById_throwResourceNotFoundException_whenTheMangaIsNotFound() {
            //Arrange
            doReturn(Optional.empty()).when(mangaRepository).findById(anyLong());

            //Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> mangaService.deleteById(1L));
            verify(mangaRepository, atLeastOnce()).findById(1L);
        }
    }

    @Nested
    class update {
        @Test
        @DisplayName("Should update a Mangá successfully when everything is ok")
        void update_returnMangaResponse_whenEverythingIsOK() {
            Manga mangaFound = new Manga(
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
            MangaCreateAndUpdate mangaUpdate = new MangaCreateAndUpdate(
                    "Test updated",
                    "Test Author update",
                    "Test Mangá test test updated",
                    1.5,
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    "Test"
            );
            MangaResponse mangaResponse = new MangaResponse(
                    1L,
                    "Test updated",
                    "Test Author update",
                    "Test Mangá test test updated",
                    1.5,
                    "Test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            when(mangaRepository.findById(1L)).thenReturn(Optional.of(mangaFound));
            doNothing().when(mangaMapper).mapManga(mangaUpdate, mangaFound);
            doReturn(mangaFound).when(mangaRepository).save(mangaFound);
            doReturn(mangaResponse).when(mangaMapper).toMangaResponseDto(mangaFound);

            MangaResponse output = mangaService.update(1L, mangaUpdate);

            assertNotNull(output);
            verify(mangaRepository, times(1)).findById(1L);
            verify(mangaMapper, times(1)).mapManga(any(), any());
            verify(mangaRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Mangá is not found")
        void update_throwResourceNotFoundException_whenTheMangaIsNotFound() {
            //Arrange
            MangaCreateAndUpdate mangaUpdate = new MangaCreateAndUpdate(
                    "Test updated",
                    "Test Author update",
                    "Test Mangá test test updated",
                    1.5,
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    "Test"
            );
            doReturn(Optional.empty()).when(mangaRepository).findById(anyLong());

            //Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> mangaService.update(1L, mangaUpdate));
            verify(mangaRepository, times(1)).findById(1L);
        }
    }

    @Nested
    class save {
        @Test
        @DisplayName("should save a Mangá successfully when everything is ok")
        void save_returnMangaResponse_whenEverythingIsOK() {
            MangaCreateAndUpdate newManga = new MangaCreateAndUpdate(
                    "Test",
                    "Test Author",
                    "Test Mangá test test",
                    1.5,
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Manga mappedManga = new Manga(
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
            Manga m = new Manga();
            MangaResponse mangaResponse = new MangaResponse(
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
            doReturn(false).when(mangaRepository).existsByTitle(anyString());
            doNothing().when(mangaMapper).mapManga(any(), any());
            doReturn(mappedManga).when(mangaRepository).save(any());
            doReturn(mangaResponse).when(mangaMapper).toMangaResponseDto(any());

            MangaResponse output = mangaService.save(newManga);

            assertNotNull(output);
            verify(mangaRepository, times(1)).existsByTitle(anyString());
            verify(mangaRepository, times(1)).save(any());
            verify(mangaMapper, times(1)).mapManga(any(), any());
            assertEquals(newManga.title(), output.title());
        }

        @Test
        @DisplayName("should throw a exception when the Mangá already exists")
        void save_throwException_whenTheMangaAlreadyExists() {
            MangaCreateAndUpdate newManga = new MangaCreateAndUpdate(
                    "Test",
                    "Test Author",
                    "Test Mangá test test",
                    1.5,
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            doReturn(true).when(mangaRepository).existsByTitle(anyString());

            assertThrows(ResourceAlreadyExistsException.class, () -> mangaService.save(newManga));
        }
    }
}