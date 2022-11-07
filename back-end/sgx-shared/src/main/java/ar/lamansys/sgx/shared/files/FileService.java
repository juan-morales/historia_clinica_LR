package ar.lamansys.sgx.shared.files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ar.lamansys.sgx.shared.actuator.infrastructure.configuration.AppNode;
import ar.lamansys.sgx.shared.context.BeanUtil;
import ar.lamansys.sgx.shared.files.exception.FileServiceEnumException;
import ar.lamansys.sgx.shared.files.exception.FileServiceException;
import ar.lamansys.sgx.shared.files.infrastructure.configuration.interceptors.FileErrorEvent;
import ar.lamansys.sgx.shared.files.infrastructure.input.rest.backoffice.dto.FileInfoDto;
import ar.lamansys.sgx.shared.files.infrastructure.output.repository.FileErrorInfo;
import ar.lamansys.sgx.shared.files.infrastructure.output.repository.FileInfo;
import ar.lamansys.sgx.shared.files.infrastructure.output.repository.FileInfoRepository;

@Component
public class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    private static final String OUTPUT = "Output -> {}";

    private final StreamFile streamFile;
	private final FileConfiguration fileConfiguration;

	private final FileInfoRepository repository;
	private final AppNode appNode;
    public FileService(StreamFile streamFile, FileConfiguration fileConfiguration,
					   FileInfoRepository repository, AppNode appNode){
        this.streamFile = streamFile;
		this.fileConfiguration = fileConfiguration;
		this.repository = repository;
		this.appNode = appNode;
	}
	public final String getSpaceDocumentLocation() {
		try {
			FileStore fs = Files.getFileStore(fileConfiguration.getDocumentsLocation().toPath());
			return String.format("%s/%s", FileUtils.byteCountToDisplaySize(fs.getUsableSpace()), FileUtils.byteCountToDisplaySize(fs.getTotalSpace()));
		} catch (IOException e) {
			LOG.error(e.toString());
			saveFileError(new FileErrorInfo(fileConfiguration.getDocumentsLocation().toPath().toString(), String.format("getSpaceDocumentLocation error => %s", e), appNode.nodeId));
			return String.format("Error en la lectura del directorio %s", fileConfiguration.getDocumentsLocation().toPath());
		}
	}
	public final String getSpaceMultipartLocation() {
		try {
			FileStore fs = Files.getFileStore(fileConfiguration.getMultipartLocation().toPath());
			return String.format("%s/%s", FileUtils.byteCountToDisplaySize(fs.getUsableSpace()), FileUtils.byteCountToDisplaySize(fs.getTotalSpace()));
		} catch (IOException e) {
			LOG.error(e.toString());
			saveFileError(new FileErrorInfo(fileConfiguration.getMultipartLocation().toPath().toString(), String.format("getSpaceMultipartLocation error => %s", e), appNode.nodeId));
			return String.format("Error en la lectura del directorio %s", fileConfiguration.getMultipartLocation().toPath());
		}
	}

    public String buildCompletePath(String fileRelativePath){
        LOG.debug("Input paramenter -> fileRelativePath {}", fileRelativePath);
        String path = streamFile.buildPathAsString(fileRelativePath);
        LOG.debug(OUTPUT, path);
        return path;
    }

    public String createFileName(String extension){
        String result = UUID.randomUUID().toString() + '.' + extension;
        LOG.debug(OUTPUT, result);
        return result;
    }

    public FileInfo transferMultipartFile(String partialPath, String uuid, String generatedFrom, MultipartFile file) throws IOException {
		String path = buildCompletePath(partialPath);
		File dirPath = new File(path);
        try {
			if (!dirPath.getParentFile().exists())
				if (!dirPath.getParentFile().mkdirs())
					throw new FileServiceException(FileServiceEnumException.CANNOT_CREATE_FOLDER, String.format("La carpeta %s no puede ser creada", dirPath.getParentFile()));
			FileStore fs = Files.getFileStore(dirPath.getParentFile().toPath());
			if (fs.getUsableSpace() < file.getSize())
				throw new FileServiceException(FileServiceEnumException.INSUFFICIENT_STORAGE,
						String.format("La carpeta %s no tiene espacio suficiente (%s) para alojar el archivo de tamaño %s",
								dirPath.getParentFile(),
								FileUtils.byteCountToDisplaySize(fs.getUsableSpace()),
								FileUtils.byteCountToDisplaySize(file.getSize())));
			file.transferTo(dirPath);
            LOG.debug(OUTPUT, true);
            return saveFileInfo(partialPath, uuid, generatedFrom, getHash(path), file);
        }  catch (FileServiceException e) {
			saveFileError(new FileErrorInfo(path, String.format("transferMultipartFile error => %s", e), appNode.nodeId));
			throw e;
		}
		catch (IOException e) {
			saveFileError(new FileErrorInfo(path, String.format("transferMultipartFile error => %s", e), appNode.nodeId));
			LOG.error(e.toString());
			throw new FileServiceException(FileServiceEnumException.SAVE_IOEXCEPTION,
					String.format("El guardado del siguiente archivo %s tuvo el siguiente error %s", dirPath.getAbsolutePath(), e.getMessage()));
        }
    }

	private FileInfo saveFileInfo(String path, String uuid, String generatedFrom, String checksum, MultipartFile file) {
		return repository.save(new FileInfo(
				file.getOriginalFilename(),
				path,
				file.getContentType(),
				file.getSize(),
				uuid,
				checksum,
				generatedFrom));
	}

	public Resource loadFileRelativePath(String relativeFilePath) {
        Path path = streamFile.buildPath(relativeFilePath);
        try {
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
			saveFileError(new FileErrorInfo(relativeFilePath, String.format("loadFile error => %s", e), appNode.nodeId));
			LOG.error(e.toString());
        }
        return null;
    }

	public Resource loadFileFromAbsolutePath(String absolutePath) {
		Path path = Paths.get(absolutePath);
		try {
			return new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			saveFileError(new FileErrorInfo(absolutePath, e.getMessage(), appNode.nodeId));
			LOG.error(e.getMessage());
		}
		return null;
	}
	public FileInfo saveStreamInPath(String partialPath, String uuid, String generatedFrom, boolean override,
									 ByteArrayOutputStream byteArrayOutputStream) {
		String path = buildCompletePath(partialPath);
		File dirPath = new File(path);
		try {
			streamFile.saveFileInDirectory(path, override, byteArrayOutputStream);
			return saveFileInfo(partialPath, uuid, generatedFrom, getHash(path), dirPath);
		} catch (IOException e) {
			saveFileError(new FileErrorInfo(dirPath.getPath(), String.format("saveStreamInPath error => %s", e), appNode.nodeId));
			LOG.error(e.toString());
			throw new FileServiceException(FileServiceEnumException.SAVE_IOEXCEPTION,
					String.format("El guardado del siguiente archivo %s tuvo el siguiente error %s", dirPath.getAbsolutePath(), e.getMessage()));
		}
	}

	private FileInfo saveFileInfo(String path, String uuid, String generatedFrom, String checksum,  File file) throws IOException {
		return repository.save(new FileInfo(
				file.getName(),
				path,
				parseToContentType(file.getName()),
				Files.size(file.toPath()),
				uuid,
				checksum,
				generatedFrom));
	}

	private String parseToContentType(String fileName) {
		return URLConnection.guessContentTypeFromName(fileName);
	}

	public String readFileAsString(String path, Charset encoding) {
		File dirPath = new File(path);
		try {
			return streamFile.readFileAsString(path, encoding);
		} catch (IOException e) {
			saveFileError(new FileErrorInfo(dirPath.getPath(), String.format("readFileAsString error => %s", e), appNode.nodeId));
			LOG.error(e.toString());
			throw new FileServiceException(FileServiceEnumException.SAVE_IOEXCEPTION,
					String.format("La lectura del siguiente archivo %s tuvo el siguiente error %s", dirPath.getAbsolutePath(), e.getMessage()));
		}
	}

	public ByteArrayInputStream readStreamFromRelativePath(String partialPath) {
		LOG.debug("Input parameters -> partialPath {}", partialPath);
		String path = buildCompletePath(partialPath);
		return readStreamFromAbsolutePath(path);
	}

	public ByteArrayInputStream readStreamFromAbsolutePath(String absolutePath) {
		LOG.debug("Input parameters -> absolutePath {}", absolutePath);
		File dirPath = new File(absolutePath);
		try {
			Path pdfPath = Paths.get(absolutePath);
			byte[] pdf = Files.readAllBytes(pdfPath);
			LOG.debug("Output -> path {}", absolutePath);
			return new ByteArrayInputStream(pdf);
		}  catch (IOException e){
			saveFileError(new FileErrorInfo(dirPath.getPath(), String.format("readStreamFromPath error => %s", e), appNode.nodeId));
			LOG.error(e.toString());
			throw new FileServiceException(FileServiceEnumException.SAVE_IOEXCEPTION,
					String.format("La lectura del siguiente archivo %s tuvo el siguiente error %s", dirPath.getAbsolutePath(), e.getMessage()));
		}
	}

	private void saveFileError(FileErrorInfo fileErrorInfo) {
		BeanUtil.publishEvent(new FileErrorEvent(fileErrorInfo));
	}

	public boolean deleteFile(String partialPath) {
		String completePath = buildCompletePath(partialPath);
		if (!streamFile.deleteFileInDirectory(completePath))
			return false;
		repository.deleteByRelativePath(partialPath);
        return true;
    }


	private static String getHash(String path) {
		LOG.debug("Input parameters -> path {}", path);
		String result;
		String algorithm = "SHA-256";
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] sha256Hash = md.digest(Files.readAllBytes(Paths.get(path)));
			result = Base64.getEncoder().encodeToString(sha256Hash);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("Algorithm doesn't exist -> {} ",algorithm);
			result = null;
		}
		catch (IOException e) {
			LOG.error("Error with path file {} ", path, e);
			result = null;
		}
		LOG.debug(OUTPUT, result);
		return result;
	}

	public long getFileSize(FileInfoDto fileInfo) throws IOException {
		String completePath = buildCompletePath(fileInfo.getRelativePath());
		Long unknownSize = -1L;
		if (!unknownSize.equals(fileInfo.getSize()))
			return fileInfo.getSize();
		try {
			return Files.size(Paths.get(completePath));
		}  catch (FileServiceException | IOException e){
			return Files.size(Paths.get(fileInfo.getOriginalPath()));
		}
	}
}
