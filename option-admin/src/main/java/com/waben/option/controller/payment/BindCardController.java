package com.waben.option.controller.payment;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.user.AdminBindCardAPI;
import com.waben.option.common.model.dto.payment.BindCardDTO;
import com.waben.option.common.web.controller.AbstractBaseController;

@RestController
@RequestMapping("/bind_card")
public class BindCardController extends AbstractBaseController {

	@Resource
	private AdminBindCardAPI adminBindCardAPI;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		return ok(adminBindCardAPI.list(getCurrentUserId()));
	}

	@RequestMapping(value = "/list/user", method = RequestMethod.GET)
	public ResponseEntity<?> listUser(Long userId) {
		return ok(adminBindCardAPI.list(userId));
	}


	@RequestMapping(value = "/bind", method = RequestMethod.POST)
	public ResponseEntity<?> bind(@RequestBody BindCardDTO request) {
		request.setUserId(getCurrentUserId());
		adminBindCardAPI.bind(request);
		return ok();
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> update(@RequestBody BindCardDTO request) {
		request.setUserId(getCurrentUserId());
		adminBindCardAPI.update(request);
		return ok();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> delete(@RequestBody BindCardDTO request) {
		adminBindCardAPI.delete(request.getId());
		return ok();
	}

}
