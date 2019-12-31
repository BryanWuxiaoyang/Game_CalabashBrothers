package maps;

import javafx.scene.image.Image;

public enum GameBackground {
    BACKGROUND1 {
        Image image = null;

        @Override
        public Image getImage() {
            return new Image("background1.jpg");
        }

        @Override
        public Image getImage(double width, double height) {
            Image image = null;
            try{
                new Image("background1.jpg", width, height, false, true);
            }catch (Exception ignore){}
            return image;

        }
    },

    BACKGROUND2{
        Image image = null;

        @Override
        public Image getImage() {
            return new Image("background2.jpg");
        }

        @Override
        public Image getImage(double width, double height) {
            return new Image("background2.jpg", width, height, false, true);
        }
    };

    public abstract Image getImage();

    public abstract Image getImage(double width, double height);
}
