package maps;

import javafx.scene.image.Image;

public enum GridTexture {
    BLACK{
        @Override
        public Image getImage(){
            return new Image("black.jpg");
        }

        @Override
        public Image getImage(double width, double height){
            return new Image("black.jpg", width, height, false, true);
        }
    },
    STONE{
        @Override
        public Image getImage(){
            return new Image("stone.jpg");
        }

        @Override
        public Image getImage(double width, double height){
            return new Image("stone.jpg", width, height, false, true);
        }
    };

    public abstract Image getImage();

    public abstract Image getImage(double width, double height);
}
