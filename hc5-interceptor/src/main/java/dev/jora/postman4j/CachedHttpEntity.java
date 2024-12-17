package dev.jora.postman4j;

import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * @author dyadyaJora on 18.12.2024
 */
public class CachedHttpEntity implements HttpEntity {

    private final byte[] content;

    public CachedHttpEntity(byte[] content) {
        this.content = content;
    }

    @Override
    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void writeTo(java.io.OutputStream outStream) throws IOException {
        outStream.write(content);
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long getContentLength() {
        return content.length;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public String getContentEncoding() {
        return "";
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public Set<String> getTrailerNames() {
        return Set.of();
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public Supplier<List<? extends Header>> getTrailers() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}