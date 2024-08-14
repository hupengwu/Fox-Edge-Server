/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.jar.info;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class JarInfoEntity {
    private JarInfoItem properties = new JarInfoItem();
    private List<JarInfoItem> dependencies = new ArrayList<>();
    private List<String> directoryName = new ArrayList<>();
    private List<String> classFileName = new ArrayList<>();
}
