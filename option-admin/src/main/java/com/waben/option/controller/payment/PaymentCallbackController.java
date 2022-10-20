package com.waben.option.controller.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.interfaces.thirdparty.PaymentAPI;
import com.waben.option.common.interfaces.user.UserBurseAPI;
import com.waben.option.common.interfacesadmin.user.AdminPaymentOrderAPI;
import com.waben.option.common.model.dto.user.UserBurseDTO;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.common.web.controller.AbstractBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 支付通道回调接口
 */
@ApiIgnore
@Slf4j
@RestController
@RequestMapping("/payment_callback")
public class PaymentCallbackController extends AbstractBaseController {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private PaymentAPI paymentAPI;

    @Resource
    private AdminPaymentOrderAPI adminPaymentOrderAPI;

    @Resource
    private UserBurseAPI userBurseAPI;

    private Map<String, String> parameterToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    private Map<String, String> parameterOneToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = requestParams.get(name);
            String valueStr = values[0];
            params.put(name, valueStr);
        }
        return params;
    }

    private String bodyToString(HttpServletRequest request) {
        BufferedReader reader = null;
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = request.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            String s = "";
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /*************************** GYIALS PAY START ***********************/

    @PostMapping("/gyials/payCallback")
    public void gyialsPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("gyials brapix pay callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            String orderNo = data.get("mer_order_no");
            log.info("gyials pay callback data: " + (data != null ? data : "null"));
            String backData = paymentAPI.payCallback(Boolean.FALSE, orderNo, 3L, data);
            log.info("gyials pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/gyials/withdrawCallback")
    public void gyialsWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("gyials withdraw callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("gyials withdraw callback data: " + JacksonUtil.encode(data));
            String backData = paymentAPI.withdrawCallback(data.get("mer_order_no"), data);
            log.info("gyials withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/flashpay/payCallback")
    public void flashpayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("flashpay callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            String orderNo = data.get("merchantOrderNo");
            log.info("flashpay callback data: " + (data != null ? data : "null"));
            String backData = paymentAPI.payCallback(Boolean.FALSE, orderNo, 28l, data);
            log.info("flashpay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/flashpay/withdrawCallback")
    public void flashpayWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("flashpay withdraw callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("flashpay withdraw callback data: " + JacksonUtil.encode(data));
            String backData = paymentAPI.withdrawCallback(data.get("merchantOrderNo"), data);
            log.info("flashpay withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*************************** GYIALS PAY END ***********************/

    /*************************** INTF PAY START ***********************/

    @PostMapping("/intf/payCallback")
    public void intfPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("intf pay callback......");
        try {
            String data = bodyToString(request);
            TreeMap<String, String> dataMap = objectMapper.readValue(data,
                    new TypeReference<TreeMap<String, String>>() {
                    });
            String orderNo = dataMap.get("trade_no");
            log.info("intf pay callback data: " + (data != null ? data : "null"));
            String backData = paymentAPI.payCallback(Boolean.FALSE, orderNo, 2L, dataMap);
            log.info("intf pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/intf/withdrawCallback")
    public void intfWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("intf withdraw callback......");
        try {
            String data = bodyToString(request);
            TreeMap<String, String> dataMap = objectMapper.readValue(data,
                    new TypeReference<TreeMap<String, String>>() {
                    });
            String orderNo = dataMap.get("trade_sn");
            log.info("intf withdraw callback data: " + (data != null ? data : "null"));
            String backData = paymentAPI.withdrawCallback(orderNo, dataMap);
            log.info("intf withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*************************** INTF PAY END ***********************/

    /*************************** SSKKING PAY START ***********************/

    @PostMapping("/sskking/payCallback")
    public void sskkingPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("sskking pay callback......");
        try {
            Map<String, String> data = parameterOneToMap(request);
            String orderNo = data.get("mchOrderNo");
            log.info("sskking pay callback data: " + JacksonUtil.encode(data));
            String backData = paymentAPI.payCallback(Boolean.FALSE, orderNo, 5L, data);
            log.info("sskking pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/sskking/withdrawCallback")
    public void sskkingWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("sskking ste withdraw callback......");
        try {
            Map<String, String> data = parameterOneToMap(request);
            String orderNo = data.get("merTransferId");
            log.info("sskking withdraw callback data: " + JacksonUtil.encode(data));
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("sskking withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*************************** SSKKING PAY END ***********************/

    @PostMapping("/upay/payCallback")
    public void upayPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("upay pay callback......");
        try {
            String body = bodyToString(request);
            log.info("upay pay callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("order_no").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("upay pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/upay/withdrawCallback")
    public void upayWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("upay withdraw callback......");
        try {
            String body = bodyToString(request);
            log.info("upay withdraw callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("order_no").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("upay withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/qepay/payCallback")
    public void qepayPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("qepay pay callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("qepay pay callback data: " + (data != null ? data : "null"));
            String orderNo = data.get("mchOrderNo");
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("qepay pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/qepay/withdrawCallback")
    public void qepayWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("qepay withdraw callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("qepay withdraw callback data: " + (data != null ? data : "null"));
            String orderNo = data.get("merTransferId");
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("qepay withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/gmspay/payCallback")
    public void gmspayPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("gmspay pay callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("gmspay pay callback data: " + (data != null ? data : "null"));
            String orderNo = data.get("mchOrderNo");
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("gmspay pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/gmspay/withdrawCallback")
    public void gmspayWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("gmspay withdraw callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("gmspay withdraw callback data: " + (data != null ? data : "null"));
            String orderNo = data.get("merTransferId");
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("gmspay withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/safepay/payCallback")
    public void safepayPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("safepay pay callback......");
        try {
            String body = bodyToString(request);
            log.info("safepay pay callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("order_no").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("safepay pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/safepay/withdrawCallback")
    public void safepayWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("safepay withdraw callback......");
        try {
            String body = bodyToString(request);
            log.info("safepay withdraw callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("order_no").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("safepay withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/wepay/payCallback")
    public void wepayPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("wepay pay callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("wepay pay callback data: " + (data != null ? data : "null"));
            String orderNo = data.get("mchOrderNo");
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("wepay pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/wepay/add/payCallback")
    public void wepayAddPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("wepay add pay callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("wepay add pay callback data: " + (data != null ? data : "null"));
           String orderNo = data.get("mchOrderNo");
           String backData = paymentAPI.payCallback(false, orderNo, null, data);

            log.info("wepay  add pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/wepay/withdrawCallback")
    public void wepayWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("wepay withdraw callback......");
        try {
            Map<String, String> data = parameterToMap(request);
            log.info("wepay withdraw callback data: " + (data != null ? data : "null"));
            String orderNo = data.get("merTransferId");
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("wepay withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/toppay/payCallback")
    public void toppayPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("toppay pay callback......");
        try {
            Map<String, String> map = parameterToMap(request);
            log.info("toppay pay callback data: " + (map != null ? map : "null"));
            String orderNo = map.get("orderNum");
            Map<String, String> data = new HashMap<>();
            data.put("body", JacksonUtil.encode(map));
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("toppay pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/toppay/withdrawCallback")
    public void toppayWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("toppay withdraw callback......");
        try {
            Map<String, String> map = parameterToMap(request);
            log.info("toppay withdraw callback data: " + (map != null ? map : "null"));
            String orderNo = map.get("orderNum");
            Map<String, String> data = new HashMap<>();
            data.put("body", JacksonUtil.encode(map));
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("toppay withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/shineupay/payCallback")
    public void shineupayPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("shineupay pay callback......");
        try {
            String body = bodyToString(request);
            log.info("shineupay pay callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("body").get("orderId").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            data.put("sign", request.getHeader("Api-Sign"));
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("shineupay pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/shineupay/withdrawCallback")
    public void shineupayWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("shineupay withdraw callback......");
        try {
            String body = bodyToString(request);
            log.info("shineupay withdraw callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("body").get("orderId").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            data.put("sign", request.getHeader("Api-Sign"));
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("shineupay withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/wouzs/payCallback")
    public void wouzsPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("wouzs pay callback......");
        try {
            Map<String, String> map = parameterToMap(request);
            log.info("wouzs pay callback data: " + (map != null ? map : "null"));
            String orderNo = map.get("mer_order_no");
            Map<String, String> data = new HashMap<>();
            data.put("body", JacksonUtil.encode(map));
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("wouzs pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/wouzs/withdrawCallback")
    public void wouzsWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("wouzs withdraw callback......");
        try {
            Map<String, String> map = parameterToMap(request);
            log.info("wouzs withdraw callback data: " + (map != null ? map : "null"));
            String orderNo = map.get("mer_order_no");
            Map<String, String> data = new HashMap<>();
            data.put("body", JacksonUtil.encode(map));
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("wouzs withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/hsusdt/payCallback")
    public void hsusdtPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("hsusdt pay callback......");
        try {
            String body = bodyToString(request);
            log.info("hsusdt pay callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("client_order_id").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            String backData = paymentAPI.payCallback(false, orderNo, null, data);
            log.info("hsusdt pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/hsusdt/withdrawCallback")
    public void hsusdtWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("hsusdt withdraw callback......");
        try {
            String body = bodyToString(request);
            log.info("hsusdt withdraw callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("client_order_id").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("hsusdt withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/hsusdtBurse/payCallback")
    public void hsusdtBursePayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("hsusdtBurse pay callback......");
        try {
            String body = bodyToString(request);
            log.info("hsusdtBurse pay callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String address = jsonNode.get("address").asText();
            UserBurseDTO burse = userBurseAPI.queryByAddress(address);
            String backData = null;
            if (burse == null) {
                backData = "{\"code\":-1,\"message\":\"address not found\"}";
            } else {
                Map<String, String> data = new HashMap<>();
                data.put("body", body);
                backData = paymentAPI.payCallback(false, null, burse.getPayApiId(), data);
            }
            log.info("hsusdtBurse pay callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/hsusdtBurse/withdrawCallback")
    public void hsusdtBurseWithdrawCallback(HttpServletRequest request, HttpServletResponse response) {
        log.info("hsusdtBurse withdraw callback......");
        try {
            String body = bodyToString(request);
            log.info("hsusdtBurse withdraw callback data: " + (body != null ? body : "null"));
            JsonNode jsonNode = JacksonUtil.decodeToNode(body);
            String orderNo = jsonNode.get("client_order_id").asText();
            Map<String, String> data = new HashMap<>();
            data.put("body", body);
            String backData = paymentAPI.withdrawCallback(orderNo, data);
            log.info("hsusdtBurse withdraw callback return to up: " + backData);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(backData != null ? backData : "");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
