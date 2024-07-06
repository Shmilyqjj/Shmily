package MyGuliVideo;
/*
*这个类用于封装清洗数据的逻辑
*/
public class ETLUtil {
    public static String etlStr(String line){
        //1.过滤脏数据 小于9条
        String[] split = line.split("\t");
        if(split.length < 9){return null;}
        //2.将类别字段中空格去掉
        split[3].replaceAll(" ","");
        //3.替换关联视频的分隔符  \t变为&
        StringBuffer sb = new StringBuffer();

        int i = 0;
        for(;i<split.length;i++){
            if(i<9){
                if(i == split.length-1){
                    sb.append(split[i]);
                }else{
                    sb.append(split[i]).append("\t");
                }
            }else{
                if(i == split.length-1){
                    sb.append(split[i]);
                }else{
                    sb.append(split[i]).append("&");
                }
            }
        }
        return sb.toString();
    }

}
