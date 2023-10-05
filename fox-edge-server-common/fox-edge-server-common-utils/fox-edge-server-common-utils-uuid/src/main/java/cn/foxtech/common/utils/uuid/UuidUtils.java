package cn.foxtech.common.utils.uuid;

import java.util.UUID;

public class UuidUtils {
    static public String randomUUID(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
