package com.waben.option.controller.payment;

import com.waben.option.common.interfaces.thirdparty.BindCardAPI;
import com.waben.option.common.model.dto.payment.BindCardDTO;
import com.waben.option.common.web.controller.AbstractBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/bind_card")
public class BindCardController extends AbstractBaseController {

	@Resource
	private BindCardAPI bindCardService;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query(@RequestParam("id") Long id) {
		return ok(bindCardService.query(getCurrentUserId(), id));
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		return ok(bindCardService.list(getCurrentUserId()));
	}

	@RequestMapping(value = "/bind", method = RequestMethod.POST)
	public ResponseEntity<?> bind(@RequestBody BindCardDTO request) {
		request.setUserId(getCurrentUserId());
		bindCardService.bind(request);
		return ok();
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> update(@RequestBody BindCardDTO request) {
		request.setUserId(getCurrentUserId());
		bindCardService.update(request);
		return ok();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> delete(@RequestBody BindCardDTO request) {
		bindCardService.delete(getCurrentUserId(), request.getId());
		return ok();
	}

}
