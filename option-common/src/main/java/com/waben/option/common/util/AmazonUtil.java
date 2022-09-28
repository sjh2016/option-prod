package com.waben.option.common.util;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AmazonUtil {

    private final static String accessKey = "AKIAZM34MSAIFCWN63MX";
    private final static String secretKey = "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";
    private final static String SOURCE_DIRECTORY = "x001singapore";

    public static void main(String[] args) {
        String s = "aaaa.jpg";
        String ss = s.substring(0,s.indexOf("."));
        String sss = s.substring(ss.length() +1,s.length());
        System.out.println(sss);
    }

    /**
     * 上传文件
     *
     * @param tempFile 文件流
     * @param folder   上传目录
     * @return
     */
    public static URL uploadS3(File tempFile, String folder) {
        AmazonS3 s3 = getAmazonS3();
        String bucketName = SOURCE_DIRECTORY;
        String key = tempFile.getName();
        String keyName = key.substring(0,key.indexOf("."));
        String keyValue = key.substring(keyName.length() + 1,key.length());
        String fileName = String.valueOf(new Date().getTime()) + "." + keyValue;
        try {
            // 校验存储筒是否存在，不存在则创建
            if (!checkBucketExists(s3, bucketName)) {
                s3.createBucket(bucketName);
            }
            String bucketFolder = bucketName + "/" + folder;
            s3.putObject(new PutObjectRequest(bucketFolder, fileName, tempFile).withCannedAcl(CannedAccessControlList.PublicRead));
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketFolder, fileName);
            URL url = s3.generatePresignedUrl(urlRequest);
            if (url == null) {
                // 抛异常 "can't get s3 file url!"
            }
            return url;
        } catch (Exception ace) {
            ace.printStackTrace();
        }
        return null;
    }

    public static URL getAmazonS3Token(String objectKey) {
        try {
            String bucketName = SOURCE_DIRECTORY;
            AmazonS3 s3Client = getAmazonS3();
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(getExpirationTime());
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证s3存储筒是否存在
     *
     * @param s3
     * @param bucketName
     * @return
     */
    public static boolean checkBucketExists(AmazonS3 s3, String bucketName) {
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket bucket : buckets) {
            if (Objects.equals(bucket.getName(), bucketName)) {
                return true;
            }
        }
        return false;
    }


    public static AmazonS3 getAmazonS3() {
        BasicAWSCredentials basicAwsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAwsCredentials))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();
        return s3;
    }

    private static Date getExpirationTime() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
