package ru.alemakave.xuitelegrambot.utils;

import ru.alemakave.qr.ImageType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static byte[] toByteArray(BufferedImage bi, ImageType format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format.name(), baos);
        return baos.toByteArray();

    }
}
