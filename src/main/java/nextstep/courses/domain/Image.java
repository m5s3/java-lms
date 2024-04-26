package nextstep.courses.domain;

import java.util.Arrays;
import java.util.List;

public class Image {

    private final String imageUrl;
    private final ImageMeta imageMeta;

    public Image(String imageUrl, int width, int height, int fileSize, String ext) {
        this(imageUrl, new ImageMeta(width, height, fileSize, ext));
    }

    public Image(String imageUrl, int width, int height, int fileSize) {
        this(imageUrl, new ImageMeta(width, height, fileSize, extractExtension(imageUrl)));
    }

    public Image(String imageUrl, ImageMeta imageMeta) {
        this.imageUrl = imageUrl;
        this.imageMeta = imageMeta;
    }

    private static String extractExtension(String text) {
        List<String> strings = Arrays.asList(text.split("\\."));
        return strings.get(strings.size() - 1);
    }
}

