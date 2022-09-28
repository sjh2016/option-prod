package com.waben.option.controller.user;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.mode.request.ClientGoogleLoginRequest;
import com.waben.option.mode.request.ClientLoginRequest;
import com.waben.option.mode.vo.UserVO;
import com.waben.option.service.user.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@Api(tags = {"登录"})
@Validated
@RestController
@RequestMapping("/")
public class LoginController extends AbstractBaseController {

    @Resource
    private LoginService loginService;

    @ApiOperation(value = "登录", response = UserVO.class)
    @Validated
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody @Valid ClientLoginRequest request) {
    	log.info("client login:" + request);
        return ok(loginService.login(request.getUsername(), request.getPassword(), getUserIp(), request.getAreaCode(), request.getAuthorityType(), request.getPlatform()));
    }

    @ApiOperation("登出")
    @GetMapping("/user/logout")
    public ResponseEntity<?> logout() {
        loginService.logout(getCurrentUserId());
        return ok();
    }

    @ApiOperation("获取系统时间")
    @RequestMapping(value = "/system/time", method = RequestMethod.GET)
    public ResponseEntity<?> systemTime() {
        return ok(System.currentTimeMillis());
    }


    @RequestMapping(value = "/google/login", method = RequestMethod.POST)
    public ResponseEntity<?> googleLogin(@RequestBody ClientGoogleLoginRequest request) {
        return ok(loginService.googleLogin(request.getToken(), request.getSource(), getUserIp()));
    }


    //{"aud":"*******-*******fica2daig6o2j.apps.googleusercontent.com",
// "azp":"*******-*******6l5832penvmjsf6rrc.apps.googleusercontent.com",
// "email":"*******@gmail.com",
// "email_verified":true,
// "exp":16*******575,"iat":161*******4975,"iss":"https://accounts.google.com",
// "sub":"113*******824269","name":"***",
// "picture":"https://l*******w/s96-c/photo.jpg",
// "given_name":"*","family_name":"*","locale":"zh-CN"}
//        logger.info("google payload = {}", JsonUtils.toJson(payload));
// Use or store profile information

        /*GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance()).setAudience(Collections.singletonList("846656246262-6osk43dfdnst2o92iom3eklfgp8vs5p4.apps.googleusercontent.com")).build();
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(token);
        } catch (Exception e) {
            System.out.println("验证时出现IOException异常");
        }
        if (idToken != null) {
            System.out.println("验证成功.");
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
			System.out.println("User ID: " + userId);
			String email = payload.getEmail();
			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			String locale = (String) payload.get("locale");
			String familyName = (String) payload.get("family_name");
			String givenName = (String) payload.get("given_name");
        } else {
            System.out.println("Invalid ID token.");
        }*/
}
