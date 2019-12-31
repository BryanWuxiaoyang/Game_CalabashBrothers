package factory;

import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RandomCreator<T> implements Creator<T> {
    private List<Creator<T>> creators = new ArrayList<Creator<T>>();

    public void addCreator(Creator<T> creator){
        creators.add(creator);
    }


    public T create() {
        Creator<T> creator = creators.get(Utils.getRandom(creators.size()));
        return creator.create();
    }
}
