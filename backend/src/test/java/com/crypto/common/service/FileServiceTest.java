//package com.crypto.common.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.web.client.HttpClientErrorException;
//
//import static java.lang.String.valueOf;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.http.HttpStatus.INSUFFICIENT_STORAGE;
//
//@ExtendWith(MockitoExtension.class)
//class FileServiceTest {
//    private static final String DOCUMENT_DIRECTORY = "documentDirectory/";
//    private static final String FILE_REF = "fileRef";
//
//    @Mock
//    private Resource resource;
//    @Mock
//    private ResourceLoader resourceLoader;
//    @InjectMocks
//    private FileService fileService;
//
//    @Test
//    public void should_throw_error_when_file_not_exists() {
//        when(resourceLoader.getResource(any())).thenReturn(resource);
//        when(resource.exists()).thenReturn(false);
//
//        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () ->
//                fileService.downloadFile(DOCUMENT_DIRECTORY, FILE_REF));
//        assertNotNull(thrown.getMessage());
//        assertTrue(thrown.getMessage().contains(valueOf(INSUFFICIENT_STORAGE.value())));
//
//        verify(resourceLoader).getResource(any());
//        verify(resource).exists();
//        verifyNoMoreInteractions(resourceLoader, resource);
//    }
//}