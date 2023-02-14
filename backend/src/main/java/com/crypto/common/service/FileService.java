package com.crypto.common.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;

import static com.crypto.api.I18N.FILE_NOT_FOUND;
import static com.crypto.utils.SpringUtils.checkArgumentCustom;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final ResourceLoader resourceLoader;

    @SneakyThrows
    public void uploadFile(String documentDirectory, String fileRef, byte[] file) {
        WritableResource resource = (WritableResource) resourceLoader
                .getResource(documentDirectory + fileRef);
        try (OutputStream stream = resource.getOutputStream()) {
            stream.write(file);
        }
    }

    @SneakyThrows
    public byte[] downloadFile(String documentDirectory, String fileRef) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(documentDirectory + fileRef)) {
            checkArgumentCustom(null != inputStream && inputStream.available() > 0, FILE_NOT_FOUND);
            return inputStream.readAllBytes();
        }
    }
}
