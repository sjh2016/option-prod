package com.waben.option.core.controller;

import com.waben.option.common.model.enums.OrderStatusEnum;
import com.waben.option.common.model.request.order.OrderRequest;
import com.waben.option.common.model.request.order.UpdateOrderRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.order.OrderService;
import com.waben.option.core.service.settlement.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author: Peter
 * @date: 2021/6/23 16:43
 */
@RestController
@RequestMapping("/order")
public class OrderController extends AbstractBaseController {

    @Resource
    private OrderService orderService;

    @Resource
    private SettlementService settlementService;

    @Resource
    private ThreadPoolTaskExecutor executorBeanName;

    @RequestMapping(value = "/place", method = RequestMethod.POST)
    public ResponseEntity<?> place(@RequestBody OrderRequest request) {
        return ok(orderService.place(request));
    }

    @RequestMapping(value = "/receiveGiveOrder", method = RequestMethod.POST)
    public ResponseEntity<?> receiveGiveOrder(@RequestParam("userId") Long userId) {
        orderService.receiveGiveOrder(userId);
        return ok();
    }

    @RequestMapping(value = "/placeRegister", method = RequestMethod.POST)
    public ResponseEntity<?> placeRegister(@RequestBody OrderRequest request) {
        orderService.placeRegister(request);
        return ok();
    }

    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public ResponseEntity<?> auditOrder(@RequestBody UpdateOrderRequest request) {
        orderService.auditOrder(request);
        return ok();
    }

    @RequestMapping(value = "/queryOrderTotalByUserId", method = RequestMethod.GET)
    public ResponseEntity<?> queryOrderTotalByUserId(@RequestParam(value = "userId", required = false) Long userId) {
        return ok(orderService.queryOrderTotalByUserId(userId));
    }

    @RequestMapping(value = "/queryPage", method = RequestMethod.GET)
    public ResponseEntity<?> queryPage(@RequestParam(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "status", required = false) OrderStatusEnum status, @RequestParam("page") int page,
                                       @RequestParam("size") int size,@RequestParam(value="topId",required = false) String topId) {
        return ok(orderService.queryPage(userId, status, page, size,topId));
    }

    @RequestMapping(value = "/queryPage/first", method = RequestMethod.GET)
    public ResponseEntity<?> queryPageFirst(@RequestParam(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "status", required = false) OrderStatusEnum status, @RequestParam("page") int page,
                                       @RequestParam("size") int size,@RequestParam(value="topId",required = false) String topId) {
        return ok(orderService.queryPageFirst(userId, status, page, size,topId));
    }

    @RequestMapping(value = "/user/sta", method = RequestMethod.GET)
    public ResponseEntity<?> userSta(@RequestParam("userId") Long userId) throws ExecutionException {
        return ok(orderService.userStaByCache(userId));
    }

    @RequestMapping(value = "/orderSettlement", method = RequestMethod.GET)
    public ResponseEntity<?> orderSettlement(@RequestParam("count") Integer count) {
        orderService.orderSettlement(count);
        return ok();
    }

    @RequestMapping(value = "/settlement", method = RequestMethod.GET)
    public ResponseEntity<?> settlement() {
        executorBeanName.execute(new Runnable() {
            @Override
            public void run() {
                settlementService.settlement();
            }
        });
        return ok();
    }

    @RequestMapping(value = "/queryOrderCount", method = RequestMethod.GET)
    public ResponseEntity<?> queryOrderCount() {
        return ok(orderService.queryOrderCount());
    }

    @RequestMapping(value = "/userPlaceCount", method = RequestMethod.GET)
    public ResponseEntity<?> userCount(Long userId) {
        return ok(orderService.userPlaceCount(userId));
    }

}
