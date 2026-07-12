package com.hrm.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /** Upload anh len Cloudinary, tra ve secure URL. */
    public String uploadAnh(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File anh khong duoc de trong");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phai la anh");
        }
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "hrm/avatars"));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new IllegalStateException("Loi upload anh: " + e.getMessage());
        }
    }
}
