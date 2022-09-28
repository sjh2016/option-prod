package com.waben.option.core.service.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.model.dto.push.OutsidePushMessageTemplateDTO;
import com.waben.option.common.model.dto.resource.*;
import com.waben.option.common.model.dto.user.UserVestDTO;
import com.waben.option.common.model.enums.OutsidePushMessageType;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.data.entity.resource.Config;
import com.waben.option.data.repository.resource.ConfigDao;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class ConfigService {

    @Resource
    private ConfigDao configDao;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private ObjectMapper objectMapper;

    public ConfigDTO queryConfig(String key) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Config.KEY, key);
        Config config = configDao.selectOne(queryWrapper);
        if (config == null) {
            config = new Config();
        }
        return modelMapper.map(config, ConfigDTO.class);
    }

    /**
     * 获取投资配置
     *
     * @return
     */
    public List<InvestmentDTO> queryInvestment() {
        QueryWrapper<Config> query = new QueryWrapper<>();
        query.eq(Config.KEY, "investment");
        Config config = configDao.selectOne(query);
        if (config != null && config.getValue() != null && config.getValue().trim().startsWith("[")
                && config.getValue().trim().endsWith("]")) {
            try {
                return objectMapper.readValue(config.getValue(), new TypeReference<List<InvestmentDTO>>() {
                });
            } catch (Exception ex) {
                log.error("withdraw config value invalid!");
            }
        }
        return new ArrayList<>();
    }

    /**
     * 获取等级收益
     *
     * @return
     */
    public List<LevelIncomeDTO> queryLevelIncome() {
        QueryWrapper<Config> query = new QueryWrapper<>();
        query.eq(Config.KEY, "levelIncome");
        Config config = configDao.selectOne(query);
        if (config != null && config.getValue() != null && config.getValue().trim().startsWith("[")
                && config.getValue().trim().endsWith("]")) {
            try {
                return objectMapper.readValue(config.getValue(), new TypeReference<List<LevelIncomeDTO>>() {
                });
            } catch (Exception ex) {
                log.error("withdraw config value invalid!");
            }
        }
        return new ArrayList<>();
    }

    /**
     * 根据key获取注册、投资奖励
     *
     * @param key
     * @return
     */
    public Config queryInviteRegister(String key) {
        QueryWrapper<Config> query = new QueryWrapper<>();
        query.eq(Config.KEY, key);
        return configDao.selectOne(query);
    }

    public RechargeConfigDTO queryRechargeConfig() {
        RechargeConfigDTO result = new RechargeConfigDTO();
        // 查询充值选项配置
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Config.KEY, DBConstants.CONFIG_RECHARGE_ITEM_GIVE);
        Config config = configDao.selectOne(queryWrapper);
        if (config != null && config.getValue() != null && config.getValue().trim().startsWith("{")
                && config.getValue().trim().endsWith("}")) {
            result = JacksonUtil.decode(config.getValue().trim(), RechargeConfigDTO.class);
            if (result != null) {
                // 对充值选项进行排序
                if (result.getItemList() != null) {
                    // 排序
                    Collections.sort(result.getItemList(), new Comparator<BigDecimal>() {
                        @Override
                        public int compare(BigDecimal o1, BigDecimal o2) {
                            return o1.compareTo(o2);
                        }
                    });
                }
                // 对充值赠送进行排序
                if (result.getRechargeGiveList() != null) {
                    Collections.sort(result.getRechargeGiveList(), new Comparator<RechargeConfigDTO.RechargeGiveDTO>() {
                        @Override
                        public int compare(RechargeConfigDTO.RechargeGiveDTO o1, RechargeConfigDTO.RechargeGiveDTO o2) {
                            return o1.getAmount().compareTo(o2.getAmount());
                        }
                    });
                }
            } else {
                new RechargeConfigDTO();
            }
        }
        if (result.getItemList() == null) {
            result.setItemList(new ArrayList<>());
        }
        if (result.getRechargeGiveList() == null) {
            result.setRechargeGiveList(new ArrayList<>());
        }
        return result;
    }

    public List<WithdrawConfigDTO> queryWithdrawConfig() {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Config.KEY, DBConstants.CONFIG_WITHDRAW_KEY);
        Config config = configDao.selectOne(queryWrapper);
        if (config != null && config.getValue() != null && config.getValue().trim().startsWith("[")
                && config.getValue().trim().endsWith("]")) {
            try {
                return objectMapper.readValue(config.getValue(), new TypeReference<List<WithdrawConfigDTO>>() {
                });
            } catch (Exception ex) {
                log.error("withdraw config value invalid!");
            }
        }
        return new ArrayList<>();
    }

    public String queryPath() {
        Config config = configDao.selectOne(new QueryWrapper<Config>().eq(Config.KEY, "url.image"));
        return config.getValue();
    }

    public OutsidePushMessageTemplateDTO queryOutsidePushMessageTemplate(OutsidePushMessageType type) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Config.KEY, DBConstants.OUTSIDE_PUSH_MESSAGE_TEMPLATE);
        Config config = configDao.selectOne(queryWrapper);
        if (config != null && config.getValue() != null && !"".equals(config.getValue().trim())) {
            try {
                HashMap<String, OutsidePushMessageTemplateDTO> map = objectMapper.readValue(config.getValue(), new TypeReference<HashMap<String, OutsidePushMessageTemplateDTO>>() {
                });
                if (map != null) {
                    return map.get(type.getKey());
                }
            } catch (Exception ex) {
                log.error(type + "outsidePushMessageTemplate config value invalid!");
            }
        }
        return null;
    }

    public List<UserVestDTO> queryOutsideBroadcastVestList() {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Config.KEY, DBConstants.OUTSIDE_BROADCAST_VEST_LIST);
        Config config = configDao.selectOne(queryWrapper);
        if (config != null && config.getValue() != null && config.getValue().trim().startsWith("[")
                && config.getValue().trim().endsWith("]")) {
            try {
                return objectMapper.readValue(config.getValue(), new TypeReference<List<UserVestDTO>>() {
                });
            } catch (Exception ex) {
                log.error("outsideBroadcastVestList config value invalid!");
            }
        }
        return new ArrayList<>();
    }
    
	public BigDecimal getUsdtRate() {
		QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(Config.KEY, DBConstants.USDT_RATE);
		Config config = configDao.selectOne(queryWrapper);
		if (config != null && !StringUtils.isBlank(config.getValue())) {
			return new BigDecimal(config.getValue().trim());
		} else {
			return BigDecimal.ONE;
		}
	}

}
