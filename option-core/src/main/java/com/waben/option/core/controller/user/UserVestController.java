package com.waben.option.core.controller.user;

import com.waben.option.common.model.dto.user.UserVestDTO;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.user.UserVestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/vest")
public class UserVestController extends AbstractBaseController {

	@Resource
	private UserVestService service;

	@RequestMapping(method = RequestMethod.GET, value = "/query")
	public ResponseEntity<?> query(@RequestParam("userIds") List<Long> userIds) {
		return ok(service.query(userIds));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/bind")
	public ResponseEntity<?> bind(@RequestBody UserVestDTO req) {
		service.bind(req);
		return ok();
	}

}
