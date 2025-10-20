package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.mappers.MangaMapper;
import fatecipi.progweb.mymanga.mappers.VolumeMapper;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaUpdate;
import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.Volume;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeUpdate;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private ArgumentCaptor<Manga> mangaCaptor;
    @Captor
    private ArgumentCaptor<Volume> volumeCaptor;
    @Captor
    private ArgumentCaptor<VolumeUpdate> volumeUpdateCaptor;

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
    class findMangaByIdWithoutDto {
        @Test
        @DisplayName("should return a manga successfully")
        void findMangaByIdWithoutDto_returnManga_whenEverythingIsOK() {
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
            Manga output = mangaService.findMangaById(1L);

            //Assert
            assertNotNull(output);
            verify(mangaRepository, atLeastOnce()).findById(1L);
        }

        @Test
        @DisplayName("should throw a exception when the manga dont exists")
        void findMangaByIdWithoutDto_throwResourceNotFoundException_whenMangaDontExists() {
            //Arrange
            when(mangaRepository.findById(anyLong())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> mangaService.findMangaById(anyLong()));
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
            when(mangaMapper.responseMapping(any(Manga.class))).thenReturn(mangaResponse);
            when(mangaRepository.findById(1L)).thenReturn(Optional.of(manga));

            //Act
            MangaResponse output = mangaService.getMangaResponseById(1L);

            //Assert
            assertNotNull(output);
            verify(mangaMapper, times(1)).responseMapping(mangaCaptor.capture());
            verify(mangaRepository, atLeastOnce()).findById(1L);

            Manga capturedManga = mangaCaptor.getValue();
            assertEquals(capturedManga.getId(), output.id());
            assertEquals(capturedManga.getTitle(), output.title());
        }
        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Mangá is not found")
        void findById_throwResourceNotFoundException_whenTheMangaIsNotFound() {
            //Arrange
            doReturn(Optional.empty()).when(mangaRepository).findById(anyLong());

            //Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> mangaService.getMangaResponseById(1L));
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
            when(mangaMapper.responseMapping(manga)).thenReturn(mangaResponse);
            when(mangaRepository.findByKeyword("test", pageable)).thenReturn(pageResponse);

            Page<MangaResponse> output = mangaService.findByKeyword("test", pageable);

            assertNotNull(output);
            assertEquals(1, output.getTotalElements());
            verify(mangaMapper, times(1)).responseMapping(any(Manga.class));
            verify(mangaRepository, atLeastOnce()).findByKeyword("test", pageable);
        }
    }

    @Nested
    class deleteMangaById {
        @Test
        @DisplayName("Should delete a manga successfully when everything is ok")
        void deleteMangaById_returnVoid_whenEverythingIsOK() {
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
            doReturn(true).when(mangaRepository).existsById(anyLong());
            doNothing().when(mangaRepository).deleteById(anyLong());

            mangaService.deleteMangaById(mangaId);

            verify(mangaRepository, times(1)).deleteById(mangaId);
            verify(mangaRepository, times(1)).existsById(mangaId);
        }

        @Test
        @DisplayName("should throw a IllegalArgumentException when the Mangá is not found")
        void deleteMangaById_throwIllegalArgumentException_whenTheMangaIsNotFound() {
            //Arrange
            doReturn(false).when(mangaRepository).existsById(anyLong());

            //Act & Assert
            assertThrows(IllegalArgumentException.class, () -> mangaService.deleteMangaById(1L));
            verify(mangaRepository, atLeastOnce()).existsById(1L);
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
            MangaUpdate mangaUpdate = new MangaUpdate(
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
            doNothing().when(mangaMapper).updateMapping(mangaUpdate, mangaFound);
            doReturn(mangaFound).when(mangaRepository).save(mangaFound);
            doReturn(mangaResponse).when(mangaMapper).responseMapping(mangaFound);

            MangaResponse output = mangaService.update(1L, mangaUpdate);

            assertNotNull(output);
            verify(mangaRepository, times(1)).findById(1L);
            verify(mangaMapper, times(1)).createMapping(any(), any());
            verify(mangaRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Mangá is not found")
        void update_throwResourceNotFoundException_whenTheMangaIsNotFound() {
            //Arrange
            MangaUpdate mangaUpdate = new MangaUpdate(
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
            MangaCreate newManga = new MangaCreate(
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
            doNothing().when(mangaMapper).createMapping(any(), any());
            doReturn(mappedManga).when(mangaRepository).save(any());
            doReturn(mangaResponse).when(mangaMapper).responseMapping(any());

            MangaResponse output = mangaService.save(newManga);

            assertNotNull(output);
            verify(mangaRepository, times(1)).existsByTitle(anyString());
            verify(mangaRepository, times(1)).save(any());
            verify(mangaMapper, times(1)).createMapping(any(), any());
            assertEquals(newManga.title(), output.title());
        }

        @Test
        @DisplayName("should throw a exception when the Mangá already exists")
        void save_throwException_whenTheMangaAlreadyExists() {
            MangaCreate newManga = new MangaCreate(
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




    @Nested
    class addVolumesToManga {
        @Test
        @DisplayName("Should return a List of MangaResponse successfully when everything is ok")
        void addVolumesToManga_returnListMangaResponse_whenEverythingIsOK() {
            long mangaId = 1L;
            String mangaTitle = "Test";
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            List<VolumeCreate> input = new ArrayList<>();
            VolumeCreate volumeCreate = new VolumeCreate(
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            input.add(volumeCreate);
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            VolumeResponse volumeResponse = new VolumeResponse(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    mangaId,
                    mangaTitle
            );

            doReturn(Optional.of(manga)).when(mangaRepository).findById(mangaId);
            doNothing().when(volumeMapper).createMapping(any(VolumeCreate.class), any(Volume.class));
            doReturn(volume).when(volumeRepository).save(any(Volume.class));
            doReturn(volumeResponse).when(volumeMapper).responseMapping(any(Volume.class));

            List<VolumeResponse> output = mangaService.addVolumesToManga(mangaId, input);

            assertNotNull(output);
            verify(mangaRepository, times(1)).findById(anyLong());
            verify(volumeRepository, times(1)).save(any());
            verify(volumeMapper, times(1)).createMapping(any(), any());
            verify(volumeMapper, times(1)).responseMapping(any());
            assertEquals(mangaId, output.getFirst().mangaId());
            assertEquals(mangaTitle, output.getFirst().mangaTitle());
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Manga is not found")
        void addVolumesToManga_throwResourceNotFoundException_whenTheMangaIsNotFound() {
            long mangaId = 1L;
            List<VolumeCreate> input = new ArrayList<>();
            VolumeCreate volumeCreate = new VolumeCreate(
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    null
            );
            input.add(volumeCreate);
            doThrow(new ResourceNotFoundException("error")).when(mangaRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> mangaService.addVolumesToManga(mangaId, input));
        }

        @Test
        @DisplayName("should throw a IllegalArgumentException when the List of VolumeCreate is empty")
        void addVolumesToManga_throwIllegalArgumentException_whenTheListOfVolumeCreateIsEmpty() {
            long mangaId = 1L;
            List<VolumeCreate> input = new ArrayList<>();

            assertThrows(IllegalArgumentException.class, () -> mangaService.addVolumesToManga(mangaId, input));
        }
    }

    @Nested
    class getAllVolumesFromManga {
        @Test
        @DisplayName("Should return a Page of Volumes when everything is ok")
        void getAllVolumesForManga_returnPageVolume_whenEverythingIsOk() {
            long mangaId = 1L;
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    null
            );
            VolumeResponse volumeResponse = new VolumeResponse(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    mangaId,
                    null
            );
            Pageable pageable = PageRequest.of(0, 10);
            Page<Volume> volumePage = new PageImpl<>(List.of(volume), pageable, 1);

            doReturn(true).when(mangaRepository).existsById(mangaId);
            doReturn(volumePage).when(volumeRepository).findByMangaId(mangaId, pageable);
            doReturn(volumeResponse).when(volumeMapper).responseMapping(any(Volume.class));

            var output = mangaService.getAllVolumesForManga(mangaId, pageable);

            assertNotNull(output);
            verify(mangaRepository, times(1)).existsById(mangaId);
            verify(volumeRepository, times(1)).findByMangaId(mangaId, pageable);
            verify(volumeMapper, times(1)).responseMapping(any(Volume.class));
            assertEquals(1, output.getTotalElements());
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Manga dont exists")
        void getAllVolumesForManga_throwResourceNotFoundException_whenTheMangaDontExists() {
            long mangaId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            doReturn(false).when(mangaRepository).existsById(mangaId);

            assertThrows(ResourceNotFoundException.class, () -> mangaService.getAllVolumesForManga(mangaId, pageable));
        }
    }

    @Nested
    class findVolumeById {
        @Test
        @DisplayName("should return a VolumeResponse successfully when everything is OK")
        void findVolumeById_returnVolumeResponse_whenEverythingIsOk() {
            long mangaId = 1L;
            long volumeId = 1L;
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            VolumeResponse volumeResponse = new VolumeResponse(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    mangaId,
                    manga.getTitle()
            );

            doReturn(Optional.of(volume)).when(volumeRepository).findById(volumeId);
            doReturn(volumeResponse).when(volumeMapper).responseMapping(volume);

            var output = mangaService.getVolumeResponseById(mangaId, volumeId);

            assertNotNull(output);
            verify(volumeRepository, times(1)).findById(volumeId);
            verify(volumeMapper, times(1)).responseMapping(volume);
            assertEquals(volumeResponse, output);
        }

        @Test
        @DisplayName("should throw a IllegalArgumentException when the Manga isn't associated with the Volume")
        void findVolumeById_throwIllegalArgumentException_whenTheMangaDontExists() {
            long mangaId = 2L;
            long volumeId = 1L;
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            doReturn(Optional.of(volume)).when(volumeRepository).findById(volumeId);

            assertThrows(IllegalArgumentException.class, () -> mangaService.getVolumeResponseById(mangaId, volumeId));
        }
    }

    @Nested
    class findVolumeByWithoutDto {
        @Test
        @DisplayName("Should return a Volume when everything is ok")
        void findVolumeByWithoutDto_returnVolume_WhenEvevrythingIsOk() {
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    null
            );
            doReturn(Optional.of(volume)).when(volumeRepository).findById(volume.getId());

            var output = mangaService.getVolumeResponseById(1L);

            assertNotNull(output);
            verify(volumeRepository, times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("should throw a exception when the volume dont exists")
        void findVolumeByIdWithoutDto_throwResourceNotFoundException_whenMangaDontExists() {
            //Arrange
            doReturn(Optional.empty()).when(volumeRepository).findById(anyLong());

            //Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> mangaService.getVolumeResponseById(anyLong()));
        }
    }

    @Nested
    class updateVolume {
        @Test
        @DisplayName("Should return a VolumeResponse when everything is ok")
        void updateVolume_returnVolumeResponse_WhenEverythingIsOk() {
            long volumeId = 1L;
            long mangaId = 1L;
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            VolumeResponse volumeResponse = new VolumeResponse(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga.getId(),
                    manga.getTitle()
            );

            VolumeUpdate volumeUpdate = new VolumeUpdate(
                    BigDecimal.valueOf(11.00),
                    "1 to 11",
                    11,
                    LocalDate.now()
            );

            doReturn(Optional.of(volume)).when(volumeRepository).findById(volumeId);
            doNothing().when(volumeMapper).updateMapping(any(VolumeUpdate.class), any(Volume.class));
            doReturn(volume).when(volumeRepository).save(any(Volume.class));
            doReturn(volumeResponse).when(volumeMapper).responseMapping(volume);

            VolumeResponse output = mangaService.updateVolume(mangaId, volumeId, volumeUpdate);

            assertNotNull(output);
            verify(volumeRepository, times(1)).findById(volumeId);
            verify(volumeMapper, times(1)).updateMapping(volumeUpdateCaptor.capture(), volumeCaptor.capture());
            var captured = volumeCaptor.getValue();
            assertEquals(captured.getId(), output.id());
            verify(volumeMapper, times(1)).responseMapping(any());
            verify(volumeRepository, times(1)).save(any(Volume.class));

        }

        @Test
        @DisplayName("Should throw a ResourceNotFoundException when the Volume dont exists")
        void updateVolume_throwResourceNotFoundException_whenVolumeDontExists() {
            long volumeId = 1L;
            long mangaId = 1L;
            VolumeUpdate volumeUpdate = new VolumeUpdate(
                    BigDecimal.valueOf(11.00),
                    "1 to 11",
                    11,
                    LocalDate.now()
            );
            doReturn(Optional.empty()).when(volumeRepository).findById(volumeId);

            assertThrows(ResourceNotFoundException.class, () -> mangaService.updateVolume(mangaId, volumeId, volumeUpdate));
        }

        @Test
        @DisplayName("Should throw a IllegalArgumentException when the MangaId of the volume dont match the parameter MangaId")
        void updateVolume_throwIllegalArgumentException_whenVolumeMangaIdDontMatchParamMangaId() {
            long volumeId = 1L;
            long mangaId = 2L;
            VolumeUpdate volumeUpdate = new VolumeUpdate(
                    BigDecimal.valueOf(11.00),
                    "1 to 11",
                    11,
                    LocalDate.now()
            );
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );

            doReturn(Optional.of(volume)).when(volumeRepository).findById(volumeId);

            assertThrows(IllegalArgumentException.class, () -> mangaService.updateVolume(mangaId, volumeId, volumeUpdate));
        }
    }

    @Nested
    class deleteVolumeById {
        @Test
        @DisplayName("should return void when the Volume is deleted successfully")
        void deleteVolumeById_returnVoid_WhenEverythingIsOk() {
            long volumeId = 1L;
            long mangaId = 1L;
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            doReturn(Optional.of(volume)).when(volumeRepository).findById(volumeId);
            doNothing().when(volumeRepository).deleteById(anyLong());

            mangaService.deleteVolumeById(mangaId, volumeId);

            verify(volumeRepository, times(1)).deleteById(volumeId);
            verify(volumeRepository, times(1)).findById(volumeId);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Manga don't exists")
        void deleteVolumeById_throwResourceNotFoundException_WhenTheMangaDontExists() {
            long volumeId = 1L;
            long mangaId = 1L;

            doReturn(Optional.empty()).when(volumeRepository).findById(volumeId);

            assertThrows(ResourceNotFoundException.class, () -> mangaService.deleteVolumeById(mangaId, volumeId));
        }

        @Test
        @DisplayName("should return void when the Volume is deleted successfully")
        void deleteVolumeById_throwResourceNotFoundException_WhenTheMangaIdDontMatchParamMangaId() {
            long volumeId = 1L;
            long mangaId = 2L;
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            doReturn(Optional.of(volume)).when(volumeRepository).findById(volumeId);

            assertThrows(IllegalArgumentException.class, () -> mangaService.deleteVolumeById(mangaId, volumeId));
        }
    }
}