package net.pladema.settings.service;

import net.pladema.assets.service.domain.Assets;
import org.apache.http.MethodNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface SettingsService {
    boolean uploadFile(Optional<Assets> newAsset, MultipartFile file) throws MethodNotSupportedException, IOException;

    boolean deleteFile(Optional<Assets> newAsset) throws MethodNotSupportedException;
}
