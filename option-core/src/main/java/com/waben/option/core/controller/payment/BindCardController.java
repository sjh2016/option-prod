package com.waben.option.core.controller.payment;

import com.waben.option.common.model.dto.payment.BindCardDTO;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.payment.BindCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/bindcard")
public class BindCardController extends AbstractBaseController {

	@Resource
	private BindCardService bindCardService;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query(@RequestParam(value = "userId") Long userId, @RequestParam(value = "id") Long id) {
		return ok(bindCardService.query(userId, id));
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list(@RequestParam(value = "userId") Long userId) {
		return ok(bindCardService.list(userId));
	}

	@RequestMapping(value = "/bind", method = RequestMethod.POST)
	public ResponseEntity<?> bind(@RequestBody BindCardDTO request) {
		bindCardService.bind(request);
		return ok();
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> update(@RequestBody BindCardDTO request) {
		bindCardService.update(request);
		return ok();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> delete(@RequestParam(value = "userId") Long userId, @RequestParam("id") Long id) {
		bindCardService.delete(userId, id);
		return ok();
	}

}
