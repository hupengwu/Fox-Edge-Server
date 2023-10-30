package cn.foxtech.huawei.iotda.service.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Paras {
    private List<Device> devices = new ArrayList<>();
}
