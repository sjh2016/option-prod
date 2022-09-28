package com.waben.option.controller.resource;

import com.waben.option.common.model.request.resource.PaymentImageRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.PaymentImageService;
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
    public ResponseEntity<?> queryList(@RequestParam(value = "day", required = false) LocalDate day) {
        return ok(paymentImageService.queryList(day));
    }

    @RequestMapping(value = "/createToUpset", method = RequestMethod.POST)
    public ResponseEntity<?> createBanner(@RequestBody PaymentImageRequest request) {
        paymentImageService.createToUpset(request);
        return ok();
    }

    @RequestMapping(value = "/clientQuery", method = RequestMethod.GET)
    public ResponseEntity<?> clientQuery() {
        return ok(paymentImageService.clientQuery());
    }

}
