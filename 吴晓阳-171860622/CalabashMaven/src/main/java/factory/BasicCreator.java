package factory;

import java.lang.reflect.Constructor;

public class BasicCreator<T> implements Creator<T>{

    private final Class<? extends T> cls;

    public BasicCreator(Class<? extends T> cls){
        this.cls = cls;
    }

    public T create(){
        T t;
        try {
            t = cls.newInstance();
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
        return t;
    }
}
