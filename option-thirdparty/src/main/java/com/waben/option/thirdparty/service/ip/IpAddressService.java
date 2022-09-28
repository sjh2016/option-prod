package com.waben.option.thirdparty.service.ip;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import com.waben.option.common.model.dto.ip.IpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Peter
 * @date: 2021/6/1 13:56
 */
@Slf4j
@Service
public class IpAddressService {
	
    public List<IpDTO> getAddressByIp(String ipStr) {
        List<IpDTO> stringList = new ArrayList<>();
        String[] ips = ipStr.split(",");
        for (String ip : ips) {
            IpDTO vo = new IpDTO();
            if (ip.contains(":")) {
                vo.setAddress(this.getIp(ip));
            } else {
                vo.setAddress(this.get(ip));
            }
            vo.setIp(ip);
            stringList.add(vo);
        }
        return stringList;
    }

    private int[] prefStart = new int[256];
    private int[] prefEnd = new int[256];
    private long[] endArr;
    private String[] addrArr;

    private String getIp(String ip) {
        try {
            ClassPathResource classPathResource = new ClassPathResource("ip/GeoLite2-City.mmdb");
            // 读取数据库内容
            DatabaseReader reader = new DatabaseReader.Builder(classPathResource.getInputStream()).build();
            InetAddress ipAddress = InetAddress.getByName(ip);
            // 获取查询结果
            CityResponse response = reader.city(ipAddress);
            // 获取国家信息
            Country country = response.getCountry();
            String countryStr = country.getNames().get("zh-CN"); // '中国'
            // 获取省份
            Subdivision subdivision = response.getMostSpecificSubdivision();
            String subdivisionStr = subdivision.getNames().get("zh-CN"); // '广西壮族自治区'
            // 获取城市
            City city = response.getCity();
            String cityStr = city.getNames().get("zh-CN"); // '南宁'
            StringBuffer sb = new StringBuffer();
            if (!StringUtils.isEmpty(countryStr)) sb.append(countryStr);
            if (!StringUtils.isEmpty(subdivisionStr)) sb.append(subdivisionStr);
            if (!StringUtils.isEmpty(cityStr)) sb.append(cityStr);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String get(String ip) {
        String[] ips = ip.split("\\.");
        int pref = Integer.valueOf(ips[0]);
        long val = ipToLong(ip);
        int low = prefStart[pref], high = prefEnd[pref];
        long cur = low == high ? low : BinarySearch(low, high, val);
        String[] results = addrArr[(int) cur].split("\\|");
        StringBuffer sb = new StringBuffer();
        for (String s : results) {
            if (StringUtils.isEmpty(s) || !isChinese(s)) {
                continue;
            }
            sb.append(s.trim());
        }
        return sb.toString();
    }

    private IpAddressService() {
        byte[] data = null;
        try (InputStream is = new ClassPathResource("ip/ip.dat").getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buff = new byte[100];
            int rc;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                baos.write(buff, 0, rc);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            log.error("", e);
        }
        for (int k = 0; k < 256; k++) {
            int i = k * 8 + 4;
            prefStart[k] = (int) BytesToLong(data[i], data[i + 1], data[i + 2], data[i + 3]);
            prefEnd[k] = (int) BytesToLong(data[i + 4], data[i + 5], data[i + 6], data[i + 7]);

        }
        int RecordSize = (int) BytesToLong(data[0], data[1], data[2], data[3]);
        endArr = new long[RecordSize];
        addrArr = new String[RecordSize];
        for (int i = 0; i < RecordSize; i++) {
            int p = 2052 + (i * 8);
            long endipnum = BytesToLong(data[p], data[1 + p], data[2 + p], data[3 + p]);
            int offset = (int) BytesToLong3(data[4 + p], data[5 + p], data[6 + p]);
            int length = data[7 + p] & 0xff;
            endArr[i] = endipnum;
            addrArr[i] = new String(Arrays.copyOfRange(data, offset, (offset + length)));
        }
    }

    private int BinarySearch(int low, int high, long k) {
        int M = 0;
        while (low <= high) {
            int mid = (low + high) / 2;
            long endipNum = endArr[mid];
            if (endipNum >= k) {
                M = mid;
                if (mid == 0) {
                    break;
                }
                high = mid - 1;
            } else
                low = mid + 1;
        }
        return M;
    }

    private long BytesToLong(byte a, byte b, byte c, byte d) {
        return (a & 0xFFL) | ((b << 8) & 0xFF00L) | ((c << 16) & 0xFF0000L) | ((d << 24) & 0xFF000000L);
    }

    private long BytesToLong3(byte a, byte b, byte c) {
        return (a & 0xFFL) | ((b << 8) & 0xFF00L) | ((c << 16) & 0xFF0000L);
    }

    public static boolean isChinese(String charaString) {
        return charaString.matches("[\\u4e00-\\u9fa5]+");
    }

    private static long ipToLong(String ip) {
        long result = 0;
        String[] d = ip.split("\\.");
        for (String b : d) {
            result <<= 8;
            result |= Long.parseLong(b) & 0xff;
        }
        return result;
    }
}
