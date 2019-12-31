package factory;

public class CountDownCreator<T> implements Creator<T> {
    private Creator<T> creator;

    private int count;

    public CountDownCreator(Creator<T> creator, int count){
        this.creator = creator;
        this.count = count;
    }

    public T create() {
        T result = null;
        if(count <= 0) result = null;
        else{
            result = creator.create();
            count--;
        }
        return result;
    }
}
