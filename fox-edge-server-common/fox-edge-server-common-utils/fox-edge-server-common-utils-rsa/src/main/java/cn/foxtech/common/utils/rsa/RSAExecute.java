package cn.foxtech.common.utils.rsa;

public class RSAExecute {
    public static void main(String[] args) throws Exception {
        String filepath = args[0];
        String cpuid = args[1];


        System.out.println("---------------私钥签名过程------------------");
        String content = cpuid;
        String signstr = RSASignature.sign(content, RSAEncrypt.loadPrivateKeyByFile(filepath));
        System.out.println("CPUID：" + content);
        System.out.println("签名串：" + signstr);
        System.out.println();

        System.out.println("---------------公钥校验签名------------------");
        System.out.println("CPUID：" + content);
        System.out.println("签名串：" + signstr);

        System.out.println("验签结果：" + RSASignature.doCheck(content, signstr, RSAEncrypt.loadPublicKeyByFile(filepath)));
        System.out.println();

    }
}
