package com.waben.option.core.controller.resource;

import com.waben.option.common.model.request.resource.PaymentImageRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.PaymentImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
@RequestMapping("/paymentImage")
public class PaymentImageController extends AbstractBaseController {

    @Resource
    private PaymentImageService paymentImageService;

    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "day", required = false)LocalDate day) {
        return ok(paymentImageService.queryList(day));
    }

    @RequestMapping(value = "/createToUpset", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody PaymentImageRequest request) {
        paymentImageService.createToUpset(request);
        return ok();
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> query() {
        return ok(paymentImageService.query());
    }

}
