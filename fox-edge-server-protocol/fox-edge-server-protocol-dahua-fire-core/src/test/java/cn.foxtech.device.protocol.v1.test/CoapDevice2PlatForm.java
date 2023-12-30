package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.CoapPduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.InfObjRegisterEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.CmdType;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.AddressUtil;

public class CoapDevice2PlatForm {
    public static void main(String[] args) {
        testRegister();
    }

    public static void testRegister() {
        byte[] data = new byte[0];

        // 设备请求
        CoapPduEntity device = new CoapPduEntity();
        device.setSn(8);
        device.getCtrlEntity().setCmd(CmdType.register.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.register);
        device.getAduEntity().getInfObjEntities().add(new InfObjRegisterEntity());
        data = CoapPduEntity.encodeEntity(device);

        // 自我验证
        device = CoapPduEntity.decodeEntity(data);

        // 平台应答
        CoapPduEntity platform = new CoapPduEntity();
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        data = CoapPduEntity.encodeEntity(platform);

        // 自我验证
        platform = CoapPduEntity.decodeEntity(data);

    }
}
