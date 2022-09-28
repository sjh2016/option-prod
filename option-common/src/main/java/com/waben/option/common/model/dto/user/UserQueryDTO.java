package com.waben.option.common.model.dto.user;

import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Data
public class UserQueryDTO {

    private Long userId;

    private int level;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserQueryDTO that = (UserQueryDTO) o;

        return new EqualsBuilder().append(level, that.level).append(userId, that.userId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(userId).append(level).toHashCode();
    }
}
