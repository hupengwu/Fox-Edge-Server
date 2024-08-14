/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.redis;

/**
 * 查找器
 */
public interface IBaseFinder {
    /**
     * 找到目标对象，并直接元素的内容
     *
     * @param value 元素
     * @return 是否修改成功
     */
    boolean compareValue(Object value);
}
