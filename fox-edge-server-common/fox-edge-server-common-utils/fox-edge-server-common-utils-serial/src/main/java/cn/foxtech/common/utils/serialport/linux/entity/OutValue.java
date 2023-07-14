package cn.foxtech.common.utils.serialport.linux.entity;

/**
 * 基础数据输出
 *    在JAVA中，Integer、String、Long、byte[]等基础对象，在java函数中不支持返回
 *    所以建立这么一个对象，用于在函数中带回数据
 */
public final class OutValue {
    private Object obj = null;
    public Object getObj(){
        return this.obj;
    }
    public void setObject(Object obj){
        this.obj = obj;
    }
}
