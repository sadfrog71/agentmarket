package com.ruoyi.site.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.site.domain.SiteMedia;
import com.ruoyi.site.mapper.SiteMediaMapper;
import com.ruoyi.site.service.ISiteMediaAdminService;

@Service
public class SiteMediaAdminServiceImpl implements ISiteMediaAdminService
{
    private static final long MAX_MEDIA_SIZE = 25L * 1024 * 1024;
    private static final Map<String, MediaDescriptor> ACCEPTED_MEDIA = Map.of(
            "application/pdf", new MediaDescriptor("DOCUMENT", ".pdf"),
            "image/jpeg", new MediaDescriptor("IMAGE", ".jpg"),
            "image/png", new MediaDescriptor("IMAGE", ".png"),
            "image/webp", new MediaDescriptor("IMAGE", ".webp"));

    @Autowired
    private SiteMediaMapper mediaMapper;

    @Override
    public List<SiteMedia> selectSiteMediaList(SiteMedia media)
    {
        return mediaMapper.selectSiteMediaList(media);
    }

    @Override
    public SiteMedia uploadAvailableMedia(MultipartFile file, String operator) throws IOException
    {
        if (file == null || file.isEmpty()) throw new ServiceException("请选择需要上传的官网媒体文件");
        if (file.getSize() > MAX_MEDIA_SIZE) throw new ServiceException("官网媒体单个文件不能超过 25MB");
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        MediaDescriptor descriptor = ACCEPTED_MEDIA.get(contentType);
        if (descriptor == null) throw new ServiceException("官网媒体只支持 PDF、JPG、PNG 和 WEBP 文件");

        Path root = Path.of(RuoYiConfig.getSiteMediaPath()).toAbsolutePath().normalize();
        Path stagingDirectory = root.resolve(".staging").normalize();
        Path availableDirectory = root.resolve("available").normalize();
        Files.createDirectories(stagingDirectory);
        Files.createDirectories(availableDirectory);
        String publicId = UUID.randomUUID().toString();
        Path staging = stagingDirectory.resolve(publicId + ".upload").normalize();
        Path target = availableDirectory.resolve(publicId + descriptor.extension()).normalize();
        if (!staging.startsWith(root) || !target.startsWith(root)) throw new ServiceException("官网媒体存储路径不安全");

        try
        {
            String contentHash;
            try (InputStream input = file.getInputStream())
            {
                Files.copy(input, staging, StandardCopyOption.REPLACE_EXISTING);
            }
            contentHash = sha256(staging);
            if (Files.size(staging) != file.getSize()) throw new ServiceException("官网媒体上传尺寸校验失败");
            if (!matchesExpectedSignature(staging, contentType)) throw new ServiceException("官网媒体文件内容与申报类型不匹配");
            try
            {
                Files.move(staging, target, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException ignored)
            {
                Files.move(staging, target);
            }
            SiteMedia media = new SiteMedia();
            media.setPublicId(publicId);
            media.setMediaKind(descriptor.kind());
            media.setOriginalFilename(safeFilename(file.getOriginalFilename()));
            media.setContentSha256(contentHash);
            media.setMimeType(contentType);
            media.setFileSize(file.getSize());
            media.setMediaState("AVAILABLE");
            media.setStorageKey("available/" + target.getFileName());
            media.setPublicPath("/open/site/v1/media/" + publicId);
            media.setCreateBy(operator);
            media.setRemark("后台上传；待被已发布官网内容引用后公开");
            mediaMapper.insertSiteMedia(media);
            return media;
        }
        finally
        {
            Files.deleteIfExists(staging);
        }
    }

    private String safeFilename(String filename)
    {
        String value = filename == null ? "官网媒体" : Path.of(filename).getFileName().toString();
        return value.replaceAll("[\\r\\n\\t]", "_");
    }

    private boolean matchesExpectedSignature(Path file, String contentType) throws IOException
    {
        byte[] header;
        try (InputStream input = Files.newInputStream(file))
        {
            header = input.readNBytes(12);
        }
        if ("application/pdf".equals(contentType)) return header.length >= 4 && header[0] == '%' && header[1] == 'P' && header[2] == 'D' && header[3] == 'F';
        if ("image/jpeg".equals(contentType)) return header.length >= 3 && (header[0] & 0xff) == 0xff && (header[1] & 0xff) == 0xd8 && (header[2] & 0xff) == 0xff;
        if ("image/png".equals(contentType)) return header.length >= 8 && (header[0] & 0xff) == 0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G';
        return "image/webp".equals(contentType) && header.length >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F' && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
    }

    private String sha256(Path file) throws IOException
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = Files.newInputStream(file))
            {
                byte[] buffer = new byte[8192];
                for (int read; (read = input.read(buffer)) >= 0;) digest.update(buffer, 0, read);
            }
            StringBuilder output = new StringBuilder(64);
            for (byte item : digest.digest()) output.append(String.format("%02x", item));
            return output.toString();
        }
        catch (NoSuchAlgorithmException error) { throw new IllegalStateException("当前JVM不支持SHA-256", error); }
    }

    private record MediaDescriptor(String kind, String extension) { }
}
