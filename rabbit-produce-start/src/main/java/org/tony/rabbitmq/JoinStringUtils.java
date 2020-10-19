package org.tony.rabbitmq;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2020/10/14
 * @ProjectName kafka-example
 * @PackageName org.tony.rabbitmq
 */
public class JoinStringUtils {

    public static String joinString(String[] args, String s, int i) {
        int length = args.length;
        if(length == 0) return "";
        if(length <= i) return "";
        StringBuffer stringBuffer = new StringBuffer(args[i]);
        for(int j = i + 1;j < length;j++){
            stringBuffer.append(s).append(args[j]);
        }
        return stringBuffer.toString();
    }

}
