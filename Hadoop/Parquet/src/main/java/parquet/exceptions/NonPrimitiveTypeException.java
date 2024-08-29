package parquet.exceptions;

/**
 * @author Shmily
 * @Description: 非基本类型 异常
 * @CreateTime: 2024/8/29 下午9:04
 */

public class NonPrimitiveTypeException extends RuntimeException {
    public NonPrimitiveTypeException(){
        super();
    }
    public NonPrimitiveTypeException(String str){
        super(str);
    }
}
