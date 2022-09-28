package com.waben.option.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtil {

	public static final String GZIP_ENCODE_UTF_8 = "UTF-8"; 
    public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";

    public static byte[] compress(String str, String encoding) throws IOException {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
        } finally {
        	out.close();
        	if(gzip != null) {
        		gzip.close();
        	}
        }
        return out.toByteArray();
    }
    
    public static byte[] compress(String str) throws IOException {  
        return compress(str, GZIP_ENCODE_UTF_8);  
    }
    
    public static byte[] uncompress(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } finally {
        	in.close();
        	out.close();
		}
        return out.toByteArray();
    }
    
    public static String uncompressToString(byte[] bytes, String encoding) throws IOException {
        if (bytes == null || bytes.length == 0) {  
            return null;  
        }  
        return new String(uncompress(bytes), encoding);
    }
    
    public static String uncompressToString(byte[] bytes) throws IOException {
        return uncompressToString(bytes, GZIP_ENCODE_UTF_8);  
    } 
	
}
