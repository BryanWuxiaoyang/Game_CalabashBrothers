package factory;

import java.io.Serializable;

public interface Creator<T> extends Serializable {
    T create();
}
