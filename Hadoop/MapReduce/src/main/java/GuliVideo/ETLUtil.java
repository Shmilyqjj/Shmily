package GuliVideo;

public class ETLUtil {
    public static String EtlStr(String s1){
        String[] words = s1.split("\t");

        //小于9个参数的为无效数据
        if(words.length < 9){
            return null;
        }

        //把视频类别中的空格去掉
        words[3] = words[3].replaceAll(" ","");

        //把相关视频的字段加在一起
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<words.length;i++){
            if(i<9){
                sb.append(words[i]).append("\t"); //每个参数之间用tab连接
            }else{
                if( i == words.length-1){//如果是最后一个参数 它是一个数组 如果只有一个参数就直接加上
                    sb.append(words[i]);
                }else{
                    sb.append(words[i]).append("&");//如果是多个参数，用&分隔开
                }
            }
        }
        return sb.toString();
    }
}
