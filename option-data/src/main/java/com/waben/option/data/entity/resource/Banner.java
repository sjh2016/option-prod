package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.data.entity.BaseEntity;
import com.waben.option.data.handler.resource.ListBannerEntityTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_banner", autoResultMap = true)
public class Banner extends BaseEntity<Long> {

    private Integer seq;

    private Boolean enable;

    private Long operatorId;

    private Integer displayType;

    @TableField(typeHandler = ListBannerEntityTypeHandler.class)
    private List<BannerDTO.BannerLanguageDTO> languageList;

    public static final String DISPLAY_TYPE = "display_type";

    public static final String OPERATOR_ID = "operator_id";

    public static final String ENABLE = "enable";

    public static final String SEQ = "seq";

}
