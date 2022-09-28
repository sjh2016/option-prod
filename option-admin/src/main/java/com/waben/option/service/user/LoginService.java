package com.waben.option.service.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfacesadmin.user.AdminUserAPI;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.PlatformEnum;
import com.waben.option.common.model.enums.RegisterEnum;
import com.waben.option.common.model.request.user.RegisterUserRequest;
import com.waben.option.common.service.JwtService;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.mode.vo.UserVO;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Service
public class LoginService {

    @Resource
    private AdminUserAPI adminUserAPI;

    @Resource
    private JwtService jwtService;

    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    private ModelMapper modelMapper;

    @Value("${google.key:846656246262-6osk43dfdnst2o92iom3eklfgp8vs5p4.apps.googleusercontent.com}")
    private String googleKey;

    @Value("${google.face.appId:917431922176927}")
    private String faceAppIdKey;

    @Value("${google.face.appSecret:39d8beb7e9453728707c886236741e29}")
    private String faceAppSecretKey;

    private final static String USER_URL = "https://graph.facebook.com/me";
    private final static String FIELDS = "id,name,birthday,gender,hometown,email,devices";

    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;

    public Map<String, Object> login(String username, String password, String ip, String code, AuthorityEnum authorityType, PlatformEnum platform) {
        if (username.startsWith("0")) username = username.substring(1);
        UserDTO user = adminUserAPI.login(username, password, ip, code);
        if (authorityType != user.getAuthorityType()) throw new ServerException(1016);
        String newToken = jwtService.sign(platform.name(), user.getId(), authorityType.name());
        adminUserAPI.cleanToken(user.getId());
        adminUserAPI.storeToken(user.getId(), newToken);
        UserVO userVO = modelMapper.map(user, UserVO.class);
        Map<String, Object> map = new HashMap<>();
        map.put("token", newToken);
        map.put("user", userVO);
        return map;
    }

    public void logout(Long userId) {
        adminUserAPI.logout(userId);
    }


//    @ShardingTransactionType(value = TransactionType.XA)
//    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> googleLogin(String token, Integer source, String userIp) {
        try {
            String username = null;
            String password = null;
            switch (source) {
                case 2:
                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                            JacksonFactory.getDefaultInstance()).setAudience(Collections.singletonList(googleKey)).build();
                    GoogleIdToken idToken = verifier.verify(token);
                    GoogleIdToken.Payload payload = idToken.getPayload();
                    log.info("GoogleIdToken.Payload|{}", payload);
                    username = payload.getEmail();
                    password = payload.getSubject();
                    break;
                case 3:
                    Map<String, String> baseMap = buildFaceBook(token);
                    username = baseMap.get("username");
                    password = baseMap.get("password");
                    break;
            }
            if (!adminUserAPI.verifyUsername(username)) adminUserAPI.register(buildRegister(username, password, source, userIp));
            return this.login(username, password, userIp, null, AuthorityEnum.CLIENT, PlatformEnum.H5);
        } catch (Exception e) {
            log.error("googleLogin|{}", e.getMessage());
        }
        return null;
    }

    private RegisterUserRequest buildRegister(String username, String password, Integer source, String userIp) {
        RegisterUserRequest clientRequest = new RegisterUserRequest();
        clientRequest.setUsername(username);
        clientRequest.setPassword(password);
        clientRequest.setRegisterType(RegisterEnum.EMAIL);
        clientRequest.setAuthorityType(AuthorityEnum.CLIENT);
        clientRequest.setPlatform(PlatformEnum.H5);
        clientRequest.setIp(userIp);
        clientRequest.setSource(source);
        return clientRequest;
    }

    private Map<String, String> buildFaceBook(String token) {
        try {
            String url = String.format("https://graph.facebook.com/debug_token?access_token=%s&input_token=%s", faceAppIdKey + "%7C" + faceAppSecretKey, token);
            Request postRequest = new Request.Builder()
                    .url(url).build();
            Response urlResponse = okHttpClient.newCall(postRequest).execute();
            if (urlResponse.isSuccessful()) {
                String json = urlResponse.body().string();
                log.info("graph.facebook.com.debug_token|{}", json);
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                JsonNode node = jsonNode.get("data");
                if (node != null) {
                    return getStringMap(token, node.get("is_valid").asBoolean());
                }
            }
        } catch (Exception e) {
            log.error("buildFaceBook|{}", e.getMessage());
        }
        return new HashMap<>();
    }

    private Map<String, String> getStringMap(String token, boolean is_valid) throws IOException {
        Map<String, String> baseMap = new HashMap<>();
        if (is_valid) {
            HttpUrl.Builder urlBuild = HttpUrl.parse(USER_URL).newBuilder().addQueryParameter("access_token", token).addQueryParameter("fields", FIELDS);
            Request request = new Request.Builder().url(urlBuild.build()).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String userJson = response.body().string();
                log.info("graph.facebook.com.me|{}", userJson);
                JsonNode userNode = JacksonUtil.decodeToNode(userJson);
                if (userNode != null) {
                    baseMap.put("username", userNode.get("email").asText());
                    baseMap.put("password", userNode.get("id").asText());
                    return baseMap;
                }
            }
        }
        return baseMap;
    }
}
