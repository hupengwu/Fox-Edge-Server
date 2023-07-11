package cn.foxtech.channel.opcua.utils;

import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import java.util.UUID;

public class ValueUtils {
    /**
     * 转换为支持json的数据类型
     *
     * @param dataValue
     * @return
     */
    public static Object buildJsonValue(Object dataValue) {
        if (dataValue == null) {
            return null;
        }

        // java类型
        if (dataValue instanceof String || dataValue instanceof String[]) {
            return dataValue;
        }
        if (dataValue instanceof Byte || dataValue instanceof Byte[]) {
            return dataValue;
        }
        if (dataValue instanceof Short || dataValue instanceof Short[]) {
            return dataValue;
        }
        if (dataValue instanceof Integer || dataValue instanceof Integer[]) {
            return dataValue;
        }
        if (dataValue instanceof Long || dataValue instanceof Long[]) {
            return dataValue;
        }
        if (dataValue instanceof Double || dataValue instanceof Double[]) {
            return dataValue;
        }
        if (dataValue instanceof Float || dataValue instanceof Float[]) {
            return dataValue;
        }
        if (dataValue instanceof Boolean || dataValue instanceof Boolean[]) {
            return dataValue;
        }
        if (dataValue instanceof UUID) {
            return dataValue.toString();
        }
        if (dataValue instanceof UUID[]) {
            String[] result = new String[((UUID[]) dataValue).length];
            for (int i = 0; i < result.length; i++) {
                result[i] = ((UUID[]) dataValue)[i].toString();
            }

            return result;
        }

        // milo中的可转java类型
        if (dataValue instanceof ULong) {
            return ((ULong) dataValue).longValue();
        }
        if (dataValue instanceof ULong[]) {
            long[] result = new long[((ULong[]) dataValue).length];
            for (int i = 0; i < result.length; i++) {
                result[i] = ((ULong[]) dataValue)[i].longValue();
            }

            return result;
        }
        if (dataValue instanceof UShort) {
            return ((UShort) dataValue).intValue();
        }
        if (dataValue instanceof UInteger) {
            return ((UInteger) dataValue).longValue();
        }
        if (dataValue instanceof UInteger[]) {
            long[] result = new long[((UInteger[]) dataValue).length];
            for (int i = 0; i < result.length; i++) {
                result[i] = ((UInteger[]) dataValue)[i].longValue();
            }

            return result;
        }
        if (dataValue instanceof UShort) {
            return ((UShort) dataValue).intValue();
        }
        if (dataValue instanceof UShort[]) {
            int[] result = new int[((UShort[]) dataValue).length];
            for (int i = 0; i < result.length; i++) {
                result[i] = ((UShort[]) dataValue)[i].intValue();
            }

            return result;
        }
        if (dataValue instanceof UByte) {
            return ((UByte) dataValue).shortValue();
        }
        if (dataValue instanceof UByte[]) {
            short[] result = new short[((UByte[]) dataValue).length];
            for (int i = 0; i < result.length; i++) {
                result[i] = ((UByte[]) dataValue)[i].shortValue();
            }

            return result;
        }
        if (dataValue instanceof StatusCode) {
            return ((StatusCode) dataValue).getValue();
        }
        if (dataValue instanceof DateTime) {
            return ((DateTime) dataValue).getJavaTime();
        }
        if (dataValue instanceof DateTime[]) {
            long[] result = new long[((DateTime[]) dataValue).length];
            for (int i = 0; i < result.length; i++) {
                result[i] = ((DateTime[]) dataValue)[i].getJavaTime();
            }

            return result;
        }
        if (dataValue instanceof LocalizedText) {
            return ((LocalizedText) dataValue).getText();
        }
        if (dataValue instanceof LocalizedText[]) {
            String[] result = new String[((LocalizedText[]) dataValue).length];
            for (int i = 0; i < result.length; i++) {
                result[i] = ((LocalizedText[]) dataValue)[i].getText();
            }

            return result;
        }

        // milo中不支持转为json的数据类型
        if (dataValue instanceof NodeId) {
            return null;
        }
        if (dataValue instanceof ExtensionObject) {
            return null;
        }
        if (dataValue instanceof ExtensionObject[]) {
            return null;
        }
        if (dataValue instanceof ByteString) {
            return null;
        }
        if (dataValue instanceof XmlElement || dataValue instanceof XmlElement[]) {
            return null;
        }
        if (dataValue instanceof QualifiedName || dataValue instanceof QualifiedName[]) {
            return null;
        }


        return null;
    }
}
