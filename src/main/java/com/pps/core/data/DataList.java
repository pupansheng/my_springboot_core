package com.pps.core.data;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @author
 * @discription; 用于从数据库分页获取内容
 * @time 2021/1/8 13:59
 */
public class DataList<T> implements Iterator<List<T>> {
    /**
     * 数据源头
     */
    private Object dataSource;
    private Integer dataPageSize;
    private Object [] args;
    private int count=1;
    private  long size;
    private  Method method;
    public DataList(Object mapper,String methodName,int length,Class [] paramTypes,Object ... args){
        this.method= ReflectionUtils.findMethod(mapper.getClass(), methodName,paramTypes);
        this.dataPageSize=length;
        if(args==null){
            args=new Object[]{null};
        }
        this.args=args;
        this.dataSource=mapper;
        init();
    }

    public void init(){

        Object arg = args[0];
        Page<T> argAdd = (Page<T>) arg;
        argAdd.setSize(dataPageSize);
        args[0]=argAdd;
        Object o =ReflectionUtils.invokeMethod(method,dataSource,args);
        IPage<T> data=(IPage<T>)o;
        this.size=data.getPages();

    }

    @Override
    public boolean hasNext() {
        return count<=size;
    }

    @Override
    public List<T>  next() {
        Object arg = args[0];
        Page<T> argAdd = (Page<T>) arg;
        count++;
        argAdd.setCurrent(count);
        args[0]=argAdd;
        Object o = ReflectionUtils.invokeMethod(method, dataSource, args);
        IPage<T> data=(IPage<T>)o;
        return data.getRecords();
    }
}
